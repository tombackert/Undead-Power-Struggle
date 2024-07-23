package ups.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 * The AIPlayer class represents an AI player in the game.
 */
public class AIPlayer extends Player {
    private final Queue<int[]> pendingMoves = new LinkedList<>();

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
     * Returns a random move of all moves that maximise gold.
     *
     * @param board the game board
     * @param currentTerrain the current terrain type
     * @return the best greedy move
     */
    public int[] findBestMove(GameBoard board, String currentTerrain) {
        int bestMovesStart = 0;
        int bestMovesEnd = 0;
        int[][] bestMoves = new int[board.boardSizeX * board.boardSizeY][2]; //Array viel größer als nötig: es würde reichen die
        int[] bestMove = null;                                               //Anzahl von der Felder mit dem currentTerrain zu nehmen
        int bestGold = Integer.MIN_VALUE;
        int gold;
        int[][] neighbours;
        int x;
        int y;
        Random r = new Random();
        //iterate over all placed villages and check to find neighbour position to place on (first move will skip this loop)
        for (int i = 0; i < this.numberOfVillages - this.remainingSettlements; i++) {
            x = this.villageCoordinates[i][0];
            y = this.villageCoordinates[i][1];
            neighbours = this.getHexagonalNeighbors(x, y);
            for (int[] n : neighbours) {
                if (!(this.canPlaceVillage(board, n[0], n[1], currentTerrain))) continue;
                gold = evaluatePosition(board, n[0], n[1]);
                if (gold == bestGold) {
                    bestMoves[bestMovesEnd] = n;
                    bestMovesEnd++;
                }
                if (gold > bestGold) {
                    bestMovesStart = bestMovesEnd;
                    bestMoves[bestMovesEnd] = n;
                    bestMovesEnd++;
                    bestGold = gold;
                }
            }
        }
        //chose random move out of highest rated moves
        if (bestMovesEnd > bestMovesStart) bestMove = bestMoves[bestMovesStart + r.nextInt(bestMovesEnd - bestMovesStart)];
        if (bestMove != null) {
            return bestMove;
        }
        //If village can't be placed next to other village, place anywhere while maxinmizing gold (this also applies to the first move)
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if (!(this.canPlaceVillage(board, i, j, currentTerrain))) continue;
                gold = evaluatePosition(board, i, j);
                if (gold == bestGold) {
                    bestMoves[bestMovesEnd][0] = i;
                    bestMoves[bestMovesEnd][1] = j;
                    bestMovesEnd++;
                }
                if (gold > bestGold) {
                    bestMovesStart = bestMovesEnd;
                    bestMoves[bestMovesEnd][0] = i;
                    bestMoves[bestMovesEnd][1] = j;
                    bestMovesEnd++;
                    bestGold = gold;
                }
            }
        }
        //chose random move out of highest rated moves
        if (bestMovesEnd > bestMovesStart) bestMove = bestMoves[bestMovesStart + r.nextInt(bestMovesEnd - bestMovesStart)];
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
        return (0 <= x && x < board.boardSizeX && 0 <= y && y < board.boardSizeY
                && board.isNotOccupied(x, y) && board.getTerrainType(x, y).equals(currentTerrain));
    }

    public void addPendingMove(int[] move) {
        pendingMoves.add(move);
    }

    public void clearPendingMoves() {
        pendingMoves.clear();
    }
}
