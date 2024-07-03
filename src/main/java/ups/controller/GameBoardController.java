package ups.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.model.GameBoard;
import ups.model.InvalidPlacementException;
import ups.model.Player;
import ups.model.AIPlayer;
import ups.utils.HighscoreManager;
import ups.view.GameBoardView;
import ups.view.GameMenuView;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import ups.utils.LanguageSettings;
import java.util.ResourceBundle;
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
    // Add a flag to indicate if the game has ended
    private int settlementsPerTurn = 3; // Default value
    private int settlementsCount = 40; // Default value

    public GameBoardController() {
        // Default constructor for FXML loading
    }

    public GameBoardController(GameBoard model, List<Player> players) {
        this.model = model;
        this.players = players.toArray(new Player[0]);
    }

    public void setSettlementsPerTurn(int settlementsPerTurn) {
        this.settlementsPerTurn = settlementsPerTurn;
        //System.out.println("Settlements per turn set to: " + settlementsPerTurn); // Debug statement
    }

    public int getSettlementsPerTurn() {
        return settlementsPerTurn;
    }

    public void setSettlementsCount(int settlementsCount) {
        this.settlementsCount = settlementsCount;
        //System.out.println("Total settlements set to: " + settlementsCount); // Debug statement
    }

    /**
     * Sets the players.
     *
     * @param playerNames the player names
     * @param playerColors the player colors
     * @param isAIPlayer the AI player status
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
        // System.out.println("Players initialized with settlements per turn: " + settlementsPerTurn); // Debug statement
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
     * Refreshes the texts of the game board.
     */
    public void refreshTexts() {
        Locale currentLocale = LanguageSettings.getCurrentLocale();
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateTexts();
    }

    /**
     * Updates the texts of the game board.
     */
    private void updateTexts() {
        if (bundle == null) {
            logger.log(Level.SEVERE, "bundle is null.");
            return;
        }

        setLabelText(currentPlayerLabel, "current_player", players[currentPlayerIndex].getName());
        setLabelText(currentPlayerSettlementLabel, "available_settlements", String.valueOf(players[currentPlayerIndex].getRemainingSettlements()));
        //setLabelText(currentTerrainLabel, "terrain_card", null);
        updateTerrainLabel();
        setButtonText(drawTerrainCardButton, "draw_terrain_card");
        setButtonText(endTurnButton, "end_turn");
        setButtonText(backToMenuButton, "back_to_menu");
    }

    /**
     * Sets the text of the given label to the value of the given key.
     *
     * @param label the label
     * @param key the key
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
     * Sets the text of the given button to the value of the given key.
     *
     * @param button the button
     * @param key the key
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
     * @throws IOException if an error occurs
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
     * Returns the current terrain type.
     *
     * @return the current terrain type
     */
    public String getCurrentTerrain() {
        return currentTerrain;
    }

    /**
     * Handles the click on a hexagon.
     *
     * @param hexGroup the group of the hexagon
     * @param row the row of the hexagon
     * @param col the column of the hexagon
     */
    public void handleHexagonClick(Group hexGroup, int row, int col) {
        try {
            Player currentPlayer = players[currentPlayerIndex];
            PlayerController playerController = playerControllers.get(currentPlayer);

            // Calculate gold for the clicked position
            int goldPerTurn = currentPlayer.calculateGoldForPosition(model, row, col);

            if (currentPlayer.canPlaceSettlement() && model.isNotOccupied(row, col)) {
                playerController.placeSettlement(currentPlayer, row, col);
                view.addHouseToHexagon(hexGroup, currentPlayer.getColor());
                updateCurrentPlayerSettlementLabel();

                // Recalculate the total gold after placing the settlement
                int goldInTotal = currentPlayer.calculateGold(model);
                System.out.println("Player: " + currentPlayer.getName() + " placed settlement on row: " + row + " and column: " + col + " with move gold: " + goldPerTurn + " and new total gold: " + goldInTotal);

                if (playerController.canEndTurn()) {
                    endTurnButton.setDisable(false);
                }
            } else {
                System.out.println("Maximale Anzahl von Siedlungen für diesen Zug erreicht oder Feld ist bereits besetzt.");
            }
        } catch (InvalidPlacementException e) {
            System.out.println(e.getMessage());
        }
        updateTerrainLabel();
    }

    /**
     * Handles the click on a hexagon by the AI player.
     *
     * @param row the row of the hexagon
     * @param col the column of the hexagon
     */
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
        // Draw a terrain card
        switchTerrain();
        System.out.println("AI zieht Geländekarte: " + currentTerrain);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < settlementsPerTurn; i++) {
                    Platform.runLater(() -> {
                        aiPlayer.makeMove(GameBoardController.this, model);
                        updateBoardForAI(aiPlayer);
                    });
                    try {
                        Thread.sleep(1500); // 2-second delay between moves
                    } catch (InterruptedException e) {
                        logger.log(Level.SEVERE, "AI move interrupted", e);
                    }
                }
                Platform.runLater(() -> {
                    System.out.println("AI moves completed");
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
     * Updates the game board for the AI player.
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
     * Updates the game board for the AI player.
     *
     * @param player the AI player
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
        terrainDrawnThisTurn = false;
        currentTerrain = null; // Reset current terrain
        updateTerrainLabel(); // Clear terrain label
        playerControllers.get(players[currentPlayerIndex]).resetTurn();
        endTurnButton.setDisable(true);
        drawTerrainCardButton.setDisable(false);

        // Wenn der nächste Spieler ein Computergegner ist, mache einen Zug
        if (players[currentPlayerIndex] instanceof AIPlayer) {
            makeAIMove((AIPlayer) players[currentPlayerIndex]);
        }
    }

    /**
     * Returns the group of the hexagon at the given row and column.
     *
     * @param row the row
     * @param col the column
     * @return the group of the hexagon
     */
    private Group getHexGroup(int row, int col) {
        // Assuming that the hexagons are stored in a 2D array or a similar data structure.
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
            return true;
        } else {
            System.out.println("Du musst 3 Siedlungen setzen, bevor du deinen Zug beenden kannst.");
            return false;
        }
    }

    /**
     * Handles the return to menu button.
     */
    @FXML
    public void handleReturnToMenu() {
        System.out.println("Handling return to menu: gameStage = " + gameStage);
        if (gameStage != null) {
            GameMenuView.showMenu();
            gameStage.close();
            
        } else {
            logger.log(Level.SEVERE, "gameStage is null.");
        }
    }

    @FXML
    public void endGame() {
        List<Player> sortedPlayers = playerControllers.keySet().stream()
                .sorted((p1, p2) -> Integer.compare(p2.calculateGold(model), p1.calculateGold(model)))
                .collect(Collectors.toList());

        StringBuilder results = new StringBuilder();
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);
            results.append(String.format("%d. Platz: %s mit %d Gold%n", i + 1, player.getName(), player.calculateGold(model)));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION, results.toString(), ButtonType.OK);
        alert.setHeaderText("Spiel beendet!");
        alert.setTitle("Spielergebnisse");
        alert.showAndWait();

        saveHighscore(sortedPlayers);
        GameMenuView.showMenu();  // Zurück zum Hauptmenü
        gameStage.close(); // Schließe das Spiel
    }

    private void saveHighscore(List<Player> sortedPlayers) {
        HighscoreManager highscoreManager = new HighscoreManager();
        highscoreManager.saveHighscore(sortedPlayers, model);
    }
}
