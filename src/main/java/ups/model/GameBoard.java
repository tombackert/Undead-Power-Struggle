package ups.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ups.gui.ColorMapping;

/**
 * The GameBoard class represents the game board of the game.
 */

public class GameBoard {
    
    private final Color[][] colors; // Hält die Farben für jede Position
    private final String[][] terrainMap;  // Hält die Geländetypen für jede Position
    private final boolean[][] occupied;  // Hält die Informationen über besetzte Felder
    private Map<Point, Player> hexagonOwnership; // Hält die Besitzer der Hexagone
    private Map<String, Integer> terrainCount; // Anzahl der verbleibenden Felder für jeden Geländetyp
    
    //List of selected cards
    public List<String> selectedCards;
    
    
    /*
    * Neue Variablen
    */
    public int boardSizeX;
    public int boardSizeY;

    public int[][] coordinatesOfLastNeighbours = new int[6][2];
    public int[][] occupiedBy;

    /**
     * Constructs a new GameBoard with the given number of rows and columns.
     *
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public GameBoard(int rows, int cols, List<String> selectedCards) {
        super();
        this.colors = new Color[rows][cols]; // Initialisierung der Farbmatrix
        this.terrainMap = new String[rows][cols]; // Initialisierung der TerrainMap
        this.occupied = new boolean[rows][cols]; // Initialisierung der Besetztheitsmatrix
        this.terrainCount = new HashMap<>(); // Initialisierung der Terrainzählung
        this.occupiedBy = new int[rows][cols];
        this.boardSizeX = rows;
        this.boardSizeY = cols;
        hexagonOwnership = new HashMap<>(); // Initialisierung der Besitzerliste
        this.selectedCards = selectedCards;
    }

    /**
     * Constructs a new GameBoard with the given number of rows and columns and initializes it with the board at the given index.
     *
     * @param rows  the number of rows
     * @param cols  the number of columns
     * @param index the index of the board
     * @throws IOException if an I/O error occurs
     */
    public GameBoard(int rows, int cols, int index) throws IOException {
        this.colors = new Color[rows][cols]; // Initialisierung der Farbmatrix
        this.terrainMap = new String[rows][cols]; // Initialisierung der TerrainMap
        this.occupied = new boolean[rows][cols]; // Initialisierung der Besetztheitsmatrix
        this.occupiedBy = new int[rows][cols];
        this.boardSizeX = rows;
        this.boardSizeY = cols;
        initialize(index, 0, 0);
    }

    /**
     * Initializes the game board with the board at the given index.
     *
     * @param index    the index of the board
     * @param startRow the start row
     * @param startCol the start column
     * @throws IOException if an I/O error occurs
     */
    public void initialize(int index, int startRow, int startCol) throws IOException {
        String filePath = getFilePathByIndex(index);
        if (filePath == null) {
            throw new IllegalArgumentException("Ungültiger Index für Board-Initialisierung");
        }
        loadBoardFromFile(filePath, startRow, startCol);
    }

    /**
     * Returns the file path for the board at the given index.
     *
     * @param index the index of the board
     * @return the file path
     */
    private String getFilePathByIndex(int index) {
        switch (index) {
            case 0:
                return "src/main/resources/One.json";
            case 1:
                return "src/main/resources/Two.json";
            case 2:
                return "src/main/resources/Three.json";
            case 3:
                return "src/main/resources/Four.json";
            default:
                return null;
        }
    }

    /**
     * Loads the board from the file at the given file path.
     *
     * @param filePath the file path
     * @param startRow the start row
     * @param startCol the start column
     * @throws IOException if an I/O error occurs
     */
    private void loadBoardFromFile(String filePath, int startRow, int startCol) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<List<String>>> data = mapper.readValue(Path.of(filePath).toFile(), new TypeReference<>() {});
        List<List<String>> terrain = data.get("terrainTypes");

