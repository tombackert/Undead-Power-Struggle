package ups.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
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

    private void updateCurrentPlayerInfo() {
        updateCurrentPlayerLabel();
        updateCurrentPlayerSettlementLabel();
    }

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

    public static void setGameStage(Stage gameStageFromMenu) {
        gameStage = gameStageFromMenu;
        System.out.println("Setting game stage: " + gameStage);
    }

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        updateTexts();
    }

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

    private void setButtonText(Button button, String key) {
        if (button != null) {
            button.setText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " button is null.");
        }
    }

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

    public static GameBoardController getInstance() {
        return instance;
    }

    private void initializeModel() throws IOException {
        model = new GameBoard(20, 20, Arrays.asList("Fischer", "Bergleute", "Arbeiter"));
        int[][] positions = {{0, 0}, {0, 10}, {10, 0}, {10, 10}};
        for (int i = 0; i < positions.length; i++) {
            model.initialize(i, positions[i][0], positions[i][1]);
        }
    }

    public GameBoard getModel() {
        return model;
    }

    public String getCurrentTerrain() {
        return currentTerrain;
    }

    public void handleHexagonClick(Group hexGroup, int row, int col) {
        try {
            Player currentPlayer = players[currentPlayerIndex];
            PlayerController playerController = playerControllers.get(currentPlayer);

            if (currentPlayer.canPlaceSettlement() && model.isNotOccupied(row, col)) {
                playerController.placeSettlement(currentPlayer, row, col);
                view.addHouseToHexagon(hexGroup, currentPlayer.getColor());
                updateCurrentPlayerSettlementLabel();
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

    public void handleAIClick(int row, int col) {
        Group hexGroup = getHexGroup(row, col);
        handleHexagonClick(hexGroup, row, col);
    }

    private void updateTerrainLabel() {
        setLabelText(currentTerrainLabel, "terrain_card", currentTerrain != null ? bundle.getString(currentTerrain) : null);
    }

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

    private void makeAIMove(AIPlayer aiPlayer) {
        // Ziehe eine Geländekarte
        switchTerrain();
        System.out.println("AI zieht Geländekarte: " + currentTerrain);
        // Mache den Zug des AI
        aiPlayer.makeMove(this);
        System.out.println("AI macht Zug.");
        updateBoardForAI(aiPlayer);
    }

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

    private Group getHexGroup(int row, int col) {
        // Assuming that the hexagons are stored in a 2D array or a similar data structure.
        return (Group) view.getChildren().get(row * model.boardSizeY + col);
    }

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

    public void checkGameEnd() {
        if (isGameFinished()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Spiel beendet");
                alert.setHeaderText(null);
                alert.setContentText("Das Spiel ist beendet!");
                alert.showAndWait();
            });
        }
    }
}
