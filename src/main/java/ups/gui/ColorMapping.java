package ups.gui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

public class ColorMapping {
    private static final Map<String, Color> colorMap = new HashMap<>();
    static {
        colorMap.put("Gras", Color.LIGHTGREEN);
        colorMap.put("Wald", Color.DARKGREEN);
        colorMap.put("Wueste", Color.YELLOW);
        colorMap.put("Blumen", Color.PINK);
        colorMap.put("Canyon", Color.BROWN);
        colorMap.put("Berg", Color.GRAY);
        colorMap.put("Wasser", Color.BLUE);
        colorMap.put("SilverCastle", Color.SILVER);
        colorMap.put("GoldCastle", Color.GOLD);
        // Möglichkeit weitere Farben nach Bedarf hinzuzufügen
    }

    public static Color getColorFromString(String colorName) {
        return colorMap.getOrDefault(colorName, Color.BLACK); // Verwendet Schwarz als Fallback
    }
}
