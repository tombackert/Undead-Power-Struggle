package ups.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import ups.model.GameBoard;

import java.util.ArrayList;
import java.util.List;

public class GameBoardView extends Pane {
    private final GameBoard model;
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;

    private final List<Polygon> hexagons = new ArrayList<>();

    public GameBoardView(GameBoard model) {
        this.model = model;
        widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> updateBoard());
        heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> updateBoard());
        updateBoard();
    }

    private void updateBoard() {
        getChildren().clear();
        hexagons.clear();

        double hexSize = Math.min(getWidth() / (BOARD_WIDTH * Math.sqrt(3)), getHeight() / (BOARD_HEIGHT * 1.5));
        double hexWidth = Math.sqrt(3) * hexSize;
        double hexHeight = 2 * hexSize;

        // Berechne die notwendige Verschiebung, um die Hexagone zu zentrieren
        double xOffset = (getWidth() - (BOARD_WIDTH * hexWidth + (hexWidth / 2))) / 2;
        double yOffset = (getHeight() - (BOARD_HEIGHT * hexHeight * 0.75 + hexHeight / 4)) / 2;

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Polygon hex = createHexagon(hexSize);
                hex.setFill(model.getColor(i, j));
                hex.setStroke(Color.BLACK);
                // Füge die berechneten Offsets hinzu
                hex.setLayoutX(xOffset + j * hexWidth + (i % 2) * hexWidth / 2);
                hex.setLayoutY(yOffset + i * hexHeight * 0.75);
                hexagons.add(hex);
                getChildren().add(hex);

                // Click-Event für jedes Hexagon
                int finalI = i;
                int finalJ = j;
                hex.setOnMouseClicked(event -> System.out.println("Hexagon at (" + finalI + ", " + finalJ + ") was clicked."));

                // Effekt der die Hexagone dunkler macht, wenn die Maus darüber schwebt
                final Color originalColor = model.getColor(i, j);
                hex.setOnMouseEntered(event -> hex.setFill(originalColor.darker()));
                hex.setOnMouseExited(event -> hex.setFill(originalColor));
            }
        }
    }

    private Polygon createHexagon(double size) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * (i + 0.5);
            double x = size * Math.cos(angle);
            double y = size * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }
        return hexagon;
    }

    public List<Polygon> getHexagons() {
        return hexagons;
    }
}
