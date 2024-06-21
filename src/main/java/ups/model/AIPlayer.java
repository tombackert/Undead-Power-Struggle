package ups.model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ups.controller.GameBoardController;
import javafx.scene.paint.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AIPlayer extends Player {
    private static final Logger logger = Logger.getLogger(AIPlayer.class.getName());

    public AIPlayer(String name, Color color) {
        super(name, color);
    }

    // Method to make a move
    public void makeMove(GameBoardController controller) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < 3; i++) {
                    int[] move = findBestMove(controller.getModel(), controller.getCurrentTerrain());
                    if (move != null) {
                        Platform.runLater(() -> {
                            controller.handleAIClick(move[0], move[1]);
                            controller.updateBoardForAI(AIPlayer.this);
                            controller.checkGameEnd();
                        });
                        try {
                            Thread.sleep(1000); // 1 Sekunde Pause zwischen dem Setzen der Häuser
                        } catch (InterruptedException e) {
                            logger.log(Level.SEVERE, "AI move interrupted", e);
                        }
                    } else {
                        break; // Keine gültigen Züge mehr
                    }
                }
                Platform.runLater(() -> {
                    if (controller.endTurn()) {
                        System.out.println("Zug beendet.");
                    } else {
                        System.out.println("Fehler: Zug konnte nicht beendet werden.");
                    }
                });
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    // Method to find the best move based on gold value
    public int[] findBestMove(GameBoard board, String currentTerrain) {
        int bestGold = -1;
        int[] bestMove = null;

        if (super.numberOfVillages == 40) {  // Assuming this means it's the first move
            // First move, can place anywhere
            for (int x = 0; x < board.boardSizeX; x++) {
                for (int y = 0; y < board.boardSizeY; y++) {
                    if (this.canPlaceVillage(board, x, y, currentTerrain)) {
                        int gold = this.evaluatePosition(board, x, y);
                        System.out.printf("Position (%d, %d) has gold %d%n", x, y, gold);
                        if (gold > bestGold) {
                            bestGold = gold;
                            bestMove = new int[]{x, y};
                        }
                    }
                }
            }
        } else {
            // Subsequent moves, should place next to existing villages if possible
            for (int[] villageCoordinate : this.villageCoordinates) {
                if (villageCoordinate[0] == 0 && villageCoordinate[1] == 0) {
                    continue;  // Skip uninitialized coordinates
                }
                int x = villageCoordinate[0];
                int y = villageCoordinate[1];
                String[] neighbours = board.getNeighbourTerrain(x, y);
                for (int j = 0; j < neighbours.length; j++) {
                    int[] neighbourPosition = board.coordinatesOfLastNeighbours[j];
                    int xNeighbour = neighbourPosition[0];
                    int yNeighbour = neighbourPosition[1];
                    if (this.canPlaceVillage(board, xNeighbour, yNeighbour)) {
                        int gold = this.evaluatePosition(board, xNeighbour, yNeighbour);
                        System.out.printf("Position (%d, %d) has gold %d%n", xNeighbour, yNeighbour, gold);
                        if (gold > bestGold) {
                            bestGold = gold;
                            bestMove = neighbourPosition;
                        }
                    }
                }
            }

            // If no adjacent valid move found, place anywhere that maximizes gold
            if (bestMove == null) {
                for (int x = 0; x < board.boardSizeX; x++) {
                    for (int y = 0; y < board.boardSizeY; y++) {
                        if (this.canPlaceVillage(board, x, y)) {
                            int gold = this.evaluatePosition(board, x, y);
                            System.out.printf("Position (%d, %d) has gold %d%n", x, y, gold);
                            if (gold > bestGold) {
                                bestGold = gold;
                                bestMove = new int[]{x, y};
                            }
                        }
                    }
                }
            }
        }

        return bestMove;
    }

    private boolean canPlaceVillage(GameBoard board, int x, int y, String currentTerrain) {
        // Add logic to check if a village can be placed at the coordinates (x, y)
        return board.isNotOccupied(x, y) && board.getTerrainType(x, y).equals(currentTerrain);
    }

    // Overloaded method to check if a village can be placed without terrain constraint
    public boolean canPlaceVillage(GameBoard board, int x, int y) {
        return board.isNotOccupied(x, y);
    }

}
