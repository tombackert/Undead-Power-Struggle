package ups.controller;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

import java.util.Arrays;
import java.util.List;

public class OptionsController {

    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                new Pair<>("Theme Settings", () -> System.out.println("Theme Options clicked")),
                new Pair<>("Change AI Difficulty", () -> System.out.println("AI Difficulty Options clicked")),
                new Pair<>("Sound Settings", () -> System.out.println("Sound Options clicked")),
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