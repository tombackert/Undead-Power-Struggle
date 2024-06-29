package ups.model;

import javafx.application.Platform;
import ups.controller.GameBoardController;
import javafx.scene.paint.Color;

/**
 * The AIPlayer class represents an AI player in the game.
 */
public class AIPlayer extends Player {
    /**
     * Constructs a new AIPlayer with the given name and color.
     *
     * @param name  the name of the player
     * @param color the color of the player
     */
    public AIPlayer(String name, Color color, int settlementsPerTurn, int settlementsCount) {
        super(name, color, settlementsPerTurn, settlementsCount);
        //System.out.println("AI Player created with settlements per turn: " + settlementsPerTurn); // Debug statement
    }

    /**
     * Makes a move for the AI player.
     *
     * @param controller the game board controller
     */
    public void makeMove(GameBoardController controller, GameBoard board) {
        int[] move = findBestMove(controller.getModel(), controller.getCurrentTerrain());
        numberOfVillages--;
        villageCoordinates[getRemainingSettlements() - 1] = move;
        System.out.println("AI move: " + move[0] + ", " + move[1] + " with gold " + evaluatePosition(controller.getModel(), move[0], move[1]));
        Platform.runLater(() -> {
            controller.handleAIClick(move[0], move[1]);
            board.occupiedBy[move[0]][move[1]] = color;
            controller.updateBoardForAI(AIPlayer.this);
        });
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

        // Erster Zug, kann überall platziert werden
        if (super.numberOfVillages == 40) { // Angenommen, das bedeutet, dass es der erste Zug ist
            System.out.println("Erster Zug, platziere überall, wo der Goldwert maximiert wird");
            for (int x = 0; x < board.boardSizeX; x++) {
                for (int y = 0; y < board.boardSizeY; y++) {
                    if (this.canPlaceVillage(board, x, y, currentTerrain)) {
                        int gold = this.evaluatePosition(board, x, y);
                        if (gold > bestGold) {
                            bestGold = gold;
                            bestMove = new int[]{x, y};
                        }
                    }
                }
            }
        } else {
            // Nachfolgende Züge, sollten neben bestehenden Dörfern platziert werden, wenn möglich
            System.out.println("Nachfolgender Zug, platziere neben bestehenden Dörfern, wenn möglich");
            for (int[] villageCoordinate : this.villageCoordinates) {
                if (villageCoordinate[0] == 0 && villageCoordinate[1] == 0) continue;
                int x = villageCoordinate[0];
                int y = villageCoordinate[1];

                // Überprüfe die Nachbarpositionen, die innerhalb der Spielfeldgrenzen liegen
                int[][] potentialNeighbours = this.getHexagonalNeighbors(x, y);

                //System.out.println("\n\nPosition: " + x + ", " + y);
                //for (int[] pN : potentialNeighbours) System.out.println("Hopefully a Neighbour: " + pN[0] + ", " + pN[1] + "\n");
                for (int[] neighbourPosition : potentialNeighbours) {
                    int xNeighbour = neighbourPosition[0];
                    int yNeighbour = neighbourPosition[1];
                    if (xNeighbour >= 0 && xNeighbour < 20 && yNeighbour >= 0 && yNeighbour < 20) {
                        if (this.canPlaceVillage(board, xNeighbour, yNeighbour, currentTerrain)) {
                            int gold = this.evaluatePosition(board, xNeighbour, yNeighbour);
                            if (gold > bestGold) {
                                bestGold = gold;
                                bestMove = neighbourPosition;
                            }
                        }
                    }
                }
            }

            // Wenn kein gültiger Nachbarzug gefunden wurde, platziere irgendwo, wo der Goldwert maximiert wird
            if (bestMove == null) {
                System.out.println("Kein gültiger Nachbarzug gefunden, platziere irgendwo, wo der Goldwert maximiert wird");
                for (int x = 0; x < board.boardSizeX; x++) {
                    for (int y = 0; y < board.boardSizeY; y++) {
                        if (this.canPlaceVillage(board, x, y, currentTerrain)) {
                            int gold = this.evaluatePosition(board, x, y);
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
        System.out.println("Bester Zug: " + bestMove[0] + ", " + bestMove[1] + " mit Gold " + bestGold);
        return bestMove;
    }

    /**
     * Checks if a given position is a neighbor of an existing village.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @return true if the position is a neighbor of an existing village, false otherwise
     */
    private boolean canPlaceVillage(GameBoard board, int x, int y, String currentTerrain) {
        return board.isNotOccupied(x, y) && board.getTerrainType(x, y).equals(currentTerrain);
    }
}