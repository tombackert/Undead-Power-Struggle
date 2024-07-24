// PlayerController.java
package ups.controller;

import ups.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The PlayerController class is an abstract class that is used to control the players in the game.
 * It contains a list of players, the game board, the number of placed settlements this turn and the game board controller.
 * The class provides methods to place a settlement on the game board and to reset the turn.
 * The class also provides a method to check if the turn can be ended.
 * The class is abstract
 */
public abstract class PlayerController {
    private final List<Player> players;
    private GameBoard gameBoard;
    private int placedSettlementsThisTurn = 0;
    private final GameBoardController gameBoardController;

    /**
     * Constructor for the PlayerController class.
     * Initializes the list of players and the game board controller.
     * @param gameBoardController The game board controller
     */
    public PlayerController(GameBoardController gameBoardController) {
        this.players = new ArrayList<>();
        this.gameBoardController = gameBoardController;
    }

    /**
     * Constructor for the PlayerController class.
     * Initializes the list of players, the game board and the game board controller.
     * @param player The player
     * @param gameBoard The game board
     * @param gameBoardController The game board controller
     */
    public PlayerController(Player player, GameBoard gameBoard, GameBoardController gameBoardController) {
        this(gameBoardController);
        this.players.add(player);
        this.gameBoard = gameBoard;
    }

    /**
     * Adds a player to the list of players.
     * @param player The player to be added
     * @param x The x-coordinate of the player's starting settlement
     * @param y The y-coordinate of the player's starting settlement
     * @throws InvalidPlacementException if the settlement cannot be placed
     */
    public void placeSettlement(Player player, int x, int y) throws InvalidPlacementException {
        String terrainType = gameBoard.getTerrainType(x, y);

        if (!gameBoard.isNotOccupied(x, y)) {
            throw new InvalidPlacementException("Das Feld ist bereits besetzt.");
        }

        if (terrainType.equals(player.getCurrentTerrainCard()) && player.canPlaceSettlement()) {
            gameBoard.placeSettlement(x, y, player.getColor());
            player.getSettlements().add(new Settlement(x, y, player.getColor()));
            player.villageCoordinates[player.numberOfVillages - player.getRemainingSettlements()] = new int[]{x, y};
            player.placeSettlement();
            placedSettlementsThisTurn++;

            gameBoardController.updateBoardForPlayer(player);

            if (canEndTurn() || player.getRemainingSettlements() == 0) {
                notifyCanEndTurn();
            }
        } else {
            throw new InvalidPlacementException("Platzierung ist auf diesem Feld nicht erlaubt oder maximale Siedlungen erreicht.");
        }

        gameBoard.setOwner(x, y, player);
    }

    private boolean isValidPlacement(int x, int y, String terrainType) {
        return gameBoard.getTerrainType(x, y).equals(terrainType) && gameBoard.isNotOccupied(x, y);
    }

    /**
     * Resets the turn by setting the number of placed settlements this turn to 0.
     */
    public void resetTurn() {
        placedSettlementsThisTurn = 0;
        players.forEach(Player::resetSettlementsPlacedThisTurn);
    }

    /**
     * Checks if the turn can be ended.
     * @return True if the turn can be ended, false otherwise
     */
    public boolean canEndTurn() {
        //System.out.println("Placed Settlements: " + Integer.toString(placedSettlementsThisTurn));
        return placedSettlementsThisTurn >= gameBoardController.getSettlementsPerTurn();
    }

    protected abstract void notifyCanEndTurn();
}
