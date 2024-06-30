package ups.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.model.*;
import ups.utils.AlertManager;
import ups.utils.HighscoreManager;
import ups.view.GameBoardView;
import ups.view.GameMenuView;
import ups.utils.LanguageSettings;
import ups.view.HighscoreView;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The GameBoardController class is responsible for controlling the game board.
 */
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

    private GameBoardView view;
    private GameBoard model;
    private final Map<Player, PlayerController> playerControllers = new HashMap<>();
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

    /**
     * Default constructor for FXML loading
     */
    public GameBoardController() {
    }

    /**
     * Constructs a new GameBoardController with the given model and players.
     *
     * @param model   the game board model
     * @param players the list of players
     */
    public GameBoardController(GameBoard model, List<Player> players) {
        this.model = model;
        this.players = players.toArray(new Player[0]);
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
     * Sets the total number of settlements.

     */
    public int getSettlementsCount() {
        return settlementsCount;
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
        System.out.println("Players initialized with settlements per turn: " + settlementsPerTurn);
    }

    /**
     * Updates the current player information.
     */
    private void updateCurrentPlayerInfo() {
        updateCurrentPlayerLabel();
        updateCurrentPlayerSettlementLabel();
    }

    /**
     * Updates the current player label.
     */
    private void updateCurrentPlayerLabel() {
        if (currentPlayerLabel == null) {
            logger.log(Level.SEVERE, "currentPlayerLabel is null.");
            return;
        }

        if (players == null || players.length == 0) {
            currentPlayerLabel.setText("Aktueller Spieler: Unbekannt");
        } else {
            Player currentPlayer = players[currentPlayerIndex];
            if (currentPlayer == null) {
                currentPlayerLabel.setText("Aktueller Spieler: Unbekannt");
                logger.log(Level.SEVERE, "Current player is null.");
            } else {
                currentPlayerLabel.setText("Aktueller Spieler: " + currentPlayer.getName());
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

        setLabelText(currentPlayerLabel, "current_player", players[currentPlayerIndex].getName());
        setLabelText(currentPlayerSettlementLabel, "available_settlements", String.valueOf(players[currentPlayerIndex].getRemainingSettlements()));
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
        updateTexts();
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
    public void handleHexagonClick(Group hexGroup, int row, int col) {
        try {
            if (tryPlaceSettlement(row, col)) {
                view.addHouseToHexagon(hexGroup, players[currentPlayerIndex].getColor());
                updateCurrentPlayerSettlementLabel();
                updateTerrainLabel();

                if (playerControllers.get(players[currentPlayerIndex]).canEndTurn()) {
                    endTurnButton.setDisable(false);
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

        if (currentPlayer.canPlaceSettlement() && model.isNotOccupied(row, col)) {
            playerController.placeSettlement(currentPlayer, row, col);
            return true;
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
        disableHumanInteraction();
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (!terrainDrawnThisTurn) {
                        switchTerrain();
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "AI move interrupted", e);
                }
                for (int i = 0; i < settlementsPerTurn; i++) {
                    Platform.runLater(() -> {
                        aiPlayer.makeMove(GameBoardController.this, model);
                        updateBoardForAI(aiPlayer);
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "AI move interrupted", e);
                    }
                }
                Platform.runLater(() -> {
                    System.out.println("AI moves completed");
                    enableHumanInteraction();
                    endTurn();
                });
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Updates the board for the given AI player.
     *
     * @param aiPlayer the AI player
     */
    public void updateBoardForAI(AIPlayer aiPlayer) {
        for (int i = 0; i < model.boardSizeX; i++) {
            for (int j = 0; j < model.boardSizeY; j++) {
                if (model.getOccupation(i, j) == ColorMapping.getIntFromColor(aiPlayer.getColor())) {
                    Group hexGroup = getHexGroup(i, j);
                    view.addHouseToHexagon(hexGroup, aiPlayer.getColor());
                }
            }
        }
    }

    /**
     * Updates the board for the given player.
     *
     * @param player the player
     */
    public void updateBoardForPlayer(Player player) {
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
     * Updates the terrain label.
     */
    private void updateTerrainLabel() {
        setLabelText(currentTerrainLabel, "terrain_card", currentTerrain != null ? bundle.getString(currentTerrain) : null);
    }

    /**
     * Switches the current player.
     */
    @FXML
    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
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
        } else {
            // Es ist ein menschlicher Spieler, aktiviere UI-Interaktionen für menschliche Eingaben
            enableHumanInteraction();
        }
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
        }
    }

    /**
     * Ends the turn of the current player.
     *
     * @return true if the turn was ended successfully, false otherwise
     */
    @FXML
    public boolean endTurn() {
        if (playerControllers.get(players[currentPlayerIndex]).canEndTurn()) {
            switchPlayer();
            terrainDrawnThisTurn = false;
            return true;
        } else {
            System.out.println("Du musst 3 Siedlungen setzen, bevor du deinen Zug beenden kannst.");
            return false;
        }
    }

    /**
     * Handles the return to menu button click.
     */
    @FXML
    public void handleReturnToMenu() {
        System.out.println("Handling return to menu: gameStage = " + gameStage);
        if (gameStage != null) {
            gameStage.close();
            GameMenuView.showMenu();
        } else {
            logger.log(Level.SEVERE, "gameStage is null.");
        }
    }

    /**
     * Ends the game and shows the highscore view.
     */
    @FXML
    public void endGame() {
        List<Player> sortedPlayers = playerControllers.keySet().stream()
                .sorted((p1, p2) -> Integer.compare(p2.calculateGold(model), p1.calculateGold(model)))
                .collect(Collectors.toList());

        Player winner = sortedPlayers.get(0); // Der Spieler mit dem meisten Gold ist der Gewinner

        saveHighscore(sortedPlayers, model);

        AlertManager.showWinner("alert.winner_is", winner.getName(), winner.calculateGold(model)); // Übergeben Sie Name und Goldmenge
        GameMenuView.showMenu();
        gameStage.close();
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
            highscoreManager.saveHighscore(new Highscore(player.getName(), player.calculateGold(model)));
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

    public void startAiGame() {
        if (players[currentPlayerIndex] instanceof AIPlayer) {
            makeAIMove((AIPlayer) players[currentPlayerIndex]);
        } else {
            System.out.println("Der aktuelle Spieler ist kein KI-Spieler.");
            // Fehlerbehandlung oder Logik zur Handhabung dieses Falls
        }
    }
}
