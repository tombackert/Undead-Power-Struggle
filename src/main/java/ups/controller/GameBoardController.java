package ups.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.model.*;
import ups.utils.*;
import ups.view.GameBoardView;
import ups.view.GameMenuView;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameBoardController {
    @FXML
    private StackPane boardPane;
    @FXML
    private Label currentPlayerLabel;
    @FXML
    private Label currentPlayerSettlementLabel;
    @FXML
    private Label currentTerrainLabel;
    @FXML
    private Button drawTerrainCardButton;
    @FXML
    private Button endTurnButton;
    @FXML
    private Button backToMenuButton;
    @FXML
    private ImageView currentPlayerImageView;
    @FXML
    private ImageView terrainImageView;
    @FXML
    private BorderPane mainBorderPane;

    private GameBoardView view;
    private GameBoard model;
    private final Map<Player, PlayerController> playerControllers = new HashMap<>();
    private Map<String, Image> terrainImages;
    private Player[] players;
    private int currentPlayerIndex = 0;
    private boolean terrainDrawnThisTurn = false;
    private final String[] terrainsBuildable = {"Gras", "Wald", "Wueste", "Blumen", "Canyon"};
    private static Stage gameStage;
    private String[] playerNames;
    private Color[] playerColors;
    private boolean[] isAIPlayer;
    public static final Logger logger = Logger.getLogger(GameBoardController.class.getName());
    private ResourceBundle bundle;
    private static GameBoardController instance;
    private String currentTerrain;
    private int settlementsPerTurn = 3; // Default value
    private int settlementsCount = 40; // Default value
    private boolean gameEnded = false;
    private boolean endgameMode = false;
    private Player firstPlayerOutOfSettlements = null;
    private boolean isPaused = false;
    private Task<Void> currentAITask = null;
    private int remainingAIMoves = 0; // Store the number of remaining AI moves
    private GameServer gameServer;
    private GameClient gameClient;

    /**
     * Default constructor for FXML loading
     */
    public GameBoardController() {
    }

    public void setGameServer(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public static void setInstance(GameBoardController controller) {
        instance = controller;
    }

    /**
     * Sets the number of settlements per turn.
     *
     * @param settlementsPerTurn the number of settlements per turn
     */
    public void setSettlementsPerTurn(int settlementsPerTurn) {
        this.settlementsPerTurn = settlementsPerTurn;
    }

    /**
     * Returns the number of settlements per turn.
     *
     * @return the number of settlements per turn
     */
    public int getSettlementsPerTurn() {
        return settlementsPerTurn;
    }

    /**
     * Sets the total number of settlements.
     *
     * @param settlementsCount the total number of settlements
     */
    public void setSettlementsCount(int settlementsCount) {
        this.settlementsCount = settlementsCount;
    }

    /**
     * Sets the players' names, colors, and AI status.
     *
     * @param playerNames  the player names
     * @param playerColors the player colors
     * @param isAIPlayer   the AI player status
     */
    public void setPlayers(String[] playerNames, Color[] playerColors, boolean[] isAIPlayer) {
        this.playerNames = playerNames;
        this.playerColors = playerColors;
        this.isAIPlayer = isAIPlayer;

        if (playerNames != null && playerColors != null && isAIPlayer != null) {
            initializePlayers();
            updateCurrentPlayerInfo();
        } else {
            logger.log(Level.SEVERE, "Player names, colors or AI status are null.");
        }
    }

    public void setPlayersForNetwork(String[] playerNames, Color[] playerColors, boolean[] isOneself, int sPerTurn, int sAmount) {
        this.playerNames = playerNames;
        this.playerColors = playerColors;
        this.isAIPlayer = new boolean[playerNames.length];
        //Init players
        this.players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            if (isOneself[i]) this.players[i] = new SelfOnlinePlayer(playerNames[i], playerColors[i], sPerTurn, sAmount);
            else this.players[i] = new OnlinePlayer(playerNames[i], playerColors[i], sPerTurn, sAmount);
            playerControllers.put(this.players[i], new PlayerController(this.players[i], model, this) {
                @Override
                protected void notifyCanEndTurn() {
                    endTurnButton.setDisable(false);
                }
            });
        }
        updateCurrentPlayerInfo();
    }

    /**
     * Ändert den Hintergrund basierend auf dem aktuellen Thema.
     *
     * @param theme Das aktuelle Thema
     */
    public void switchBackground(String theme) {
        mainBorderPane.getStyleClass().removeIf(styleClass -> styleClass.startsWith("background-pane-"));
        if ("dark".equals(theme)) {
            mainBorderPane.getStyleClass().add("background-pane-dark");
        } else {
            mainBorderPane.getStyleClass().add("background-pane-light");
        }
    }

    /**
     * Initializes the players.
     */
    private void initializePlayers() {
        players = new Player[playerNames.length];
        for (int i = 0; i < playerNames.length; i++) {
            Player player;
            if (isAIPlayer[i]) {
                player = new AIPlayer(playerNames[i], playerColors[i], settlementsPerTurn, settlementsCount);
            } else {
                player = new Player(playerNames[i], playerColors[i], settlementsPerTurn, settlementsCount);
            }
            players[i] = player;
            playerControllers.put(player, new PlayerController(player, model, this) {
                @Override
                protected void notifyCanEndTurn() {
                    endTurnButton.setDisable(false);
                }
            });
        }
    }

    /**
     * Updates the current player information.
     */
    private void updateCurrentPlayerInfo() {
        updateCurrentPlayerLabel();
        updateCurrentPlayerSettlementLabel();
    }

    /**
     * Updates the current player label and image.
     */
    private void updateCurrentPlayerLabel() {
        if (currentPlayerLabel == null || currentPlayerImageView == null) {
            logger.log(Level.SEVERE, "currentPlayerLabel or currentPlayerImageView is null.");
            return;
        }

        if (players == null || players.length == 0) {
            currentPlayerLabel.setText("Aktueller Spieler: Unbekannt");
            currentPlayerImageView.setImage(null); // Setze das Bild auf null
        } else {
            Player currentPlayer = players[currentPlayerIndex];
            if (currentPlayer == null) {
                currentPlayerLabel.setText("Aktueller Spieler: Unbekannt");
                currentPlayerImageView.setImage(null); // Setze das Bild auf null
                logger.log(Level.SEVERE, "Current player is null.");
            } else {
                currentPlayerLabel.setText("Aktueller Spieler: " + currentPlayer.getName());
                String colorName = ColorMapping.getStringFromColor(currentPlayer.getColor()).toLowerCase();
                ImageView playerImageView = view.loadHouseImage(colorName);
                if (playerImageView != null) {
                    currentPlayerImageView.setImage(playerImageView.getImage()); // Setze das Bild basierend auf der Spielerfarbe

                    // Passe die Größe des Bildes an
                    double imageSize = 40; // Beispielgröße, passe diese nach Bedarf an
                    currentPlayerImageView.setFitWidth(imageSize);
                    currentPlayerImageView.setFitHeight(imageSize);
                    currentPlayerImageView.setPreserveRatio(true);
                } else {
                    currentPlayerImageView.setImage(null); // Falls kein Bild gefunden wurde
                }
            }
        }
    }

    /**
     * Updates the current player's settlement label.
     */
    private void updateCurrentPlayerSettlementLabel() {
        if (currentPlayerSettlementLabel == null) {
            logger.log(Level.SEVERE, "currentPlayerSettlementLabel is null.");
            return;
        }

        if (players == null || players.length == 0) {
            currentPlayerSettlementLabel.setText("Verfügbare Siedlungen: Unbekannt");
        } else {
            Player currentPlayer = players[currentPlayerIndex];
            if (currentPlayer == null) {
                currentPlayerSettlementLabel.setText("Verfügbare Siedlungen: Unbekannt");
                logger.log(Level.SEVERE, "Current player is null.");
            } else {
                currentPlayerSettlementLabel.setText("Verfügbare Siedlungen: " + currentPlayer.getRemainingSettlements());
            }
        }
        updateTexts();
    }

    /**
     * Sets the game stage.
     *
     * @param gameStageFromMenu the game stage
     */
    public static void setGameStage(Stage gameStageFromMenu) {
        gameStage = gameStageFromMenu;
        System.out.println("Setting game stage: " + gameStage);
    }

    /**
     * Sets the resource bundle.
     *
     * @param bundle the resource bundle
     */
    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        System.out.println("ResourceBundle set in GameBoardController.");
        updateTexts();
    }

    /**
     * Refreshes the texts based on the current locale.
     */
    public void refreshTexts() {
        Locale currentLocale = LanguageSettings.getCurrentLocale();
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateTexts();
    }

    /**
     * Updates the texts for the game board.
     */
    private void updateTexts() {
        if (bundle == null) {
            logger.log(Level.SEVERE, "bundle is null.");
            return;
        }

        setLabelText(currentPlayerLabel, "current_player", players != null && currentPlayerIndex >= 0 && currentPlayerIndex < players.length ? players[currentPlayerIndex].getName() : "Unknown");
        setLabelText(currentPlayerSettlementLabel, "available_settlements", players != null && currentPlayerIndex >= 0 && currentPlayerIndex < players.length ? String.valueOf(players[currentPlayerIndex].getRemainingSettlements()) : "Unknown");
        updateTerrainLabel();
        setButtonText(drawTerrainCardButton, "draw_terrain_card");
        setButtonText(endTurnButton, "end_turn");
        setButtonText(backToMenuButton, "back_to_menu");
    }

    /**
     * Sets the text of the given label.
     *
     * @param label  the label
     * @param key    the key
     * @param suffix the suffix
     */
    private void setLabelText(Label label, String key, String suffix) {
        if (label != null) {
            String text = bundle.getString(key);
            if (suffix != null) {
                text += ": " + suffix;
            }
            label.setText(text);
        } else {
            logger.log(Level.SEVERE, key + " label is null.");
        }
    }

    /**
     * Sets the text of the given button.
     *
     * @param button the button
     * @param key    the key
     */
    private void setButtonText(Button button, String key) {
        if (button != null) {
            button.setText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " button is null.");
        }
    }

    /**
     * Initializes the game board controller.
     */
    @FXML
    public void initialize() {
        instance = this;
        try {
            initializeModel();
            view = new GameBoardView(model);
            view.setController(this);
            boardPane.getChildren().add(view);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Initialisierung fehlgeschlagen.", e);
        }
        loadTerrainImages();
        updateTexts();
        switchBackground(MenuController.theme == 0 ? "light" : "dark"); // Hintergrund basierend auf dem aktuellen Thema setzen
    }

    /**
     * Refreshes the background
     */
    public void refreshBackground() {
        switchBackground(MenuController.theme == 0 ? "light" : "dark");
        loadTerrainImages();
        updateTerrainLabel();
    }

    /**
     * Returns the instance of the game board controller.
     *
     * @return the instance of the game board controller
     */
    public static GameBoardController getInstance() {
        return instance;
    }

    /**
     * Initializes the game board model.
     *
     * @throws IOException if an I/O error occurs
     */
    private void initializeModel() throws IOException {
        model = new GameBoard(20, 20, Arrays.asList("Fischer", "Bergleute", "Arbeiter"));
        int[][] positions = {{0, 0}, {0, 10}, {10, 0}, {10, 10}};
        for (int i = 0; i < positions.length; i++) {
            model.initialize(i, positions[i][0], positions[i][1]);
        }
    }

    /**
     * loads terrainimage
     *
     */
    private void loadTerrainImages() {
        if (MenuController.theme == 0) {
            terrainImages = new HashMap<>();
            terrainImages.put("Gras", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Gras.png"))));
            terrainImages.put("Wald", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Wald.png"))));
            terrainImages.put("Wueste", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Wüste.png"))));
            terrainImages.put("Blumen", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Blumen.png"))));
            terrainImages.put("Canyon", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Canyon.png"))));
        } else {
            terrainImages = new HashMap<>();
            terrainImages.put("Gras", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Gras_blood.png"))));
            terrainImages.put("Wald", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Wald_blood.png"))));
            terrainImages.put("Wueste", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Wüste_blood.png"))));
            terrainImages.put("Blumen", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Blumen_blood.png"))));
            terrainImages.put("Canyon", new Image(Objects.requireNonNull(getClass().getResourceAsStream("/location-cards/Canyon_blood.png"))));

        }
    }

    /**
     * Returns the game board model.
     *
     * @return the game board model
     */
    public GameBoard getModel() {
        return model;
    }

    /**
     * Returns the current terrain.
     *
     * @return the current terrain
     */
    public String getCurrentTerrain() {
        return currentTerrain;
    }

    /**
     * Handles the hexagon click event.
     *
     * @param hexGroup the hexagon group
     * @param row      the row
     * @param col      the column
     */
    @FXML
    public void handleHexagonClick(Group hexGroup, int row, int col) {
        if (gameEnded) return; // Exit if the game has ended

        Player currentPlayer = players[currentPlayerIndex];

        if (currentPlayer.getRemainingSettlements() == 0) {
            AlertManager.showAlert("alert.no_settlements_left");
            return;
        }

        try {
            if (tryPlaceSettlement(row, col)) {
                view.addHouseToHexagon(hexGroup, players[currentPlayerIndex].getColor());
                updateCurrentPlayerSettlementLabel();
                updateTerrainLabel();
                if (currentPlayer.isSelfOnlinePlayer) {
                    ClientGameConnection.setMessageToClient("MOVE:" + Integer.toString(row) + ":" + Integer.toString(col));
                }

                if ((playerControllers.get(players[currentPlayerIndex]).canEndTurn() || players[currentPlayerIndex].getRemainingSettlements() == 0) && !currentPlayer.isExternalPlayer) {
                    endTurnButton.setDisable(false);
                }
                if (checkAllPlayersNoSettlements()) {
                    endGame();
                }
            }
        } catch (InvalidPlacementException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Attempts to place a settlement on the given row and column.
     *
     * @param row the row
     * @param col the column
     * @return true if the settlement was placed successfully, false otherwise
     * @throws InvalidPlacementException if the placement is invalid
     */
    private boolean tryPlaceSettlement(int row, int col) throws InvalidPlacementException {
        if (!terrainDrawnThisTurn) {
            AlertManager.showAlert("alert.no_terrain_drawn");
            return false;
        }

        Player currentPlayer = players[currentPlayerIndex];
        PlayerController playerController = playerControllers.get(currentPlayer);

        // Überprüfe, ob das Terrain der gezogenen Geländekarte entspricht
        String terrainType = model.getTerrainType(row, col);
        if (!terrainType.equals(currentPlayer.getCurrentTerrainCard())) {
            AlertManager.showAlert("alert.wrong_terrain_card");
            return false;
        }

        if (currentPlayer.canPlaceSettlement() && model.isNotOccupied(row, col)) {
            // Überprüfe, ob die Siedlung neben einer bestehenden Siedlung des Spielers gebaut werden kann
            if (!isNextToExistingSettlement(currentPlayer, row, col) && canPlaceNextToExistingSettlement(currentPlayer)) {
                AlertManager.showAlert("alert.must_place_next_to_existing");
                return false;
            }

            playerController.placeSettlement(currentPlayer, row, col);
            return true;
        }
        return false;
    }

    private boolean isNextToExistingSettlement(Player player, int row, int col) {
        // Überprüfe die Nachbarfelder
        int[][] neighbors = player.getHexagonalNeighbors(row, col);
        for (int[] neighbor : neighbors) {
            int x = neighbor[0];
            int y = neighbor[1];
            if (x >= 0 && x < model.boardSizeX && y >= 0 && y < model.boardSizeY && model.getOccupation(x, y) == ColorMapping.getIntFromColor(player.getColor())) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceNextToExistingSettlement(Player player) {
        for (int i = 0; i < model.boardSizeX; i++) {
            for (int j = 0; j < model.boardSizeY; j++) {
                if (model.getOccupation(i, j) == ColorMapping.getIntFromColor(player.getColor())) {
                    int[][] neighbors = player.getHexagonalNeighbors(i, j);
                    for (int[] neighbor : neighbors) {
                        int x = neighbor[0];
                        int y = neighbor[1];
                        if (x >= 0 && x < model.boardSizeX && y >= 0 && y < model.boardSizeY && model.isNotOccupied(x, y) && model.getTerrainType(x, y).equals(player.getCurrentTerrainCard())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Handles the AI player click event.
     *
     * @param row the row
     * @param col the column
     */
    @FXML
    public void handleAIClick(int row, int col) {
        Group hexGroup = getHexGroup(row, col);
        handleHexagonClick(hexGroup, row, col);
    }

    /**
     * Makes a move for the AI player.
     *
     * @param aiPlayer the AI player
     */
    private void makeAIMove(AIPlayer aiPlayer) {
        if (gameEnded || isPaused) return; // Exit if the game has ended or is paused

        disableHumanInteraction();
        remainingAIMoves = settlementsPerTurn; // Set the remaining AI moves

        currentAITask = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (!terrainDrawnThisTurn) {
                        switchTerrain();
                    }
                });
                try {
                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    if (isCancelled()) {
                        return null;
                    }
                    logger.log(Level.SEVERE, "AI move interrupted", e);
                }
                processAIMoves(aiPlayer, remainingAIMoves);
                return null;
            }
        };

        Thread thread = new Thread(currentAITask);
        thread.setDaemon(true);
        thread.start();
    }

    private void processAIMoves(AIPlayer aiPlayer, int movesToProcess) {
        currentAITask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    for (int i = 0; i < movesToProcess; i++) {
                        if (isPaused || isCancelled()) {
                            remainingAIMoves = movesToProcess - i; // Update remaining moves
                            return null;
                        }
                        Platform.runLater(() -> {
                            if (aiPlayer.getRemainingSettlements() > 0) {
                                int[] move = aiPlayer.findBestMove(getModel(), getCurrentTerrain());
                                aiPlayer.addPendingMove(move);
                                handleAIClick(move[0], move[1]);
                            }
                        });
                        try {
                            Thread.sleep(750);
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                remainingAIMoves = movesToProcess - i; // Update remaining moves
                                return null;
                            }
                            logger.log(Level.SEVERE, "AI move interrupted", e);
                        }
                        if (aiPlayer.getRemainingSettlements() <= 0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error processing AI moves", e);
                }

                Platform.runLater(() -> {
                    if (!checkAllPlayersNoSettlements() && !gameEnded) {
                        enableHumanInteraction();
                        endTurn();
                    }
                });
                return null;
            }
        };

        Thread thread = new Thread(currentAITask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Updates the board for the given player.
     *
     * @param player the player
     */
    private void updateBoardForPlayerGeneric(Player player) {
        System.out.println("Update board for this player:");
        player.printPlayer();
        for (int i = 0; i < model.boardSizeX; i++) {
            for (int j = 0; j < model.boardSizeY; j++) {
                if (model.getOccupation(i, j) == ColorMapping.getIntFromColor(player.getColor())) {
                    Group hexGroup = getHexGroup(i, j);
                    view.addHouseToHexagon(hexGroup, player.getColor());
                }
            }
        }
    }

    /**
     * Updates the board for the given AI player.
     *
     * @param aiPlayer the AI player
     */
    public void updateBoardForAI(AIPlayer aiPlayer) {
        updateBoardForPlayerGeneric(aiPlayer);
    }

    /**
     * Updates the board for the given player.
     *
     * @param player the player
     */
    public void updateBoardForPlayer(Player player) {
        updateBoardForPlayerGeneric(player);
    }


    /**
     * Updates the terrain label.
     */
    public void updateTerrainLabel() {
        if (currentTerrain != null && terrainImages.containsKey(currentTerrain)) {
            terrainImageView.setImage(terrainImages.get(currentTerrain));
            currentTerrainLabel.setText(bundle.getString("terrain_card") + ": " + bundle.getString(currentTerrain));
        } else {
            terrainImageView.setImage(null);
            currentTerrainLabel.setText(bundle.getString("terrain_card") + ": ");
        }
    }
    private void doItInThread() {
        ClientGameConnection.setMessageToGame(null);
        new Thread(() -> handleOnlinePlayer()).start();
    }
    /**
     * Switches the current player.
     */
    @FXML
    public void switchPlayer() {
        if (gameEnded) return; // Exit if the game has ended

        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;

        // Überprüfe, ob der erste Spieler ohne Siedlungen wieder an der Reihe ist
        if (endgameMode && players[currentPlayerIndex] == firstPlayerOutOfSettlements) {
            endGame();
            return;
        }

        updateCurrentPlayerInfo();
        updateTexts();
        terrainDrawnThisTurn = false; // Flag zurücksetzen
        currentTerrain = null; // Reset current terrain
        updateTerrainLabel(); // Clear terrain label
        playerControllers.get(players[currentPlayerIndex]).resetTurn();
        endTurnButton.setDisable(true);
        drawTerrainCardButton.setDisable(false);

        if (players[currentPlayerIndex] instanceof AIPlayer) {
            makeAIMove((AIPlayer) players[currentPlayerIndex]);
        } 
        else if (players[currentPlayerIndex].isExternalPlayer) {
            doItInThread();
        }
        else {
            // Es ist ein menschlicher Spieler, aktiviere UI-Interaktionen für menschliche Eingaben
            enableHumanInteraction();
        }
        if (players[currentPlayerIndex].isExternalPlayer) System.out.println("New player is external player!!!!");
        else System.out.println("New player is own online player!!!");
    }

    /**
     * Returns the hexagon group for the given row and column.
     *
     * @param row the row
     * @param col the column
     * @return the hexagon group
     */
    private Group getHexGroup(int row, int col) {
        return (Group) view.getChildren().get(row * model.boardSizeY + col);
    }

    /**
     * Draws a terrain card for the current player.
     */
    @FXML
    public void switchTerrain() {
        if (players == null || players.length == 0) {
            logger.log(Level.SEVERE, "Players are not initialized.");
            return;
        }

        Player currentPlayer = players[currentPlayerIndex];
        if (currentPlayer == null) {
            logger.log(Level.SEVERE, "Current player is null.");
            return;
        }

        if (!terrainDrawnThisTurn) {
            currentTerrain = terrainsBuildable[new Random().nextInt(terrainsBuildable.length)];
            currentPlayer.drawTerrainCard(currentTerrain);
            updateTerrainLabel();
            terrainDrawnThisTurn = true;
            drawTerrainCardButton.setDisable(true);
            notifyTerrainCardDrawn(currentPlayer.getName(), currentTerrain);
            if (currentPlayer.isSelfOnlinePlayer) ClientGameConnection.setMessageToClient("TERRAINCARD:" + currentTerrain);
        }
    }

    /**
     * Ends the turn of the current player.
     *
     * @return true if the turn was ended successfully, false otherwise
     */
    @FXML
    public boolean endTurn() {
        Player currentPlayer = players[currentPlayerIndex];
        PlayerController playerController = playerControllers.get(currentPlayer);

        if (playerController.canEndTurn() || currentPlayer.getRemainingSettlements() == 0) {
            // Überprüfe, ob der aktuelle Spieler keine Siedlungen mehr hat
            if (currentPlayer.isSelfOnlinePlayer) ClientGameConnection.setMessageToClient("ENDTURN");
            if (currentPlayer.getRemainingSettlements() == 0) {
                if (!endgameMode) {
                    endgameMode = true;
                    firstPlayerOutOfSettlements = currentPlayer;
                }
            }
            switchPlayer();
            terrainDrawnThisTurn = false;
            return true;
        } else {
            System.out.println("Du musst " + settlementsPerTurn + " Siedlungen setzen, bevor du deinen Zug beenden kannst.");
            return false;
        }
    }

    /**
     * Handles the return to menu button click.
     */
    @FXML
    public void handleReturnToMenu() {
        pauseGame();  // Pause the game before returning to the menu
        System.out.println("Handling return to menu: gameStage = " + gameStage);
        if (gameStage != null) {
            GameMenuView.showMenu();
            gameStage.close();
        } else {
            logger.log(Level.SEVERE, "gameStage is null.");
        }
    }

    public void pauseGame() {
        isPaused = true;
        if (currentAITask != null) {
            currentAITask.cancel();
        }
        disableHumanInteraction();
    }

    public void resumeGame() {
        isPaused = false;
        if (players[currentPlayerIndex] instanceof AIPlayer) {
            makeAIMove((AIPlayer) players[currentPlayerIndex]);
        } else {
            enableHumanInteraction();
        }
    }

    /**
     * Ends the game and shows the highscore view.
     */
    @FXML
    public void endGame() {
        if (gameEnded) return; // Prevent multiple executions
        gameEnded = true; // Set the flag to indicate the game has ended

        // Clear pending moves for all AI players
        for (Player player : players) {
            if (player instanceof AIPlayer) {
                ((AIPlayer) player).clearPendingMoves();
            }
        }

        List<Player> sortedPlayers = playerControllers.keySet().stream()
                .sorted((p1, p2) -> Integer.compare(p2.evaluateGameboard(model), p1.evaluateGameboard(model)))
                .collect(Collectors.toList());

        Player winner = sortedPlayers.get(0); // Der Spieler mit dem meisten Gold ist der Gewinner

        saveHighscore(sortedPlayers, model);
        AlertManager.showWinner("alert.winner_is", winner.getName(), winner.evaluateGameboard(model)); // Pass name and gold amount
        Platform.runLater(() -> {
            GameMenuView.showMenu();
            gameStage.close();
            GameMenuController.clientThreadIsRunning = false;//Stoppe Server und Client sobald spiel endet
            GameMenuController.serverThreadIsRunning = false;
        });
    }

    /**
     * Saves the highscore for the given players and model.
     *
     * @param sortedPlayers the sorted list of players
     * @param model         the game board model
     */
    private void saveHighscore(List<Player> sortedPlayers, GameBoard model) {
        HighscoreManager highscoreManager = new HighscoreManager();
        for (Player player : sortedPlayers) {
            highscoreManager.saveHighscore(new Highscore(player.getName(), player.evaluateGameboard(model)));
        }
    }

    /**
     * Disables human interaction during the AI player's turn.
     */
    private void disableHumanInteraction() {
        drawTerrainCardButton.setDisable(true);
        endTurnButton.setDisable(true);
        boardPane.setDisable(true); // Deaktiviert die gesamte Spielfläche
    }

    /**
     * Enables human interaction after the AI player's turn.
     */
    private void enableHumanInteraction() {
        drawTerrainCardButton.setDisable(false);
        endTurnButton.setDisable(true);
        boardPane.setDisable(false); // Aktiviert die gesamte Spielfläche
    }

    public boolean checkAvailableBuildableFields(int requiredSettlements) {
        int buildableFields = 0;
        for (int i = 0; i < model.boardSizeX; i++) {
            for (int j = 0; j < model.boardSizeY; j++) {
                String terrain = model.getTerrainType(i, j);
                // Zählen Sie nur die bebaubaren Felder
                if (!terrain.equals("Wasser") && !terrain.equals("Berg") && !terrain.equals("GoldCastle") && !terrain.equals("SilverCastle")) {
                    buildableFields++;
                }
            }
        }
        // Überprüfen, ob genügend Felder für die benötigten Siedlungen vorhanden sind
        System.out.println("Buildable fields: " + buildableFields);
        return buildableFields >= requiredSettlements;
    }

    /**
     * Checks if all players have no remaining settlements.
     *
     * @return true if all players have no remaining settlements, false otherwise
     */
    public boolean checkAllPlayersNoSettlements() {
        for (Player player : players) {
            System.out.println("Remaining settlements for " + player.getName() + ": " + player.getRemainingSettlements());
            if (player.getRemainingSettlements() > 0) {
                return false;
            }
        }
        return true;
    }

    public void startAiGame() {
        if (players[currentPlayerIndex] instanceof AIPlayer) {
            makeAIMove((AIPlayer) players[currentPlayerIndex]);
        } else {
            System.out.println("Der aktuelle Spieler ist kein KI-Spieler.");
            // Fehlerbehandlung oder Logik zur Handhabung dieses Falls
        }
    }

    public void startOnlineGame() {
        System.out.println("Wait for other players");
        doItInThread();
    }

    /**
     * Sets the selected cards for the model.
     *
     * @param selectedCards The list of selected cards to set.
     */
    public void setSelectedCards(List<String> selectedCards) {
        for (String c : selectedCards) {
            System.out.println(c);
        }
        model.setSelectedCards(selectedCards);
    }

    private void notifyTerrainCardDrawn(String playerName, String terrainType) {
        if (GameMenuController.getGameServer() != null) {
            GameMenuController.getGameServer().broadcastTerrainCardDrawn(playerName, terrainType);
            System.out.println("Broadcasting terrain card drawn message: " + playerName + ", " + terrainType);
        }
        else if (GameMenuController.getGameClient() != null) {
            GameMenuController.getGameClient().handleTerrainCardDrawnMessage(playerName, terrainType);
            System.out.println("Handling terrain card drawn message: " + playerName + ", " + terrainType);
        }
        else {
            System.out.println("No server or client found.");
        }
    }

    public void handleTerrainCardDrawnMessage(TerrainCardDrawnMessage message) {
        System.out.println("Updating terrain card for player: " + message.getPlayerName() + " with terrain: " + message.getTerrainType()); // Debug output
        currentTerrain = message.getTerrainType();
        updateTerrainLabel();
        terrainDrawnThisTurn = true;
        drawTerrainCardButton.setDisable(true);
    }
    
    public void handleOnlinePlayer() {
        disableHumanInteraction();
        String message;
        while (true) {
            message = ClientGameConnection.getMessageToGame();
            if (message == null) continue;
            if (message.startsWith("MOVE")) {
                String[] moveStr = message.split(":");
                int x = Integer.parseInt(moveStr[1]);
                int y = Integer.parseInt(moveStr[2]);
                System.out.println("Online Spieler setzt auf Feld: " + Integer.toString(x) + "," + Integer.toString(y));
                final int fx = x; // final variables to use inside the lambda
                final int fy = y;
                Platform.runLater(() -> handleHexagonClick(getHexGroup(fx, fy), fx, fy));
            }
            else if (message.equals("ENDTURN") && terrainDrawnThisTurn) {
                Platform.runLater(() -> endTurn());
                return;
            }
            else if (message.startsWith("TERRAINCARD")) {
                Player currentPlayer = players[currentPlayerIndex];
                currentTerrain = message.split(":")[1];
                currentPlayer.drawTerrainCard(currentTerrain);
                updateTerrainLabel();
                terrainDrawnThisTurn = true;
                drawTerrainCardButton.setDisable(true);
                notifyTerrainCardDrawn(currentPlayer.getName(), currentTerrain);
            }
        }
    }
    public void parseGameBoardFromNetwork(String b) {
        model.parseGameBoardFromNetwork(b);
    }
}