        for (int row = 0; row < terrain.size(); row++) {
            List<String> terrainRow = terrain.get(row);
            for (int col = 0; col < terrainRow.size(); col++) {
                colors[startRow + row][startCol + col] = ColorMapping.getColorFromString(terrainRow.get(col));
                terrainMap[startRow + row][startCol + col] = terrainRow.get(col);  // Speichert den Geländetyp
                occupied[startRow + row][startCol + col] = false;  // Initialisiere alle Felder als unbesetzt

                terrainCount.put(terrainRow.get(col), terrainCount.getOrDefault(terrainRow.get(col), 0) + 1); // Zähle die Anzahl der Geländetypen
            }
        }
    }

    /**
     * Returns the color at the given row and column.
     *
     * @param row the row
     * @param col the column
     * @return the color
     */
    public Color getColor(int row, int col) {
        return colors[row][col];
    }

    /**
     * Places a settlement at the given row and column with the given color.
     *
     * @param x     the row
     * @param y     the column
     * @param color the color
     */
    public void placeSettlement(int x, int y, Color color) {
        if (isNotOccupied(x, y)) {
            colors[x][y] = color;
            occupied[x][y] = true; // Setze das Hexagon als besetzt
            occupiedBy[x][y] = ColorMapping.getIntFromColor(color);

            // Aktualisiere den Zähler für den Geländetyp
            String terrainType = terrainMap[x][y]; // Hole den Geländetyp des Hexagons
            terrainCount.put(terrainType, terrainCount.get(terrainType) - 1); // Reduziere die Anzahl der verbleibenden Felder
        } else {
            throw new IllegalStateException("Auf diesem Feld ist bereits eine Siedlung vorhanden.");
        }
    }

    /**
     * Returns the terrain type at the given row and column.
     *
     * @param x the row
     * @param y the column
     * @return the terrain type
     */
    public String getTerrainType(int x, int y) {
        if (0 <= x && x < this.boardSizeX && 0 <= y && y < this.boardSizeY) return terrainMap[x][y];
        return "!";
    }

    /**
     * Returns whether the hexagon at the given row and column is not occupied.
     *
     * @param x the row
     * @param y the column
     * @return true if the hexagon is not occupied, false otherwise
     */
    public boolean isNotOccupied(int x, int y) {
        return !occupied[x][y];
    }


    /**
     * Returns the occupation of the hexagon at the given row and column.
     * 
     * @param x the row
     * @param y the column
     * @return 0 if the hexagon is not occupied, 1 to 4 depending on which player occupies hexagon.
     */
    public int getOccupation(int x, int y) {
        return this.occupiedBy[x][y];
    }

    /**
     * Sets the owner of the hexagon at the given row and column to the given player.
     *
     * @param x      the row
     * @param y      the column
     * @param player the player
     */
    public void setOwner(int x, int y, Player player) {
        hexagonOwnership.put(new Point(x, y), player);
    }

    /**
     * Returns the owner of the hexagon at the given row and column.
     *
     * @param x the row
     * @param y the column
     * @return the owner
     */
    public Object getOwner(int x, int y) {
        return hexagonOwnership.get(new Point(x, y));
    }

    /**
     * Returns whether the given terrain type is available.
     *
     * @param terrainType the terrain type
     * @return true if the terrain type is available, false otherwise
     */
    public boolean isTerrainAvailable(String terrainType) {
        return terrainCount.getOrDefault(terrainType, 0) > 0;
    }

    //Returns the tile types of the neighbours in an array and stores the coordinates in the 2D array coordinatesOfLastNeighbours
    public void resetNeighbourCoordinates(int x, int y) {

        if ((y % 2) == 0) {
            this.coordinatesOfLastNeighbours[0] = new int[]{x - 1, y - 1};
            this.coordinatesOfLastNeighbours[1] = new int[]{x - 1, y};
            this.coordinatesOfLastNeighbours[2] = new int[]{x, y - 1};
            this.coordinatesOfLastNeighbours[3] = new int[]{x, y + 1};
            this.coordinatesOfLastNeighbours[4] = new int[]{x + 1, y - 1};
            this.coordinatesOfLastNeighbours[5] = new int[]{x + 1, y};
        }
        else {
            this.coordinatesOfLastNeighbours[0] = new int[]{x - 1, y};
            this.coordinatesOfLastNeighbours[1] = new int[]{x - 1, y + 1};
            this.coordinatesOfLastNeighbours[2] = new int[]{x, y - 1};
            this.coordinatesOfLastNeighbours[3] = new int[]{x, y + 1};
            this.coordinatesOfLastNeighbours[4] = new int[]{x + 1, y};
            this.coordinatesOfLastNeighbours[5] = new int[]{x + 1, y + 1};
        }
        
    }

    /*
     * Returns the terrain type of the neighbours of the hexagon at the given row and column.
     */
    public String[] getNeighbourTerrain(int x, int y) {
        this.resetNeighbourCoordinates(x, y);
        String[] arr = new String[6];
        for (int i = 0; i < 6; i++) {
            arr[i] = this.getTerrainType(this.coordinatesOfLastNeighbours[i][0], this.coordinatesOfLastNeighbours[i][1]);
        }
        return arr;
    }


}
