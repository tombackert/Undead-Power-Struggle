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
    public void makeMove(GameBoardController controller, GameBoard board) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < 3; i++) {
                    int[] move = findBestMove(controller.getModel(), controller.getCurrentTerrain());
                    numberOfVillages--;
                    villageCoordinates[40 - numberOfVillages] = move;
                    System.out.println("AI move: " + move[0] + ", " + move[1] + " with gold " + evaluatePosition(controller.getModel(), move[0], move[1]));
                    if (move != null) {
                        Platform.runLater(() -> {
                            controller.handleAIClick(move[0], move[1]);
                            board.occupiedBy[move[0]][move[1]] = color;
                            controller.updateBoardForAI(AIPlayer.this);
                        });
                        try {
                            Thread.sleep(1000); // 1 Sekunde Pause zwischen dem Setzen der Häuser
                        } catch (InterruptedException e) {
                            logger.log(Level.SEVERE, "AI move interrupted", e);
                        }
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
    public int[] findBestMove1(GameBoard board, String currentTerrain) {
        int bestGold = -1;
        int[] bestMove = null;

        // First move, can place anywhere
        if (super.numberOfVillages == 40) { // Assuming this means it's the first move
            System.out.println("First move, place anywhere that maximizes gold");
            for (int x = 0; x < board.boardSizeX; x++) {
                for (int y = 0; y < board.boardSizeY; y++) {
                    if (this.canPlaceVillage(board, x, y, currentTerrain)) {
                        int gold = this.evaluatePosition(board, x, y);
                        //System.out.printf("Position (%d, %d) has gold %d%n", x, y, gold);
                        if (gold > bestGold) {
                            bestGold = gold;
                            bestMove = new int[]{x, y};
                        }
                    }
                }
            }
        } else {
            // Subsequent moves, should place next to existing villages if possible
            System.out.println("Subsequent move, place next to existing villages if possible");
            for (int i = 0; i < 40 - this.numberOfVillages; i++) {
                int x = this.villageCoordinates[i][0];
                int y = this.villageCoordinates[i][1];
                //System.out.println("Village: " + Integer.toString(x) + ", " + Integer.toString(y));
                board.resetNeighbourCoordinates(x, y);
                for (int j = 0; j < 6; j++) {
                    int xNeighbour = board.coordinatesOfLastNeighbours[j][0];
                    int yNeighbour = board.coordinatesOfLastNeighbours[j][1];
                    System.out.println(1);
                    if (this.canPlaceVillage(board, xNeighbour, yNeighbour, currentTerrain)) {
                        System.out.println(2);
                        int gold = this.evaluatePosition(board, xNeighbour, yNeighbour);
                        System.out.printf("Position (%d, %d) has gold %d%n", x, y, gold);
                        if (gold > bestGold) {
                            System.out.println(3);
                            bestGold = gold;
                            bestMove = new int[]{xNeighbour, yNeighbour};
                        }
                    }
                }
                
            }

            // If no adjacent valid move found, place anywhere that maximizes gold
            if (bestMove == null) {
                System.out.println("No adjacent valid move found, place anywhere that maximizes gold");
                for (int x = 0; x < board.boardSizeX; x++) {
                    for (int y = 0; y < board.boardSizeY; y++) {
                        if (this.canPlaceVillage(board, x, y, currentTerrain)) {
                            int gold = this.evaluatePosition(board, x, y);
                            //System.out.printf("Position (%d, %d) has gold %d%n", x, y, gold);
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
















    public static String itS(int a) {
        return Integer.toString(a);
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
                int a = -1 + ((y % 2) * 2); // a = -1 für gerade y-Koordinaten, a = 1 für ungerade y-Koordinaten
                int[][] potentialNeighbours = {
                        {x - 1, y},
                        {x + 1, y},
                        {x, y - 1}, 
                        {x - 1, y + a}, 
                        {x, y + 1},
                        {x + 1, y + a}
                };
                System.out.println("\n\nPosition: " + itS(x) + ", " + itS(y) + "; a= " + itS(a));
                for (int[] pN : potentialNeighbours) System.out.println("Hopefully a Neighbour: " + itS(pN[0]) + ", " + itS(pN[1]) + "\n");
                for (int[] neighbourPosition : potentialNeighbours) {
                    int xNeighbour = neighbourPosition[0];
                    int yNeighbour = neighbourPosition[1];
                    if (xNeighbour >= 0 && xNeighbour < 20 && yNeighbour >= 0 && yNeighbour < 20) {
                        if (!this.isNeighborOfExistingVillage(xNeighbour, yNeighbour)) System.out.println("Not a Neighbour: " + itS(xNeighbour) + ", " + itS(yNeighbour));
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
    private boolean isNeighborOfExistingVillage(int x, int y) {
        // Schleife durch alle Dorf-Koordinaten
        for (int[] village : this.villageCoordinates) {
            // Überspringe den Eintrag, wenn die Koordinaten (0, 0) sind
            if (village[0] == 0 && village[1] == 0) continue;

            // Speichere die x- und y-Koordinaten des bestehenden Dorfes in vx und vy
            int vx = village[0];
            int vy = village[1];

            // Überprüfe, ob (x, y) angrenzend zu (vx, vy) ist, unter Berücksichtigung der Spielfeldgrenzen
            if ((vx - 1 >= 0 && vx - 1 == x && vy == y) || // links
                    (vx + 1 < 20 && vx + 1 == x && vy == y) || // rechts
                    (vy - 1 >= 0 && vx == x && vy - 1 == y) || // oben
                    (vy + 1 < 20 && vx == x && vy + 1 == y)) { // unten
                // Wenn eine der Bedingungen erfüllt ist, ist (x, y) ein Nachbar und die Methode gibt true zurück
                return true;
            }
        }
        // Wenn keine Nachbarschaft gefunden wurde, gibt die Methode false zurück
        return false;
    }

}