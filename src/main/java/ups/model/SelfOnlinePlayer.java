package ups.model;

import javafx.scene.paint.Color;

public class SelfOnlinePlayer extends Player {
    
    public SelfOnlinePlayer(String name, Color color, int settlementsPerTurn, int settlementsCount) {
        super(name, color, settlementsPerTurn, settlementsCount);
        this.isSelfOnlinePlayer = true;
    }
}
