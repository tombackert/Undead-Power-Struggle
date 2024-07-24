package ups.controller;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;
import ups.view.TutorialView;

/**
 * Controls the tutorial view.
 */
public class TutorialController {

    public static TutorialView tutorialView;

    public static String[][] menuItems = {

        // Tutorial Steps
        {"Sprache und Design", "Language and Theme"},
        {"Spieler Konfiguration", "Player Configuration"},
        {"Siedlungen", "Settlements"},
        {"KDB-Karten wählen", "Choose KDB-Cards"},
        {"Geländekarte / Häuser", "Terraincard / Placehouses"},
        {"Zusätzliche Einstellungen", "Additional Settings"},
        {"Zurück", "Back"}
    };

    public static String tutorialPath = "src/main/resources/tutorial/";

    public static String[][] tutorialPaths = {

        {"language_and_theme_ger.png", "language_and_theme_eng.png"},
        {"player_config_ger.png", "player_config_eng.png"},
        {"set_settlements_ger.png", "set_settlements_eng.png"},
        {"choose_kdbcards_ger.png", "choose_kdbcards_eng.png"},
        {"terraincard_placehouses_ger.png", "terraincard_placehouses_eng.png"},
        {"additional_settings_ger.png", "additional_settings_eng.png" }
        
    };


    /**
     * Returns the menu data of the view.
     *
     * @param primaryStage the primary stage
     * @return the menu data of the view
     */
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        
        return Arrays.asList(
            
            // Show the first tutorial step
            new Pair<>(menuItems[0][MenuController.languageIndex], () -> {
                try {
                    tutorialView.showTutorial(tutorialPath + tutorialPaths[0][MenuController.languageIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                        
            // Show the second tutorial step
            new Pair<>(menuItems[1][MenuController.languageIndex], () -> {
                try {
                    tutorialView.showTutorial(tutorialPath + tutorialPaths[1][MenuController.languageIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                            
            // Show the third tutorial step
            new Pair<>(menuItems[2][MenuController.languageIndex], () -> {
                try {
                    tutorialView.showTutorial(tutorialPath + tutorialPaths[2][MenuController.languageIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                            
            // Show the fourth tutorial step
            new Pair<>(menuItems[3][MenuController.languageIndex], () -> {
                try {
                    tutorialView.showTutorial(tutorialPath + tutorialPaths[3][MenuController.languageIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                            
            // Show the fifth tutorial step
            new Pair<>(menuItems[4][MenuController.languageIndex], () -> {
                try {
                    tutorialView.showTutorial(tutorialPath + tutorialPaths[4][MenuController.languageIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
    
            // Show the sixth tutorial step
            new Pair<>(menuItems[5][MenuController.languageIndex], () -> {
                try {
                    tutorialView.showTutorial(tutorialPath + tutorialPaths[5][MenuController.languageIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),


            // Go back one stage
            new Pair<>(menuItems[6][MenuController.languageIndex], () -> {
                try {
                    new MenuView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );
    }
}
