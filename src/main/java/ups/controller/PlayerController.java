// PlayerController.java
package ups.controller;

import ups.model.GameBoard;
import ups.model.InvalidPlacementException;
import ups.model.Player;
import ups.model.Settlement;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerController {
    private final List<Player> players;
    private GameBoard gameBoard;
    private int placedSettlementsThisTurn = 0;

    public PlayerController() {
        this.players = new ArrayList<>();
    }

    public PlayerController(Player player, GameBoard gameBoard) {
        this();
        this.players.add(player);
        this.gameBoard = gameBoard;
    }

    public void placeSettlement(Player player, int x, int y) throws InvalidPlacementException {
        String terrainType = gameBoard.getTerrainType(x, y);

        if (gameBoard.isTerrainAvailable(player.getCurrentTerrainCard())) {
            if (terrainType.equals(player.getCurrentTerrainCard()) && isValidPlacement(x, y, terrainType) && player.canPlaceSettlement()) {
                gameBoard.placeSettlement(x, y, player.getColor());
                player.getSettlements().add(new Settlement(x, y, player.getColor()));
                player.placeSettlement();
                placedSettlementsThisTurn++;
                if (canEndTurn()) {
                    notifyCanEndTurn();
                }
            } else {
                throw new InvalidPlacementException("Platzierung ist auf diesem Feld nicht erlaubt, Geländetyp stimmt nicht überein oder maximale Siedlungen erreicht.");
            }
        } else {
            if (isValidPlacement(x, y, terrainType) && player.canPlaceSettlement()) {
                gameBoard.placeSettlement(x, y, player.getColor());
                player.getSettlements().add(new Settlement(x, y, player.getColor()));
                player.placeSettlement();
                placedSettlementsThisTurn++;
                if (canEndTurn()) {
                    notifyCanEndTurn();
                }
            } else {
                throw new InvalidPlacementException("Platzierung ist auf diesem Feld nicht erlaubt oder maximale Siedlungen erreicht.");
            }
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
        return placedSettlementsThisTurn >= 3;
    }

    protected abstract void notifyCanEndTurn();
}
