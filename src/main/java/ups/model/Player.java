package ups.model;

import ups.gui.ColorMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.paint.Color;

/**
 * The Player class represents a player in the game.
 */
public class Player {

    //Number of villages left to place
    public int numberOfVillages;
    
    //0=Oracle; 1=Farm; 2=Oasis; 3=Tower; 4=Tavern; 5=Barn; 6=Harbour; 7=Paddock
    int[] locationCards;
    
    //G=GRASS; C=CANYON; D=DESERT; F=FLOWER; T=FOREST;
    public String locationTile;

    //Array of coordinates of Player's villages
    int[][] villageCoordinates;
    
    // Player's name
    public String name;
    
    //1=Red; 2=Black; 3=Blue; 4=White
    int color;

    

    //Current terrain card
    private String currentTerrainCard;

    //List of settlements 
    private final List<Settlement> settlements;

    //Number of settlements left to place
    private int remainingSettlements;

    //Number of settlements placed this turn
    private int settlementsPlacedThisTurn;

    /**
     * Constructs a new Player with the given name and color.
     *
     * @param name  the name of the player
     * @param color the color of the player
     */
    public Player(String name, Color color) {
        this.numberOfVillages = 40;
        this.villageCoordinates = new int[40][2];
        this.name = name;
        this.color = ColorMapping.getIntFromColor(color);
        this.settlements = new ArrayList<>();
        this.remainingSettlements = 40; // Initiale Anzahl der Siedlungen
        this.settlementsPlacedThisTurn = 0; // Initialisiert mit 0
    }

    /**
     * Returns the gold value of a position based on the selected UPS cards.
     *
     * @param board the game board
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the gold value of the position
     */
    public int evaluatePosition(GameBoard board, int x, int y) {
        int gold = 0;

        // Evaluate gold based on selected Kindom Builder cards
        for (String card : board.selectedCards) {
            switch (card) {
                case "Fischer":
                    gold += evaluateFischer(board, x, y);
                    break;
                case "Bergleute":
                    gold += evaluateBergleute(board, x, y);
                    break;
                case "Arbeiter":
                    gold += evaluateArbeiter(board, x, y);
                    break;
                // Add cases for other cards
            }
        }

        // Bonus for being adjacent to Castles or Locations
        gold += evaluateStaticBonuses(board, x, y);

        return gold;
    }

    

    /**
     * Returns 1 if there is a Water hex adjacent to the position.
     *
     * @param board the game board
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return 1 if there is a Water hex adjacent to the position, 0 otherwise
     */
    int evaluateFischer(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("Wasser")) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Returns 1 if there is a Berg hex adjacent to the position.
     *
     * @param board the game board
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return 1 if there is a Berg hex adjacent to the position, 0 otherwise
     */
    int evaluateBergleute(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("Berg")) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Returns 1 if there is a Castle hex adjacent to the position.
     *
     * @param board the game board
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return 1 if there is a Castle hex adjacent to the position, 0 otherwise
     */
    int evaluateArbeiter(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("GoldCastle") || neighbour.startsWith("SilverCastle")) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Returns 3 if there is a Castle or Location hex adjacent to the position.
     *
     * @param board the game board
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return 3 if there is a Castle or Location hex adjacent to the position, 0 otherwise
     */
    int evaluateStaticBonuses(GameBoard board, int x, int y) {
        int gold = 0;
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("GoldCastle") || neighbour.equals("SilverCastle")) {
                gold += 3;  // Assume 3 gold for adjacent to Castle or Location
            }
        }
        return gold;
    }

    /**
     * Returns the total gold value of all villages placed by the player.
     *
     * @param board the game board
     * @return the total gold value of all villages placed by the player
     */
    public int calculateGold(GameBoard board) {
        int totalGold = 0;
        for (Settlement settlement : settlements) {
            int x = settlement.getX();
            int y = settlement.getY();
            totalGold += evaluatePosition(board, x, y);
        }
        return totalGold;
    }

//    public int calculateGold(GameBoard board) {
//        int gold = 0;
//        for (int i = 0; i < this.villageCoordinates.length; i++) {
//            if (this.villageCoordinates[i][0] == 0 && this.villageCoordinates[i][1] == 0) {
//                continue;  // Skip uninitialized coordinates
//            }
//            gold += evaluatePosition(board, this.villageCoordinates[i][0], this.villageCoordinates[i][1]);
//        }
//        return gold;
//    }


    public boolean canPlaceSettlement() {
        return settlementsPlacedThisTurn < 3 && remainingSettlements > 0;
    }

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the color of the player.
     *
     * @return the color of the player
     */
    public Color getColor() {
        return ColorMapping.getColorFromInt(this.color);
    }

    public void drawTerrainCard(String terrainType) {
        this.currentTerrainCard = terrainType;
    }

    public List<Settlement> getSettlements() {
        return settlements;
    }

    public String getCurrentTerrainCard() {
        return currentTerrainCard;
    }

    public int getRemainingSettlements() {
        return remainingSettlements;
    }

    public void placeSettlement() {
        if (canPlaceSettlement()) {
            remainingSettlements--;
            settlementsPlacedThisTurn++;
        } else {
            throw new IllegalStateException("Maximale Anzahl von Siedlungen für diesen Zug erreicht oder keine Siedlungen mehr verfügbar.");
        }
    }

    public void resetSettlementsPlacedThisTurn() {
        settlementsPlacedThisTurn = 0;
    }
}

//    /**
//     * Places a village at the given coordinates.
//     *
//     * @param b the game board
//     * @param x the x-coordinate
//     * @param y the y-coordinate
//     * @return true if the village was placed successfully, false otherwise
//     */
//    public boolean placeVillage(GameBoard b, int x, int y) {
//        if (!(Objects.equals(locationTile, b.getTerrainType(x, y)) && (b.getOccupation(x, y) == 0) && (x >= 0 && x < 20 && y >= 0 && y < 20))) {
//            System.out.println("Invalid coordinates. Try again!");
//            return false;
//        }
//        this.villageCoordinates[40 - this.numberOfVillages][0] = x;
//        this.villageCoordinates[40 - this.numberOfVillages][1] = y;
//        b.placeSettlement(x, y, ColorMapping.getColorFromInt(this.color));
//        this.numberOfVillages--;
//
//        System.out.printf("%s placed a village at (%d, %d)%n", this.name, x, y);
//        return true;
//    }
//
//    /**
//     * Returns true if a village can be placed at the given coordinates.
//     *
//     * @param b the game board
//     * @param x the x-coordinate
//     * @param y the y-coordinate
//     * @return true if a village can be placed, false otherwise
//     */
//    public boolean canPlaceVillage(GameBoard b, int x, int y) {
//        return Objects.equals(locationTile, b.getTerrainType(x, y)) && (b.getOccupation(x, y) == 0);
//    }
//
//    /**
//     * Returns true if a village can be placed at the given coordinates.
//     *
//     * @param b the game board
//     * @param x the x-coordinate
//     * @param y the y-coordinate
//     * @return true if a village can be placed, false otherwise
//     */
//    public boolean isAdjacent(GameBoard b, int x, int y) {
//        b.resetNeighbourCoordinates(x, y);
//        for (int i = 0; i < 6; i++) {
//            if (b.getOccupation(b.coordinatesOfLastNeighbours[i][0], b.coordinatesOfLastNeighbours[i][1]) == this.color)
//                return true;
//        }
//        return false;
//    }
