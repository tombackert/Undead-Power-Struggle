package ups.controller;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

import java.util.Arrays;
import java.util.List;

public class MultiPlayerController {

    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                new Pair<>("Player 1", () -> selectColor(Color.RED)),
                new Pair<>("Player 2", () -> selectColor(Color.GREEN)),
                new Pair<>("Player 3", () -> selectColor(Color.BLUE)),
                new Pair<>("Player 4", () -> selectColor(Color.YELLOW)),
                new Pair<>("Back", () -> {
                    try {
                        new MenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private static void selectColor(Color color) {
        System.out.println("Selected color: " + color.toString());
        // Add functionality to save the selected color and proceed with the game setup
    }
}
