package ups.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.model.GameBoard;
import ups.model.InvalidPlacementException;
import ups.model.Player;
import ups.model.AIPlayer;
import ups.view.GameBoardView;
import ups.view.GameMenuView;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import ups.utils.LanguageSettings;
import java.util.ResourceBundle;

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
                player = new AIPlayer(playerNames[i], playerColors[i]);
            } else {
                player = new Player(playerNames[i], playerColors[i]);
            }
            players[i] = player;
            playerControllers.put(player, new PlayerController(player, model) {
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
            int goldPerTurn = calculateGoldForPosition(currentPlayer, row, col);

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
     * Calculates the gold for the given position.
     *
     * @param player the player
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the gold value
     */
    public int calculateGoldForPosition(Player player, int x, int y) {
        return player.evaluatePosition(model, x, y);
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
     * Makes a move for the AI player.
     *
     * @param aiPlayer the AI player
     */
    private void makeAIMove(AIPlayer aiPlayer) {
        // Ziehe eine Geländekarte
        switchTerrain();
        System.out.println("AI zieht Geländekarte: " + currentTerrain);
        // Mache den Zug des AI
        aiPlayer.makeMove(this, this.model);
        System.out.println("AI macht Zug.");
        updateBoardForAI(aiPlayer);
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
            gameStage.hide();
            GameMenuView.showMenu();
        } else {
            logger.log(Level.SEVERE, "gameStage is null.");
        }
    }

    /**
     * Refreshes the texts of the game board.
     */
    public void refreshTexts() {
        Locale currentLocale = LanguageSettings.getCurrentLocale();
        bundle = ResourceBundle.getBundle("messages", currentLocale);
        updateTexts();
    }

    private boolean isGameFinished() {
        // Logik zur Überprüfung, ob das Spiel beendet ist
        // Zum Beispiel: Überprüfen, ob alle Siedlungen gesetzt wurden
        for (Player player : players) {
            if (player.getRemainingSettlements() > 0) {
                return false;
            }
        }
        return true;
    }
}
