package ups.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Objects;
import ups.controller.GameMenuController;

/**
 * The GameMenuView class is responsible for displaying the game menu.
 */
public class GameMenuView extends Application {
    private static Stage menuStage; // The stage for the game menu
    private static Stage gameStage; // The stage for the game

    /**
     * Starts the game menu.
     *
     * @param menuStage the menu stage
     * @throws Exception if an error occurs
     */
    @Override
    public void start(Stage menuStage) throws Exception {
        GameMenuView.menuStage = menuStage; // Set the menu stage
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameMenuView.fxml")); // Load the FXML file
        Parent root = loader.load(); // Load the root

        GameMenuController controller = loader.getController(); // Get the controller
        controller.setMenuStage(menuStage); // Set the menu stage

        menuStage.setTitle("Undead Power Struggle - Hauptmenü"); // Set the title
        menuStage.setScene(new Scene(root, 1488, 850)); // Set the scene
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))); // Load the icon
        menuStage.getIcons().add(icon); // Add the icon
        menuStage.show(); // Show the stage
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
        if (menuStage != null) {
            menuStage.show(); // Show the menu stage
        } else {
            System.out.println("menuStage is null.");
        }
        if (gameStage != null) {
            gameStage.hide(); // Hide the game stage
        }
    }

    /**
     * Continues the game.
     */
    public static void continueGame() {
        System.out.println("Continue game stage: " + gameStage);
        if (gameStage != null) {
            gameStage.show();
            if (menuStage != null) {
                menuStage.hide(); // Hide the menu stage
            }
        }
    }

    /**
     * Sets the game stage.
     *
     * @param stage the game stage
     */
    public static void setGameStage(Stage stage) {
        System.out.println("Setting game stage: " + stage);
        gameStage = stage;
    }
}
