package ups.model;

import ups.gui.ColorMapping;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;


public class Player {

    //Number of villages left to place
    int numberOfVillages;
    
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

    public Player(String name, Color color) {
        this.numberOfVillages = 40;
        this.villageCoordinates = new int[40][2];
        this.name = name;
        this.color = ColorMapping.getIntFromColor(color);
        this.settlements = new ArrayList<>();
        this.remainingSettlements = 40; // Initiale Anzahl der Siedlungen
        this.settlementsPlacedThisTurn = 0; // Initialisiert mit 0
    }

    //Return false if village can't be placed, else true and place village 
    public boolean placeVillage(GameBoard b, int x, int y) {
        if (!(locationTile == b.getTerrainType(x, y) && (b.getOccupation(x, y) == 0) && (x >= 0 && x < 20 && y >= 0 && y < 20))) {
            System.out.println("Invalid coordinates. Try again!");
            return false;
        }
        this.villageCoordinates[40 - this.numberOfVillages][0] = x;
        this.villageCoordinates[40 - this.numberOfVillages][1] = y;
        b.placeSettlement(x, y, ColorMapping.getColorFromInt(this.color));
        this.numberOfVillages--;
        
        System.out.println(String.format("%s placed a village at (%d, %d)", this.name, x, y));
        return true;
    }

    //Return false if village can't be placed, else true
    public boolean canPlaceVillage(GameBoard b, int x, int y) {
        return locationTile == b.getTerrainType(x, y) && (b.getOccupation(x, y) == 0);
    }

    //Return true if village is adjacent to another village of the same color
    public boolean isAdjacent(GameBoard b, int x, int y) {
        b.resetNeighbourCoordinates(x, y);
        for (int i = 0; i < 6; i++) {
            if (b.getOccupation(b.coordinatesOfLastNeighbours[i][0], b.coordinatesOfLastNeighbours[i][1]) == this.color)
                return true;
        }
        return false;
    }

    //Returns the gold value of a Position
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

    

    // Returns 1 if there is Water adjacent to the position
    int evaluateFischer(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("Wasser")) {
                return 1;
            }
        }
        return 0;
    }

    // Returns 1 if there is a Mountain adjacent to the position
    int evaluateBergleute(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("Berg")) {
                return 1;
            }
        }
        return 0;
    }

    // Returns 1 if there is a Castle or Special Location adjacent to the position
    int evaluateArbeiter(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("GoldCastle") || neighbour.startsWith("SilverCastle")) {
                return 1;
            }
        }
        return 0;
    }

    // Returns the gold value of a position based on static bonuses (e.g., adjacent to Castle or Location)
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

    //Returns the amount of gold a player currently has
    public int calculateGold(GameBoard board) {
        int gold = 0;
        for (int i = 0; i < this.villageCoordinates.length; i++) {
            if (this.villageCoordinates[i][0] == 0 && this.villageCoordinates[i][1] == 0) {
                continue;  // Skip uninitialized coordinates
            }
            gold += evaluatePosition(board, this.villageCoordinates[i][0], this.villageCoordinates[i][1]);
        }
        return gold;
    }

    public boolean canPlaceSettlement() {
        return settlementsPlacedThisTurn < 3 && remainingSettlements > 0;
    }

    public String getName() {
        return name;
    }

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
