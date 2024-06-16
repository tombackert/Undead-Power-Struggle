package ups.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MultiPlayerMenuView;
import ups.view.OptionsMenuView;
import ups.view.SinglePlayerMenuView;

import java.util.Arrays;
import java.util.List;

public class MenuController {

    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                new Pair<>("Single Player", () -> {
                    try {
                        new SinglePlayerMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                new Pair<>("Multiplayer", () -> {
                    try {
                        new MultiPlayerMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                new Pair<>("Game Options", () -> {
                    try {
                        new OptionsMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                new Pair<>("Additional Content", () -> System.out.println("Additional Content clicked")),
                new Pair<>("Tutorial", () -> System.out.println("Tutorial clicked")),
                new Pair<>("Benchmark", () -> System.out.println("Benchmark clicked")),
                new Pair<>("Credits", () -> System.out.println("Credits clicked")),
                new Pair<>("Exit to Desktop", Platform::exit)
        );
    }
}
