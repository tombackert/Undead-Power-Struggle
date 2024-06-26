package ups.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import ups.model.Highscore;
import ups.utils.HighscoreManager;

import java.util.List;

public class HighscoreController {

    @FXML
    private ListView<String> highscoreList;

    public void initialize() {
        HighscoreManager highscoreManager = new HighscoreManager();
        List<Highscore> highscores = highscoreManager.loadHighscores();

        for (Highscore entry : highscores) {
            highscoreList.getItems().add(entry.getPlayerName() + ": " + entry.getScore() + " Gold");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) highscoreList.getScene().getWindow();
        stage.close();
    }
}
