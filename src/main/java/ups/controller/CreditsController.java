package ups.controller;

import java.util.Arrays;
import java.util.List;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

public class CreditsController {
    
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                new Pair<>("Christopher Horn", null),
                new Pair<>("Baris Kaya", null),
                new Pair<>("Marvin Detzkeit", null),
                new Pair<>("Tom Backert", null),                        
                new Pair<>("Back", () -> {
                    try {
                        new MenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        );
    }

}
