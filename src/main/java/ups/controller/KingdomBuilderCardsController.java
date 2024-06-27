package ups.controller;

import java.util.Arrays;
import java.util.List;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

public class KingdomBuilderCardsController {
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                
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
