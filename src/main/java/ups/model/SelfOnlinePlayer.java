package ups.model;

import javafx.scene.paint.Color;

/**
 * SelfOnlinePlayer class represents the player that is playing the game online.
 */
public class SelfOnlinePlayer extends Player {

    /**
     * Constructor for SelfOnlinePlayer class.
     * @param name Name of the player.
     * @param color Color of the player.
     * @param settlementsPerTurn Number of settlements that the player can build per turn.
     * @param settlementsCount Number of settlements that the player has.
     */
    public SelfOnlinePlayer(String name, Color color, int settlementsPerTurn, int settlementsCount) {
        super(name, color, settlementsPerTurn, settlementsCount);
        this.isSelfOnlinePlayer = true;
    }
}
