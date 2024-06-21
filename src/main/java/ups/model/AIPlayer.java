package ups.model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ups.controller.GameBoardController;
import javafx.scene.paint.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The AIPlayer class represents an AI player in the game.
 */
public class AIPlayer extends Player {
    private static final Logger logger = Logger.getLogger(AIPlayer.class.getName());
    /**
     * Constructs a new AIPlayer with the given name and color.
     *
     * @param name  the name of the player
     * @param color the color of the player
     */
    public AIPlayer(String name, Color color) {
        super(name, color);
    }

    /**
     * Makes a move for the AI player.
     *
     * @param controller the game board controller
     */
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

    /**
     * Finds the best move for the AI player.
     *
     * @param board the game board
     * @param currentTerrain the current terrain type
     * @return the best move
     */
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
                    if (this.canPlaceVillage(board, xNeighbour, yNeighbour, currentTerrain)) {
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
            }
        }
        assert bestMove != null;
        System.out.println("Best move: " + bestMove[0] + ", " + bestMove[1] + " with gold " + bestGold);
        return bestMove;
    }

    /**
     * Evaluates the gold value of a position on the game board.
     *
     * @param board the game board
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return the gold value of the position
     */
    private boolean canPlaceVillage(GameBoard board, int x, int y, String currentTerrain) {
        return board.isNotOccupied(x, y) && board.getTerrainType(x, y).equals(currentTerrain);
    }
}
