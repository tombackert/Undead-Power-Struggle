package ups.controller;

import javafx.application.Platform;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class MenuController {

    public static List<Pair<String, Runnable>> getMenuData() {
        return Arrays.asList(
                new Pair<>("Single Player", () -> System.out.println("Single Player clicked")),
                new Pair<>("Multiplayer", () -> System.out.println("Multiplayer clicked")),
                new Pair<>("Game Options", () -> System.out.println("Game Options clicked")),
                new Pair<>("Additional Content", () -> System.out.println("Additional Content clicked")),
                new Pair<>("Tutorial", () -> System.out.println("Tutorial clicked")),
                new Pair<>("Benchmark", () -> System.out.println("Benchmark clicked")),
                new Pair<>("Credits", () -> System.out.println("Credits clicked")),
                new Pair<>("Exit to Desktop", Platform::exit)
        );
    }
}
