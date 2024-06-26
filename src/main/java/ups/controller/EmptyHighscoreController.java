package ups.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmptyHighscoreController {

    @FXML
    private VBox rootPane;  // Referenz zum Root-Element

    @FXML
    private void handleClose() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
