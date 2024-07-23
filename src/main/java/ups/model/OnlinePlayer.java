package ups.model;

import javafx.scene.paint.Color;

public class OnlinePlayer extends Player{

    public OnlinePlayer(String name, Color color, int settlementsPerTurn, int settlementsCount) {
        super(name, color, settlementsPerTurn, settlementsCount);
        this.isExternalPlayer = true;
    }
    
}
