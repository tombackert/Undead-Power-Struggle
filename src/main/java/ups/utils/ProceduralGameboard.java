package ups.utils;
import java.util.LinkedList;
import java.util.Random;

public class ProceduralGameboard {
    int[][] board;
    int sizeX;
    int sizeY;
    int[] sizes;
    int freeSpace;
    
    public ProceduralGameboard(int sX, int sY) {
        this.board = new int[sX][sY];
        this.sizeX = sX;
        this.sizeY = sY;
        this.sizes = new int[6];
        this.freeSpace = sX * sY;
    }

    protected int[][] getNeighbours(int x, int y) {
        if (x % 2 == 0) { // even row
            return new int[][]{
                    {x - 1, y},     // North-East
                    {x    , y + 1}, // East
                    {x + 1, y},     // South-East
                    {x - 1, y - 1}, // North-West
                    {x    , y - 1}, // West
                    {x + 1, y - 1}  // South-West
            };
        } else { // odd row
            return new int[][]{
                    {x - 1, y + 1}, // North-East
                    {x    , y + 1}, // East
                    {x + 1, y + 1}, // South-East
                    {x - 1, y},     // North-West
                    {x    , y - 1},     // West
                    {x + 1, y}      // South-West
            };
        }
    }

    protected boolean isAvailable(int[] tile) {
        int x = tile[0];
        int y = tile[1];
        return (0 <= x && x < this.sizeX && 0 <= y && y < this.sizeY && this.board[x][y] == 0);
    }

    private int[] getNextStartingPosition() {
        int smallest = Integer.MAX_VALUE;
        int[] start = new int[]{-1, -1};
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                if (!(this.board[x][y] == 0)) continue;
                if (x + y < smallest) {
                    smallest = x + y;
                    start = new int[]{x, y};
                break;
                }
            }
        }
        return start;
    }

    /**
     * Returns a random size for a new Zone between 3 and the minimum of sizeX and sizeY.
     * 
     * @return random zone size
     */
    private int getRandomZoneSize() {
        Random r = new Random();
        return 3 + r.nextInt(Math.min(this.sizeY, this.sizeX) - 3);
    }

    /**
     * Choses a random terrain type of the terrain types which have the minimal coverage of the game board.
     * @return random minimal terrain type
     */
    private int choseNewZoneTerrain() {
        int min = Integer.MAX_VALUE;
        LinkedList<Integer> minima = new LinkedList<Integer>();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            min = Math.min(min, this.sizes[i]);
        }
        for (int i = 0; i < 6; i++) {
            if (this.sizes[i] == min) minima.add(i);
        }
        
        return 1 + minima.get(r.nextInt(minima.size())); //Add 1 because sizes array is 0-induced and terrain types start at 1
    }

    /**
     * Generates a random Gameboard.
     * 
     * @return the random Gameboard
     */
    public String[][] generateProceduralGameboard() {
        String[][] terrainMap = new String[this.sizeX][this.sizeY];
        while (this.freeSpace > 0) {
            System.out.println(this.freeSpace);
            ProceduralZone z = new ProceduralZone(this, this.getRandomZoneSize(), this.choseNewZoneTerrain(), this.getNextStartingPosition());
            while (z.appendZone());
            if (z.getSize() < 3) z.changeTerrain(7);
            else {
            this.sizes[z.terrain - 1] += z.getSize(); //Subtract 1 because sizes array is 0-induced and terrain types start at 1
            }
            this.freeSpace -= z.getSize();
        }

        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                terrainMap[x][y] = ProceduralZone.decodeTerrain(this.board[x][y]);
            }
        }
        return terrainMap;
    }
}
