package ups.utils;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Arrays;
import java.util.Random;

public class ProceduralZone {
    int goalSize;
    int currentSize;
    int[][] coordinates;
    int terrain;
    LinkedList<int[]> neighbours;
    ListIterator<int[]> iterator;
    ProceduralGameboard board;

    protected ProceduralZone(ProceduralGameboard b, int size, int terrainType, int[] start) {
        this.goalSize = size;
        this.currentSize = 0;
        this.coordinates = new int[size][2];
        this.coordinates[0] = start;
        this.terrain = terrainType;
        this.neighbours = new LinkedList<int[]>();
        this.iterator = neighbours.listIterator();
        this.board = b;
        this.addTile(start);
        this.addNeighbours(start);
    }

    private boolean isNeighbour(int[] n) {
        for (int[] neigh : this.neighbours) {
            if (Arrays.equals(n, neigh)) return true;
        }
        return false;
    }

    //adds possible neighbours for a tile
    private void addNeighbours(int[] tile) {
        int x = tile[0];
        int y = tile[1];
        int neighs[][];
        if (x % 2 == 0) { // even row
            neighs = new int[][]{
                    {x - 1, y},     // North-East
                    {x    , y + 1}, // East
                    {x + 1, y},     // South-East
                    {x - 1, y - 1}, // North-West
                    {x    , y - 1}, // West
                    {x + 1, y - 1}  // South-West
            };
        } else { // odd row
            neighs = new int[][]{
                    {x - 1, y + 1}, // North-East
                    {x    , y + 1}, // East
                    {x + 1, y + 1}, // South-East
                    {x - 1, y},     // North-West
                    {x    , y - 1},     // West
                    {x + 1, y}      // South-West
            };
        }
        for (int[] n : neighs) {
            if (!(this.isNeighbour(n)) && this.board.isAvailable(n)) this.neighbours.add(n);
        }
    }

    /**
     * Adds a tile to the zone
     * 
     * @param tile tile to add to the zone
     */
    private void addTile(int[] tile) {
        this.coordinates[this.currentSize] = tile;
        this.currentSize++;
        this.addNeighbours(tile);
        this.board.board[tile[0]][tile[1]] = this.terrain;
    }

    /**
     * Appends the zone to a random neighbour tile.
     * 
     * @return true if zone could be appended, false otherwise
     */
    protected boolean appendZone() {
        //Return false if there is no tile to append zone
        if (this.neighbours.size() == 0) return false;
        //append to a random neighbour tile
        Random r = new Random();
        int j = r.nextInt(this.neighbours.size());
        int[] tile = this.neighbours.get(j);
        this.neighbours.remove(j);
        this.addTile(tile);
        //Returns true as long as goal size is not reached
        return this.currentSize < this.goalSize;
    }

    /**
     * Changes the terrain of a zone.
     * 
     * @param t the new terrain
     */
    protected void changeTerrain(int t) {
        int x;
        int y;
        this.terrain = t;
        for (int i = 0; i < this.currentSize; i++) {
            x = this.coordinates[i][0];
            y = this.coordinates[i][1];
            this.board.board[x][y] = t;

        }
    }

    /**
     * Translates terrain type from int to String.
     * 
     * @param t terrain type as int
     * @return terrain type as String
     */
    public static String decodeTerrain(int t) {
        switch (t) {
            case 1:
                return "Gras";
            case 2:
                return "Wald";
            case 3:
                return "Wueste";
            case 4:
                return "Blumen";
            case 5:
                return "Canyon";
            case 6:
                return "Berg";
            case 7:
                return "Wasser";
            case 8:
                return "SilverCastle";
            case 9:
                return "GoldCastle";
            default:
                return "";
        }
    }

    protected int getSize() {
        return this.currentSize;
    }
}
