package ups.controller;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import ups.view.GameBoardView;

public class GameBoardController {
    private final GameBoardView view;

    public GameBoardController(GameBoardView view) {
        this.view = view;
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        for (Polygon hex : view.getHexagons()) {
            hex.setOnMouseClicked(this::handleHexagonClick);
        }
    }

    private void handleHexagonClick(MouseEvent event) {
        event.getSource();
        // Führt hier die Aktionen aus, die ausgeführt werden sollen, wenn auf ein Hexagon geklickt wird
    }
}

