package ups.controller;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

/**
 * Controls the creators view.
 */
public class CreatorsController {

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
        
        // list of all creators
        return Arrays.asList(
            
            new Pair<>("@Christopher Horn", () -> System.out.println("christopher.horn@student.uni-luebeck.de")),
                    
            new Pair<>("@Baris Kaya", () -> System.out.println("baris.kaya@student.uni-luebeck.de")),
                    
            new Pair<>("@Marvin Detzkeit", () -> System.out.println("marvin.detzkeit@student.uni-luebeck.de")),
                    
            new Pair<>("@Tom Backert", () -> System.out.println("tom.backert@student.uni-luebeck.de")),
               
            // Go back one stage
            new Pair<>( menuItems[0][MenuController.languageIndex], () -> {
                try {
                    new MenuView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );

    }

}
