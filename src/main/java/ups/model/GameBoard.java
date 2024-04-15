package ups.model;

import javafx.scene.paint.Color;
import ups.gui.ColorMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class GameBoard {
    private final Color[][] colors;

    public GameBoard(int rows, int cols) {
        this.colors = new Color[rows][cols];
    }

    public GameBoard(int rows, int cols, int index) throws IOException {
        this.colors = new Color[rows][cols];
        initialize(index, 0, 0);  // Aufruf der initialize-Methode mit dem gegebenen Index
    }

    public void initialize(int index, int startRow, int startCol) throws IOException {
        String filePath;
        switch (index) {
            case 0:
                filePath = "src/main/resources/One.txt";
                break;
            case 1:
                filePath = "src/main/resources/Two.txt";
                break;
            case 2:
                filePath = "src/main/resources/Three.txt";
                break;
            case 3:
                filePath = "src/main/resources/Four.txt";
                break;
            default:
                throw new IllegalArgumentException("Ungültiger Index für Board-Initialisierung");
        }
        loadColorsFromFile(filePath, startRow, startCol);
    }

    private void loadColorsFromFile(String filePath, int startRow, int startCol) throws IOException {
        List<String> lines = Files.lines(Path.of(filePath))
                .map(String::trim)  // Remove any leading or trailing whitespace
                .collect(Collectors.toList());
        // Assuming a 10x10 board
        int rows = 10;
        int cols = 10;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int lineIndex = row * cols + col;  // Calculate the line index based on row and col
                if (lineIndex < lines.size()) {
                    String colorName = lines.get(lineIndex);
                    colors[startRow + row][startCol + col] = ColorMapping.getColorFromString(colorName);
                }
            }
        }
    }

    // Gibt die Farbe für eine spezifische Zelle zurück
    public Color getColor(int row, int col) {
        return colors[row][col];
    }
}
