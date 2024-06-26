package ups.model;

import javafx.scene.paint.Color;

public class Settlement {
    private final int x;
    private final int y;
    private final Color ownerColor;

    public Settlement(int x, int y, Color ownerColor) {
        this.x = x;
        this.y = y;
        this.ownerColor = ownerColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getOwnerColor() {
        return ownerColor;
    }
}
