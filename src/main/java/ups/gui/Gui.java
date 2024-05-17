package ups.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import java.util.Objects;

/**
 * Starting point of the JavaFX GUI
 */
public class Gui extends Application {

    @Override
    public void start(Stage stage) {

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);

        Image i = new Image(Gui.class.getResourceAsStream("Civ6_bg.png"));

        ImageView iv = new ImageView();
        iv.setImage(i);

        layout.getChildren().addAll(iv);

        Scene scene = new Scene(layout, 880, 460);
        stage.setScene(scene);
        stage.show();


    }

    /**
     * The entry point of the GUI application.
     *
     * @param args The command line arguments passed to the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}
