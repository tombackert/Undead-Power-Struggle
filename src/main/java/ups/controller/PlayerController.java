// PlayerController.java
package ups.controller;

import ups.model.*;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerController {
    private final List<Player> players;
    private GameBoard gameBoard;
    private int placedSettlementsThisTurn = 0;
    private final GameBoardController gameBoardController;

    public PlayerController(GameBoardController gameBoardController) {
        this.players = new ArrayList<>();
        this.gameBoardController = gameBoardController;
    }

    public PlayerController(Player player, GameBoard gameBoard, GameBoardController gameBoardController) {
        this(gameBoardController);
        this.players.add(player);
        this.gameBoard = gameBoard;
    }

    public void placeSettlement(Player player, int x, int y) throws InvalidPlacementException {
        String terrainType = gameBoard.getTerrainType(x, y);

        if (!gameBoard.isNotOccupied(x, y)) {
            throw new InvalidPlacementException("Das Feld ist bereits besetzt.");
        }

        if (terrainType.equals(player.getCurrentTerrainCard()) && player.canPlaceSettlement()) {
            gameBoard.placeSettlement(x, y, player.getColor());
            player.getSettlements().add(new Settlement(x, y, player.getColor()));
            player.placeSettlement();
            placedSettlementsThisTurn++;

            if (player.getRemainingSettlements() == 0) {
                gameBoardController.updateBoardForPlayer(player);
                gameBoardController.endGame();
            }

            if (canEndTurn()) {
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

    public void resetTurn() {
        placedSettlementsThisTurn = 0;
        players.forEach(Player::resetSettlementsPlacedThisTurn);
    }

    public boolean canEndTurn() {
        //System.out.println("Placed Settlements: " + Integer.toString(placedSettlementsThisTurn));
        return placedSettlementsThisTurn >= gameBoardController.getSettlementsPerTurn();
    }

    protected abstract void notifyCanEndTurn();
}
