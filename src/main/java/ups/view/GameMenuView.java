package ups.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ups.controller.GameMenuController;

import java.util.Objects;

/**
 * The GameMenuView class is responsible for displaying the game menu.
 */
public class GameMenuView extends Application {
    private static Stage primaryStage; // The primary stage for the game menu

    /**
     * Starts the game menu.
     *
     * @param primaryStage the primary stage
     * @throws Exception if an error occurs
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameMenuView.primaryStage = primaryStage; // Set the primary stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameMenuView.fxml")); // Load the FXML file
        Parent root = loader.load(); // Load the root

        GameMenuController controller = loader.getController(); // Get the controller
        controller.setPrimaryStage(primaryStage); // Set the primary stage

        primaryStage.setTitle("Undead Power Struggle - Hauptmenü"); // Set the title
        primaryStage.setScene(new Scene(root, 1024, 768)); // Set the scene
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))); // Load the icon
        primaryStage.getIcons().add(icon); // Add the icon
        primaryStage.show(); // Show the stage
    }

    /**
     * Shows the game menu.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Shows the game menu.
     */
    public static void showMenu() {
            primaryStage.show(); // Show the primary stage
    }
}
