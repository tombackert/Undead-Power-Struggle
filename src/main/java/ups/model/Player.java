package ups.model;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;

/**
 * The Player class represents a player in the game.
 */
public class Player {

    //0=Oracle; 1=Farm; 2=Oasis; 3=Tower; 4=Tavern; 5=Barn; 6=Harbour; 7=Paddock
    int[] locationCards;

    //G=GRASS; C=CANYON; D=DESERT; F=FLOWER; T=FOREST;
    public String locationTile; // Location tile of the player

    public int numberOfVillages; // Number of villages left to place
    int[][] villageCoordinates; // Array of coordinates of Player's villages (x, y)
    public String name; // Name of the player
    int color; // Color of the player as an integer (1=Red, 2=Black, 3=Blue, 4=Orange)
    private String currentTerrainCard; // The current terrain card of the player
    private final List<Settlement> settlements; // List of settlements placed by the player
    private int remainingSettlements; // Number of settlements left to place
    private int settlementsPlacedThisTurn; // Number of settlements placed this turn
    private final int settlementsPerTurn; // Number of settlements that can be placed per turn
    private GameBoard board; // Reference to the game board

    /**
     * Constructs a new Player with the given name and color.
     *
     * @param name  the name of the player
     * @param color the color of the player
     */
    public Player(String name, Color color, int settlementsPerTurn, int settlementsCount) {
        this.numberOfVillages = 40;
        this.villageCoordinates = new int[40][2];
        this.name = name;
        this.color = ColorMapping.getIntFromColor(color);
        this.settlements = new ArrayList<>();
        this.remainingSettlements = settlementsCount; // Initialize with the configurable number of settlements
        this.settlementsPlacedThisTurn = 0; // Initialisiert mit 0
        this.settlementsPerTurn = settlementsPerTurn; // Initialize settlementsPerTurn
        //System.out.println("Player created with settlements per turn: " + settlementsPerTurn); // Debug statement
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
        if (board == null) return 0; // Return 0 if board is null

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
     * Calculates the gold for the given position.
     *
     * @param board the game board
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the gold value
     */
    public int calculateGoldForPosition(GameBoard board, int x, int y) {
        return evaluatePosition(board, x, y);
    }

    /**
     * Returns the total gold value of all villages placed by the player.
     *
     * @param board the game board
     * @return the total gold value of all villages placed by the player
     */
    public int calculateGold(GameBoard board) {
        if (board == null) return 0; // Return 0 if board is null

        int totalGold = 0;
        for (Settlement settlement : settlements) {
            int x = settlement.getX();
            int y = settlement.getY();
            totalGold += evaluatePosition(board, x, y);
        }
        return totalGold;
    }

    public boolean canPlaceSettlement() {
        //System.out.println("Can place settlement check: " + (settlementsPlacedThisTurn < settlementsPerTurn) + " settlements placed: " + settlementsPlacedThisTurn + " settlements per turn: " + settlementsPerTurn + " remaining settlements: " + remainingSettlements); // Debug statement
        return settlementsPlacedThisTurn < settlementsPerTurn && remainingSettlements > 0;
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
