package ups.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
        this.numberOfVillages = settlementsCount;
        this.villageCoordinates = new int[settlementsCount][2];
        this.name = name;
        this.color = ColorMapping.getIntFromColor(color);
        this.settlements = new ArrayList<>();
        this.remainingSettlements = settlementsCount; // Initialize with the configurable number of settlements
        this.settlementsPlacedThisTurn = 0; // Initialisiert mit 0
        this.settlementsPerTurn = settlementsPerTurn; // Initialize settlementsPerTurn
        //System.out.println("Player created with settlements per turn: " + settlementsPerTurn); // Debug statement
    }

    public int[][] getHexagonalNeighbors(int x, int y) {
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
    public int evaluateFischer(GameBoard board, int x, int y) {
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
    public int evaluateBergleute(GameBoard board, int x, int y) {
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
    public int evaluateArbeiter(GameBoard board, int x, int y) {
        String[] neighbours = board.getNeighbourTerrain(x, y);
        for (String neighbour : neighbours) {
            if (neighbour.equals("GoldCastle") || neighbour.startsWith("SilverCastle")) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Returns the amount of non connected village groups.
     * 
     * @param board the game board
     * @return the amount of non connected village groups
     */
    public int evaluateEinsiedler(GameBoard board) {
        int groups = 0;
        int[][] eboard = new int[board.boardSizeX][board.boardSizeY]; //Creates zeroed out 2D-array
        //set location of own villages to own color, leave rest as 0
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if(board.occupiedBy[i][j] == this.color) eboard[i][j] = this.color;
            }
        }
        Queue<int[]> queue = new LinkedList<>();
        int[][] neighbours;
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if (eboard[i][j] == 0) continue;
                queue.offer(new int[]{i, j}); //found a new village group
                groups++;
                //remove all connected villages with breadth search
                while (!queue.isEmpty()) {
                    int[] current = queue.poll();
                    eboard[current[0]][current[1]] = 0;
                    neighbours = this.getHexagonalNeighbors(current[0], current[1]);
                    for (int[] n : neighbours) {
                        if (0 < n[0] && n[0] < board.boardSizeX && 0 < n[1] && n[1] < board.boardSizeY 
                        && !(eboard[n[0]][n[1]] == 0)) queue.offer(n);
                    }
                }
                
            }
        }

        return groups;
    }

    /**
     * Returns the amount of gold given to the player by evaluating the Haendler card.
     * 
     * @param board the game board
     * @return gold value
     */
    public int evaluateHaendler(GameBoard board) {
        int gold = 0;
        int[][] eboard = new int[board.boardSizeX][board.boardSizeY]; //Creates zeroed out 2D-array
        //set location of own villages to own color, leave rest as 0
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if(board.occupiedBy[i][j] == this.color) eboard[i][j] = this.color;
            }
        }
        Queue<int[]> queue = new LinkedList<>();
        int[][] neighbours;
        int castles;
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if (eboard[i][j] != this.color) continue;
                queue.offer(new int[]{i, j}); //found a new village group
                //find all castles connected to that group with breadth search
                castles = 0;
                while (!queue.isEmpty()) {
                    int[] current = queue.poll();
                    eboard[current[0]][current[1]] = 0;
                    neighbours = this.getHexagonalNeighbors(current[0], current[1]);
                    for (int[] n : neighbours) {
                        if (!(0 < n[0] && n[0] < board.boardSizeX && 0 < n[1] && n[1] < board.boardSizeY)) continue;
                        if (eboard[n[0]][n[1]] == this.color) queue.offer(n);
                        if (eboard[n[0]][n[1]] == 0
                        && (board.getTerrainType(n[0], n[1]).equals("GoldCastle") 
                        || board.getTerrainType(n[0], n[1]).equals("SilverCastle"))) {
                            castles++;
                            eboard[n[0]][n[1]] = -1; //mark counted castle
                        }
                    }
                }
                if (castles > 1) gold += 4 * castles;
            }
        }
        return gold;
    }

    /**
     * Returns the number of horizontal lines on which the player has built at least one village.
     * 
     * @param board the game board
     * @return 1 gold per horizontal line
     */
    public int evaluateEntdecker(GameBoard board) {
        int lines = 0;
        int current;
        for (int x = 0; x < board.boardSizeX; x++) {
            current = 0;
            for (int y = 0; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == this.color) current = 1;
            }
            lines += current;
        }
        return lines;
    }

    /**
     * Returns twice the number of own villages on the horizontal line that contains the most own villages
     * 
     * @param board the game board
     * @return 2 * number of own villages on the horizontal line that contains the most own villages
     */
    public int evaluateRitter(GameBoard board) {
        int maxVills = 0;
        int localVills;
        for (int x = 0; x < board.boardSizeX; x++) {
            localVills = 0;
            for (int y = 0; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == this.color) localVills++;
            }
            if (localVills > maxVills) maxVills = localVills;
        }
        return 2 * maxVills;
    }

    //Help function for evaluating lords card
    private static int[] helpLords(int[] a) {
        int[] b = new int[a.length];
        int place = 1;
        int max;
        int placed = 0;
        while (placed < a.length) {
            max = -1;
            for (int i = 0; i < a.length; i++) {
                if (a[i] > max) max = a[i];
            }
            for (int i = 0; i < a.length; i++) {
                if (a[i] == max) {
                    b[i] = place;
                    a[i] = Integer.MIN_VALUE;
                    placed++;
                }
            }
            place = 1 + placed;
        }
        return b;
    }

    /**
     * Returns the evaluation of the Lords card.
     * 
     * @param board the game board
     * @return evaluation of the Lords card
     */
    public int evaluateLords(GameBoard board) {
        int gold = 0;
        int[] numsOfVills = new int[4];
        int[] placements;
        //Q1
        for (int x = 0; x < board.boardSizeX / 2; x++) {
            for (int y = 0; y < board.boardSizeY / 2; y++) {
                if (board.getOccupation(x, y) == 0) continue;
                numsOfVills[board.getOccupation(x, y) - 1]++;
            }
        }
        //Assign gold based on placement
        placements = helpLords(numsOfVills);
        switch (placements[this.color - 1]) {
            case 1:
                gold += 12;
                break;
            case 2:
                gold += 6;
            default:
                break;
        }
        numsOfVills = new int[4];
        //Q2
        for (int x = 0; x < board.boardSizeX / 2; x++) {
            for (int y = board.boardSizeY / 2; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == 0) continue;
                numsOfVills[board.getOccupation(x, y) - 1]++;
            }
        }
        //Assign gold based on placement
        placements = helpLords(numsOfVills);
        switch (placements[this.color - 1]) {
            case 1:
                gold += 12;
                break;
            case 2:
                gold += 6;
            default:
                break;
        }
        numsOfVills = new int[4];
        //Q3
        for (int x = board.boardSizeX / 2; x < board.boardSizeX; x++) {
            for (int y = 0; y < board.boardSizeY / 2; y++) {
                if (board.getOccupation(x, y) == 0) continue;
                numsOfVills[board.getOccupation(x, y) - 1]++;
            }
        }
        //Assign gold based on placement
        placements = helpLords(numsOfVills);
        switch (placements[this.color - 1]) {
            case 1:
                gold += 12;
                break;
            case 2:
                gold += 6;
            default:
                break;
        }
        numsOfVills = new int[4];
        //Q4
        for (int x = board.boardSizeX / 2; x < board.boardSizeX; x++) {
            for (int y = board.boardSizeY / 2; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == 0) continue;
                numsOfVills[board.getOccupation(x, y) - 1]++;
            }
        }
        //Assign gold based on placement
        placements = helpLords(numsOfVills);
        switch (placements[this.color - 1]) {
            case 1:
                gold += 12;
                break;
            case 2:
                gold += 6;
            default:
                break;
        }
        
        return gold;
    }

    /**
     * Returns the evaluation of the Buerger card.
     * 
     * @param board the game board
     * @return evaluation of the Buerger card
     */
    public int evaluateBuerger(GameBoard board) {
        int maxVills = 0;
        int vills = 0;
        int[][] eboard = new int[board.boardSizeX][board.boardSizeY]; //Creates zeroed out 2D-array
        //set location of own villages to own color, leave rest as 0
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if(board.occupiedBy[i][j] == this.color) eboard[i][j] = this.color;
            }
        }
        Queue<int[]> queue = new LinkedList<>();
        int[][] neighbours;
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if (eboard[i][j] == 0) continue;
                queue.offer(new int[]{i, j}); //found a new village group
                eboard[i][j] = 0;
                vills++;
                //count number of members
                while (!queue.isEmpty()) {
                    int[] current = queue.poll();
                    eboard[current[0]][current[1]] = 0;
                    neighbours = this.getHexagonalNeighbors(current[0], current[1]);
                    for (int[] n : neighbours) {
                        if (0 < n[0] && n[0] < board.boardSizeX && 0 < n[1] && n[1] < board.boardSizeY 
                        && !(eboard[n[0]][n[1]] == 0)) {
                            queue.offer(n);
                            eboard[n[0]][n[1]] = 0;
                            vills++;
                        }
                    }
                }
                if (vills > maxVills) maxVills = vills;
                vills = 0;
            }
        }
        return maxVills / 2;
    }

    /**
     * Returns the evaluation of the Bauern card.
     * @param board the game board
     * @return evaluation of the Bauern card
     */
    public int evaluateBauern(GameBoard board) {
        int minVills = Integer.MAX_VALUE;
        int vills = 0;
        for (int x = 0; x < board.boardSizeX / 2; x++) {
            for (int y = 0; y < board.boardSizeY / 2; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        minVills = 0;
        //Q2
        for (int x = 0; x < board.boardSizeX / 2; x++) {
            for (int y = board.boardSizeY / 2; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        minVills = 0;
        //Q3
        for (int x = board.boardSizeX / 2; x < board.boardSizeX; x++) {
            for (int y = 0; y < board.boardSizeY / 2; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        minVills = 0;
        //Q4
        for (int x = board.boardSizeX / 2; x < board.boardSizeX; x++) {
            for (int y = board.boardSizeY / 2; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        return 3 * minVills;
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
