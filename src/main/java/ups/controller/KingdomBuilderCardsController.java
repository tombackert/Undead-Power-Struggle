package ups.controller;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

/**
 * Controls the kingdom builder cards view.
 */
public class KingdomBuilderCardsController {

    public static String[][] menuItems = {
        {"Zurück", "Back"}
    };
    
    /**
     * Returns the menu data of the view.
     *
     * @param primaryStage the primary stage
     * @return the menu data of the view
     */
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        
        return Arrays.asList(
                
            // Go back one stage
            new Pair<>(menuItems[0][MenuController.languageIndex], () -> {
                try {
                    new MenuView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );

    }
}
