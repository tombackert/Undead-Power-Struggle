package ups.model;

import javafx.scene.paint.Color;

/**
 * OnlinePlayer
 */
public class OnlinePlayer extends Player{

    /**
     * Constructor for OnlinePlayer
     * @param name name of the player
     * @param color color of the player
     * @param settlementsPerTurn number of settlements player can build per turn
     * @param settlementsCount number of settlements player has
     */
    public OnlinePlayer(String name, Color color, int settlementsPerTurn, int settlementsCount) {
        super(name, color, settlementsPerTurn, settlementsCount);
        this.isExternalPlayer = true;
    }
    
}
