package ups.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
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
        GridPane gridPane = new GridPane();

        VBox vBox = new VBox();
        HBox hbox = new HBox(gridPane, vBox);
        hbox.setFillHeight(true); // Setzt die Kind-Elemente, um die volle Höhe zu füllen

        HBox.setHgrow(gridPane, Priority.ALWAYS); // Erlaubt dem GridPane, horizontal zu wachsen
        HBox.setHgrow(vBox, Priority.ALWAYS); // Erlaubt dem VBox, horizontal zu wachsen

        gridPane.setPadding(new Insets(100)); // Angemessenes Padding
        gridPane.setHgap(0); // Kein horizontaler Abstand
        gridPane.setVgap(0); // Kein vertikaler Abstand
        gridPane.prefWidthProperty().bind(primaryStage.widthProperty());
        gridPane.prefHeightProperty().bind(primaryStage.heightProperty());

        GameBoard model = new GameBoard(20, 20);
        // Initialisiert das Spielfeld mit der Datei, die dem Index entspricht, an der Position (r,c)
        int[][] positions = {{0, 0}, {0, 10}, {10, 0}, {10, 10}};
        for (int i = 0; i < positions.length; i++) {
            model.initialize(i, positions[i][0], positions[i][1]);
        }
        GameBoardView view = new GameBoardView(model);
        view.setMinSize(100, 100);
        new GameBoardController(view);

        // Bindet die Größe der GameBoardView an die Größe des TilePane
        view.prefWidthProperty().bind(gridPane.widthProperty().divide(1));
        view.prefHeightProperty().bind(gridPane.heightProperty().divide(1));

        gridPane.add(view, 0, 0); // Fügt die GameBoardView zum GridPane hinzu

        // Erstellen Sie die Buttons
        Button button1 = new Button("Button 1");
        Button button2 = new Button("Button 2");

        // Fügen Sie die Buttons zu einem HBox hinzu
        HBox buttonBox = new HBox(10, button1, button2); // 10 ist der Abstand zwischen den Buttons
        buttonBox.setPadding(new Insets(10)); // 10 ist der Abstand vom Rand des HBox

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPane);
        borderPane.setPadding(new Insets(0)); // Reduziertes Padding für das BorderPane
        borderPane.setBottom(buttonBox); // Fügt die Buttons am unteren Rand des BorderPane hinzu

        // Erstellen Sie die Scene
        Scene scene = new Scene(borderPane, 745, 720);
        primaryStage.setMinWidth(500);  // Setzt die minimale Breite
        primaryStage.setMinHeight(500); // Setzt die minimale Höhe
        primaryStage.setTitle("Undead Power Struggle");
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}