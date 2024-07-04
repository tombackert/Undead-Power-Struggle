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
        // Check if the game should end
        if (controller.checkAllPlayersNoSettlements()) {
            controller.endGame();
        }

        int[] move = findBestMove(controller.getModel(), controller.getCurrentTerrain());
        villageCoordinates[this.numberOfVillages - getRemainingSettlements()] = move;
        Platform.runLater(() -> {
            controller.handleAIClick(move[0], move[1]);
            board.occupiedBy[move[0]][move[1]] = this.color;
            controller.updateBoardForAI(AIPlayer.this);
        });
        System.out.println("AI move: " + move[0] + ", " + move[1] + ". AI Player now has gold=" + evaluateGameboard(controller.getModel()));
    }

    /**
     * Finds the best move for the AI player
     * 
     * @param board the game board
     * @param currentTerrain the current terrain type
     * @return the best greedy move
     */
    public int[] findBestMove(GameBoard board, String currentTerrain) {
        int[] bestMove = null;
        int bestGold = Integer.MIN_VALUE;
        int gold;
        int[][] neighbours;
        int x;
        int y;
        //iterate over all placed villages and check to find neighbour position to place on (first move will skip this loop)
        for (int i = 0; i < this.numberOfVillages - this.remainingSettlements; i++) {
            x = this.villageCoordinates[i][0];
            y = this.villageCoordinates[i][1];
            neighbours = this.getHexagonalNeighbors(x, y);
            for (int[] n : neighbours) {
                if (this.canPlaceVillage(board, n[0], n[1], currentTerrain)) {
                    gold = evaluatePosition(board, n[0], n[1]);
                    if (gold <= bestGold) continue;
                    bestGold = gold;
                    bestMove = n;
                }
            }
        }
        if (bestMove != null) {
            System.out.println("Found move as Neighbour move");
            return bestMove;
        };
        //If village can't be placed next to other village, place anywhere while maxinmizing gold (this also applies to the first move)
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if (!(this.canPlaceVillage(board, i, j, currentTerrain))) continue;
                gold = evaluatePosition(board, i, j);
                if (gold <= bestGold) continue;
                bestGold = gold;
                bestMove = new int[]{i, j};
            }
        }
        System.out.println("Found move as random move");
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
}