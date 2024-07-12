package ups.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.CreatorsMenuView;
import ups.view.KingdomBuilderCardsView;
import ups.view.TutorialView;
import ups.view.LocationCardsView;
import ups.view.GameMenuView;
import java.util.Arrays;
import java.util.List;

/**
 * Controls the start menu view.
 */
public class MenuController {

    // language variable for main menu
    public static String language;

    // language index: 0 = German, 1 = English
    public static int languageIndex = 0;

    // German and English translations for the main menu items
    public static String[][] menuItems = {
        { "Spielen", "Play" },
        { "Kingdom Builder Karten", "Kingdom Builder Cards" },
        { "Ortskarten", "Location Cards" },
        { "Anleitung", "Tutorial" },
        { "Erbauer", "Creators" },
        { "Beenden", "Exit" }
    };
    
    

    /**
     * Returns the menu data of the view.
     *
     * @param primaryStage the primary stage
     * @return the menu data of the view
     */
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {

        return Arrays.asList(

            // Start the game        
                new Pair<>( menuItems[0][languageIndex], () -> {
                    try {
                        new GameMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Display the kingdom builder cards
                new Pair<>(menuItems[1][languageIndex], () -> {
                    try {
                        new KingdomBuilderCardsView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Display the location cards
                new Pair<>(menuItems[2][languageIndex], () -> {
                    try {
                        new LocationCardsView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Display the tutorial (game instructions)
                new Pair<>(menuItems[3][languageIndex], () -> {
                    try {
                        new TutorialView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Display the creators of the game
                new Pair<>(menuItems[4][languageIndex], () -> {
                    try {
                        new CreatorsMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Exit to desktop
                new Pair<>(menuItems[5][languageIndex], Platform::exit)

        );

    }

    public void openGameMenu(Stage primaryStage) {
        try {
            GameMenuView gameMenuView = new GameMenuView();
            gameMenuView.start(new Stage()); // This starts the GameMenuView and assigns the menuStage

            // Assuming we need to set the primary stage in GameMenuController
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("src\\main\\resources\\ups\\view\\GameMenuView.fxml"));
            Parent root = loader.load();
            GameMenuController gameMenuController = loader.getController();
            gameMenuController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initLanguage() {
        language = "de";
        languageIndex = 0;
    }
}
