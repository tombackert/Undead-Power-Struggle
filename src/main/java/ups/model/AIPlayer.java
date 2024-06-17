package ups.model;

import javafx.scene.paint.Color;


public class AIPlayer extends Player {
    public AIPlayer(String name, Color color) {
        
        super(name, color);
    }

    // Evaluate position based on gold value
    public int[] findBestMove(GameBoard board) {
        int bestGold = -1;
        int[] bestMove = null;

        if (super.numberOfVillages == 40) {
            // First move, can place anywhere
            for (int x = 0; x < board.boardSizeX; x++) {
                for (int y = 0; y < board.boardSizeY; y++) {
                    if (this.canPlaceVillage(board, x, y)) {
                        int gold = this.evaluatePosition(board, x, y);
                        System.out.println(String.format("Position (%d, %d) has gold %d", x, y, gold));
                        if (gold > bestGold) {
                            bestGold = gold;
                            bestMove = new int[]{x, y};
                        }
                    }
                }
            }
        } else {
            // Subsequent moves, should place next to existing villages if possible
            for (int i = 0; i < this.villageCoordinates.length; i++) {
                if (this.villageCoordinates[i][0] == 0 && this.villageCoordinates[i][1] == 0) {
                    continue;  // Skip uninitialized coordinates
                }
                int x = this.villageCoordinates[i][0];
                int y = this.villageCoordinates[i][1];
                String[] neighbours = board.getNeighbourTerrain(x, y);
                for (int j = 0; j < neighbours.length; j++) {
                    int[] neighbourPosition = board.coordinatesOfLastNeighbours[j];
                    int xNeighbour = neighbourPosition[0];
                    int yNeighbour = neighbourPosition[1];
                    if (this.canPlaceVillage(board, xNeighbour, yNeighbour)) {
                        int gold = this.evaluatePosition(board, xNeighbour, yNeighbour);
                        System.out.println(String.format("Position (%d, %d) has gold %d", xNeighbour, yNeighbour, gold));
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
                            System.out.println(String.format("Position (%d, %d) has gold %d", x, y, gold));
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
}