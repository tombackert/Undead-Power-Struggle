package ups.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ups.controller.GameBoardController;
import ups.model.GameBoard;
import ups.view.GameBoardView;

import java.io.IOException;

public class Gui extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        GameBoard model = new GameBoard(20, 20);
        // Initialisiere das Spielfeld mit der Datei, die dem Index entspricht, an der Position (r, c)
        int[][] positions = {{0, 0}, {0, 10}, {10, 0}, {10, 10}};
        for (int i = 0; i < positions.length; i++) {
            model.initialize(i, positions[i][0], positions[i][1]);
        }
        GameBoardView view = new GameBoardView(model);
        new GameBoardController(view);

        // Erstelle die Buttons
        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button 2");

        // Füge Buttons zu einem VBox hinzu
        VBox buttonBox = new VBox(10, button1, button2); // 10 ist der Abstand zwischen den Buttons
        buttonBox.setPadding(new Insets(10)); // 10 ist der Abstand vom Rand der VBox

        // Erstelle ein StackPane und füge das GameBoardView hinzu, um es zu zentrieren
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(view);
        stackPane.setAlignment(view, Pos.CENTER);
        stackPane.setMargin(view, new Insets(50)); // Padding um das Spielfeld im StackPane
        stackPane.setStyle("-fx-background-color: white;"); // Optional: Hintergrundfarbe setzen

        // Binde die Größe des GameBoardView an die Größe des StackPane
        view.prefWidthProperty().bind(stackPane.widthProperty());
        view.prefHeightProperty().bind(stackPane.heightProperty());

        // Erstelle ein BorderPane und füge die HBox in die Mitte
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(stackPane); // Setzt das Spielfeld in die Mitte
        borderPane.setLeft(buttonBox); // Fügt die Buttons am linken Rand des BorderPane hinzu
        BorderPane.setMargin(stackPane, new Insets(100)); // Padding um das Spielfeld im BorderPane

        // Erstelle die Scene
        Scene scene = new Scene(borderPane, 1024, 768);
        primaryStage.setMinWidth(800);  // Setzt die minimale Breite
        primaryStage.setMinHeight(600); // Setzt die minimale Höhe
        primaryStage.setTitle("Undead Power Struggle");
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
