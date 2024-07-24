package ups.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ups.model.Highscore;

import java.io.IOException;
import java.util.List;

public class HighscoreView {

    public void showHighscoreView(List<Highscore> highscores) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/HighscoreView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Highscores");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
