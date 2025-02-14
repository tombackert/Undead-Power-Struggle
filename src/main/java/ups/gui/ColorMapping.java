package ups.gui;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * The ColorMapping class is responsible for mapping color names to JavaFX Color objects and vice versa.
 */
public class ColorMapping {

    /**
     * A map that maps color names to JavaFX Color objects.
     */
    private static final Map<String, Color> colorMap = new HashMap<>();
    static {
        colorMap.put("Gras", Color.LIGHTGREEN);
        colorMap.put("Wald", Color.DARKGREEN);
        colorMap.put("Wueste", Color.YELLOW);
        colorMap.put("Blumen", Color.PINK);
        colorMap.put("Canyon", Color.BROWN);
        colorMap.put("Berg", Color.GRAY);
        colorMap.put("Wasser", Color.LIGHTBLUE);
        colorMap.put("SilverCastle", Color.SILVER);
        colorMap.put("GoldCastle", Color.GOLD);

        // Spieler Farben
        colorMap.put("Rot", Color.RED);
        colorMap.put("Schwarz", Color.BLACK);
        colorMap.put("Blau", Color.BLUE);
        colorMap.put("Orange", Color.ORANGE);
        colorMap.put("Red", Color.RED);
        colorMap.put("Black", Color.BLACK);
        colorMap.put("Blue", Color.BLUE);
        colorMap.put("Lila", Color.PURPLE);
        colorMap.put("Weiß", Color.WHITE);
        colorMap.put("Magenta", Color.MAGENTA);
        colorMap.put("Babyblau", Color.LIGHTBLUE);
        colorMap.put("Purple", Color.PURPLE);
        colorMap.put("White", Color.WHITE);
        
    }

    /**
     * Returns the JavaFX Color object for the given color name.
     *
     * @param colorName the color name
     * @return the JavaFX Color object
     */
    public static Color getColorFromString(String colorName) {
        return colorMap.getOrDefault(colorName, Color.GRAY); // Default color is gray
    }

    /**
     * Returns the JavaFX Color object for the given player color.
     * @param color the player color
     * @return the JavaFX Color object
     */
    public static Color getColorFromInt(int color) {
        switch (color) {
            case 1:
                return Color.RED;
            case 2:
                return Color.BLACK;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.ORANGE;
            case 5:
                return Color.PURPLE;
            case 6:
                return Color.WHITE;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.LIGHTBLUE;    
            default:
                throw new IllegalArgumentException("Ungültige Spielerfarbe: " + color);
        }
    }

    /**
     * Returns the player color for the given JavaFX Color object.
     * @param color the JavaFX Color object
     * @return the player color
     */
    public static int getIntFromColor(Color color) {
        if (color.equals(Color.RED)) {
            return 1;
        } else if (color.equals(Color.BLACK)) {
            return 2;
        } else if (color.equals(Color.BLUE)) {
            return 3;
        } else if (color.equals(Color.ORANGE)) {
            return 4;
        } else if (color.equals(Color.PURPLE)) {
            return 5;
        } else if (color.equals(Color.WHITE)) {
            return 6;
        } else if (color.equals(Color.MAGENTA)) {
            return 7;
        } else if (color.equals(Color.LIGHTBLUE)) {
            return 8;
        }
        else {
            throw new IllegalArgumentException("Ungültige Spielerfarbe: " + color);
        }  
    }
    

    /**
     * Returns the color name for the given JavaFX Color object.
     *
     * @param color the JavaFX Color object
     * @return the color name
     */
    public static String getStringFromColor(Color color) {
        return colorMap.entrySet().stream() // Stream the color map
                .filter(entry -> entry.getValue().equals(color)) // Filter the color name that matches the given color
                .map(Map.Entry::getKey)// Return the color name for the given color
                .findFirst() // Return the first color name that matches the given color
                .orElse("Unbekannt"); // Return "Unbekannt" if color is not found
    }

}
