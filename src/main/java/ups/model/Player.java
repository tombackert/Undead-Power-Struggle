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

    public int numberOfVillages; // Number of villages the player starts with
    public int[][] villageCoordinates; // Array of coordinates of Player's villages (x, y)
    public String name; // Name of the player
    int color; // Color of the player as an integer (1=Red, 2=Black, 3=Blue, 4=Orange)
    private String currentTerrainCard; // The current terrain card of the player
    private final List<Settlement> settlements; // List of settlements placed by the player
    protected int remainingSettlements; // Number of settlements left to place
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
     * Evaluates the game board for the player and returns the gold the player gets based on the selected cards.
     * 
     * @param board the game board
     * @return the amount of gold the player would get
     */
    public int evaluateGameboard(GameBoard board) {
        int gold = 0;
        for (String card : board.selectedCards) {
            switch (card) {
                case "Fischer" :
                    gold += evaluateFischer(board);
                    System.out.println("Fischer");
                    break;
                case "Fishermen" :
                    gold += evaluateFischer(board);
                    System.out.println("Fishermen");
                    break;
                case "Z.Fish" :
                    gold += evaluateFischer(board);
                    System.out.println("Z.Fish");
                    break;
                case "Z.Fisch" :
                    gold += evaluateFischer(board);
                    System.out.println("Z.Fisch");
                    break;      
                case "Bergleute":
                    gold += evaluateBergleute(board);
                    System.out.println("Bergleute");
                    break;
                case "Miners":
                    gold += evaluateBergleute(board);
                    System.out.println("Miners");
                    break;
                case "Z.Guard":
                    gold += evaluateBergleute(board);
                    System.out.println("Z.Guard");
                    break;
                case "Z:Wächter":
                    gold += evaluateBergleute(board);
                    System.out.println("Z.Wächter");
                    break;      
                case "Arbeiter":
                    gold += evaluateArbeiter(board);
                    System.out.println("Arbeiter");
                    break;
                case "Worker":
                    gold += evaluateArbeiter(board);
                    System.out.println("Worker");
                    break;
                case "Z.Worker":
                    gold += evaluateArbeiter(board);
                    System.out.println("Z.Worker");
                    break;
                case "Z.Arbeiter":
                    gold += evaluateArbeiter(board);
                    System.out.println("Z.Arbeiter");
                    break;
                case "Einsiedler":
                    gold += evaluateEinsiedler(board);
                    System.out.println("Einsiedler");
                    break;
                case "Hermit":
                    gold += evaluateEinsiedler(board);
                    System.out.println("Hermit");
                    break;
                case "Z.Hermit":
                    gold += evaluateEinsiedler(board);
                    System.out.println("Z.Hermit");
                    break;
                case "Z.Einsiedler":
                    gold += evaluateEinsiedler(board);
                    System.out.println("Z.Einsiedler");
                    break;
                case "Haendler":
                    gold += evaluateHaendler(board);
                    System.out.println("Haendler");
                    break;
                case "Merchant":
                    gold += evaluateHaendler(board);
                    System.out.println("Merchant");
                    break;
                case "Z.Haendler":
                    gold += evaluateHaendler(board);
                    System.out.println("Z.Haendler");
                    break;
                case "Z.Merchant":
                    gold += evaluateHaendler(board);
                    System.out.println("Z.Merchant");
                    break;
                case "Entdecker":
                    gold += evaluateEntdecker(board);
                    System.out.println("Entdecker");
                    break;
                case "Discoverer":
                    gold += evaluateEntdecker(board);
                    System.out.println("Discoverer");
                    break;
                case "Z.Entdecker":
                    gold += evaluateEntdecker(board);
                    System.out.println("Z:Entdecker");
                    break;
                case "Z.Discoverer":
                    gold += evaluateEntdecker(board);
                    System.out.println("Z.Discoverer");
                    break;
                case "Ritter":
                    gold += evaluateRitter(board);
                    System.out.println("Ritter");
                    break;
                case "Knight":
                    gold += evaluateRitter(board);
                    System.out.println("Knight");
                    break;
                case "Z.Ritter":
                    gold += evaluateRitter(board);
                    System.out.println("Z.Ritter");
                    break;
                case "Z.Knight":
                    gold += evaluateRitter(board);
                    System.out.println("Z.Knight");
                    break;
                case "Lords":
                    gold += evaluateLords(board);
                    System.out.println("Lords");
                    break;
                case "Z.Lords":
                    gold += evaluateLords(board);
                    System.out.println("Z.Lords");
                    break;
                case "Buerger":
                    gold += evaluateBuerger(board);
                    System.out.println("Buerger");
                    break;
                case "Citizen":
                    gold += evaluateBuerger(board);
                    System.out.println("Citizen");
                    break;
                case "Z.Buerger":
                    gold += evaluateBuerger(board);
                    System.out.println("Z.Buerger");
                    break;
                case "Z.Citizen":
                    gold += evaluateBuerger(board);
                    System.out.println("Z.Citizen");
                    break;
                case "Bauern":
                    gold += evaluateBauern(board);
                    System.out.println("Bauern");
                    break;
                case "Farmer":
                    gold += evaluateBauern(board);
                    System.out.println("Farmer");
                    break;
                case "Z.Farmer":
                    gold += evaluateBauern(board);
                    System.out.println("Z.Farmer");
                    break;
                case "Z.Bauern":
                    gold += evaluateBauern(board);
                    System.out.println("Z.Bauern");
                    break;
            }
        }
        gold += evaluateStaticBonuses(board);
        return gold;
    }

    /**
     * Returns the difference in gold if the player places at (x,y) vs if he doesn't.
     *
     * @param board the game board
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the gold value of the position
     */
    public int evaluatePosition(GameBoard board, int x, int y) {
        if (board == null) return 0; // Return 0 if board is null
        int gold = 0;
        //calculate gold amount for gameboard if move (x,y) is made
        board.occupiedBy[x][y] = this.color;
        this.villageCoordinates[this.numberOfVillages - this.remainingSettlements] = new int[]{x,y};
        this.remainingSettlements--;
        gold += evaluateGameboard(board);
        //Subtract gold amount of gameboard before move (x,y) is made
        board.occupiedBy[x][y] = 0;
        this.remainingSettlements++;
        gold -= evaluateGameboard(board);

        return gold;
    }



    /**
     * Returns the evaluation of the Fischer card.
     * 
     * @param board the game board
     * @return evaluation of the Fischer card
     */
    public int evaluateFischer(GameBoard board) {
        int gold = 0;
        int[][] neighbours;
        int x;
        int y;
        for (int i = 0; i < this.numberOfVillages - this.remainingSettlements; i++){
            x = this.villageCoordinates[i][0];
            y = this.villageCoordinates[i][1];
            neighbours = this.getHexagonalNeighbors(x, y);
            for (int[] n : neighbours) {
                if (board.getTerrainType(n[0], n[1]).equals("Wasser")) {
                    gold++;
                    break;
                }
            }
        }
        return gold;
    }

    /**
     * Returns the evaluation of the Bergleute card.
     * 
     * @param board the game board
     * @return evaluation of the Bergleute card
     */
    public int evaluateBergleute(GameBoard board) {
        int gold = 0;
        int[][] neighbours;
        int x;
        int y;
        for (int i = 0; i < this.numberOfVillages - this.remainingSettlements; i++){
            x = this.villageCoordinates[i][0];
            y = this.villageCoordinates[i][1];
            neighbours = this.getHexagonalNeighbors(x, y);
            for (int[] n : neighbours) {
                if (board.getTerrainType(n[0], n[1]).equals("Berg")) {
                    gold++;
                    break;
                }
            }
            }
        return gold;
    }

    /**
     * Returns the evaluation of the Arbeiter card.
     * 
     * @param board the game board
     * @return evaluation of the Arbeiter card
     */
    public int evaluateArbeiter(GameBoard board) {
        int gold = 0;
        int[][] neighbours;
        int x;
        int y;
        for (int i = 0; i < this.numberOfVillages - this.remainingSettlements; i++){
            x = this.villageCoordinates[i][0];
            y = this.villageCoordinates[i][1];
            neighbours = this.getHexagonalNeighbors(x, y);
            for (int[] n : neighbours) {
                if (board.getTerrainType(n[0], n[1]).equals("GoldCastle") || 
                board.getTerrainType(n[0], n[1]).equals("SilverCastle")) {
                    gold++;
                    break;
                }
            }
            }
        return gold;
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
                        if (0 <= n[0] && n[0] < board.boardSizeX && 0 <= n[1] && n[1] < board.boardSizeY 
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
                        if (!(0 <= n[0] && n[0] < board.boardSizeX && 0 <= n[1] && n[1] < board.boardSizeY)) continue;
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

    /**
     * Returns the evaluation of the Lords card.
     * 
     * @param board the game board
     * @return evaluation of the Lords card
     */
    public int evaluateLords(GameBoard board) {
        int gold = 0;
        int[] numsOfVills;
        int bigger;
        //Q1
        numsOfVills = new int[9];
        for (int i = 0; i < board.boardSizeX / 2; i++) {
            for (int j = 0; j < board.boardSizeY / 2; j++) {
                numsOfVills[board.occupiedBy[i][j]]++;
            }
        }
        bigger = 0;
        for (int c = 1; c < 9; c++) if (numsOfVills[c] > numsOfVills[this.color]) bigger++;
        if (numsOfVills[this.color] < 1) bigger = 3;
        switch (bigger) {
            case 0:
                gold += 12;
                break;
            case 1:
                gold += 6;
                break;
            default:
                break;
        }

        //Q2
        numsOfVills = new int[9];
        for (int i = 0; i < board.boardSizeX / 2; i++) {
            for (int j = board.boardSizeY / 2; j < board.boardSizeY; j++) {
                numsOfVills[board.occupiedBy[i][j]]++;
            }
        }
        bigger = 0;
        for (int c = 1; c < 9; c++) if (numsOfVills[c] > numsOfVills[this.color]) bigger++;
        if (numsOfVills[this.color] < 1) bigger = 3;
        switch (bigger) {
            case 0:
                gold += 12;
                break;
            case 1:
                gold += 6;
                break;
            default:
                break;
        }

        //Q3
        numsOfVills = new int[9];
        for (int i = board.boardSizeX / 2; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY / 2; j++) {
                numsOfVills[board.occupiedBy[i][j]]++;
            }
        }
        bigger = 0;
        for (int c = 1; c < 9; c++) if (numsOfVills[c] > numsOfVills[this.color]) bigger++;
        if (numsOfVills[this.color] < 1) bigger = 3;
        switch (bigger) {
            case 0:
                gold += 12;
                break;
            case 1:
                gold += 6;
                break;
            default:
                break;
        }

        //Q4
        numsOfVills = new int[9];
        for (int i = board.boardSizeX / 2; i < board.boardSizeX; i++) {
            for (int j = board.boardSizeY / 2; j < board.boardSizeY; j++) {
                numsOfVills[board.occupiedBy[i][j]]++;
            }
        }
        bigger = 0;
        for (int c = 1; c < 9; c++) if (numsOfVills[c] > numsOfVills[this.color]) bigger++;
        if (numsOfVills[this.color] < 1) bigger = 3;
        switch (bigger) {
            case 0:
                gold += 12;
                break;
            case 1:
                gold += 6;
                break;
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
                        if (0 <= n[0] && n[0] < board.boardSizeX && 0 <= n[1] && n[1] < board.boardSizeY 
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
        //Q1
        for (int x = 0; x < board.boardSizeX / 2; x++) {
            for (int y = 0; y < board.boardSizeY / 2; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        vills = 0;
        //Q2
        for (int x = 0; x < board.boardSizeX / 2; x++) {
            for (int y = board.boardSizeY / 2; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        vills = 0;
        //Q3
        for (int x = board.boardSizeX / 2; x < board.boardSizeX; x++) {
            for (int y = 0; y < board.boardSizeY / 2; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        vills = 0;
        //Q4
        for (int x = board.boardSizeX / 2; x < board.boardSizeX; x++) {
            for (int y = board.boardSizeY / 2; y < board.boardSizeY; y++) {
                if (board.getOccupation(x, y) == this.color) vills++;
            }
        }
        if (minVills > vills) minVills = vills;
        return 3 * minVills;
    }

    /**
     * Returns the gold amount of the static bonus - 3 gold for each Castle, that has at least one own village as its neighbour
     *
     * @param board the game board
     * @return the gold amount of the static bonus
     */
    public int evaluateStaticBonuses(GameBoard board) {
        int gold = 0;
        int[][] neighbours;
        for (int i = 0; i < board.boardSizeX; i++) {
            for (int j = 0; j < board.boardSizeY; j++) {
                if (!(board.getTerrainType(i, j).equals("SilverCastle") || 
                board.getTerrainType(i, j).equals("GoldCastle"))) continue;
                neighbours = this.getHexagonalNeighbors(i, j);
                for (int[] n : neighbours) {
                    if (board.getOccupation(n[0], n[1]) != this.color) continue;
                    gold += 3;
                    break;
                }
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

    public void printMoves() {
        for (int i = 0; i < this.numberOfVillages; i++) {
            System.out.println(Integer.toString(i+1) + ". (" + Integer.toString(this.villageCoordinates[i][0]) + ", " + Integer.toString(this.villageCoordinates[i][1]) + ")");
        }
    }
}
