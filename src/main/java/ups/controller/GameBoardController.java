// GameBoardController.java
package ups.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.model.GameBoard;
import ups.model.InvalidPlacementException;
import ups.model.Player;
import ups.view.GameBoardView;
import ups.view.GameMenuView;
import java.util.Arrays;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private GameBoardView view;
    private GameBoard model;
    private final Map<Player, PlayerController> playerControllers = new HashMap<>();
    private Player[] players;
    private int currentPlayerIndex = 0;
    private boolean terrainDrawnThisTurn = false;
    private final String[] terrainsBuildable = {"Gras", "Wald", "Wueste", "Blumen", "Canyon"};
    private Stage gameStage;
    private String[] playerNames;
    private Color[] playerColors;
    private static final Logger logger = Logger.getLogger(GameBoardController.class.getName());

    public void setPlayers(String[] playerNames, Color[] playerColors) {
        this.playerNames = playerNames;
        this.playerColors = playerColors;

        if (playerNames != null && playerColors != null) {
            players = new Player[playerNames.length];
            for (int i = 0; i < playerNames.length; i++) {
                Player player = new Player(playerNames[i], playerColors[i]);
                players[i] = player;
                playerControllers.put(player, new PlayerController(player, model) {
                    @Override
                    protected void notifyCanEndTurn() {
                        endTurnButton.setDisable(false);
                    }
                });
            }
            updateCurrentPlayerLabel();
            updateCurrentPlayerSettlementLabel();
        } else {
            logger.log(Level.SEVERE, "Player names or colors are null.");
        }
    }

    public void start(Stage primaryStage) throws IOException {
        this.gameStage = primaryStage;
        initializeGame();
    }

    @FXML
    private void initializeGame() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameBoardView.fxml"));
        Parent root = loader.load();

        GameBoardController controller = loader.getController();
        controller.setPlayers(playerNames, playerColors);

        Scene scene = new Scene(root, 1024, 768);
        gameStage.setScene(scene);
        gameStage.setTitle("Undead Power Struggle");
        gameStage.show();
    }

    @FXML
    public void initialize() {
        // Initialize model and view
        try {
            initializeModel();
            view = new GameBoardView(model);
            view.setController(this);
            boardPane.getChildren().add(view);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Initialisierung fehlgeschlagen.", e);
        }

        if (currentTerrainLabel != null) {
            currentTerrainLabel.setText("Geländekarte:\nZiehe erst eine Geländekarte!");
        } else {
            logger.log(Level.SEVERE, "currentTerrainLabel is null.");
        }
    }

    private void initializeModel() throws IOException {
        model = new GameBoard(20, 20, Arrays.asList("Fischer", "Bergleute", "Arbeiter"));
        int[][] positions = {{0, 0}, {0, 10}, {10, 0}, {10, 10}};
        for (int i = 0; i < positions.length; i++) {
            model.initialize(i, positions[i][0], positions[i][1]);
        }
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
    }

    @FXML
    public void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        updateCurrentPlayerLabel();
        updateCurrentPlayerSettlementLabel();
        currentTerrainLabel.setText("Geländekarte:\nZiehe erst eine Geländekarte!");
        terrainDrawnThisTurn = false;
        playerControllers.get(players[currentPlayerIndex]).resetTurn();
        endTurnButton.setDisable(true);
        drawTerrainCardButton.setDisable(false);
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
            currentPlayerSettlementLabel.setText("Noch verfügbare Siedlungen: Unbekannt");
        } else {
            Player currentPlayer = players[currentPlayerIndex];
            if (currentPlayer == null) {
                currentPlayerSettlementLabel.setText("Noch verfügbare Siedlungen: Unbekannt");
                logger.log(Level.SEVERE, "Current player is null.");
            } else {
                currentPlayerSettlementLabel.setText("Noch verfügbare Siedlungen: " + currentPlayer.getRemainingSettlements());
            }
        }
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
            String currentTerrain = terrainsBuildable[new Random().nextInt(terrainsBuildable.length)];
            currentPlayer.drawTerrainCard(currentTerrain);
            if (currentTerrainLabel != null) {
                currentTerrainLabel.setText("Geländekarte:\n" + currentTerrain);
            } else {
                logger.log(Level.SEVERE, "currentTerrainLabel is null.");
            }
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
        GameMenuView.showMenu();
        ((Stage) boardPane.getScene().getWindow()).close();
    }
}
