package ups.controller;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;
import ups.model.MenuItem;
import ups.model.PlayerMenuItem;
import ups.view.GameMenuView;

import java.util.Arrays;
import java.util.List;

public class SinglePlayerController {

    private static String player1Name = "Player Name";

    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(

                new Pair<>("Start Game", () -> {
                    try {
                        new GameMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                new Pair<>(player1Name, () -> updatePlayerName(1)),
                new Pair<>("Back", () -> {
                    try {
                        new MenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private static void updatePlayerName(int playerNumber) {
        // This method is now handled by the PlayerMenuItem class, so no further action is required here.
    }

    public static MenuItem createMenuItem(String name, boolean isPlayer) {
        if (isPlayer) {
            return new PlayerMenuItem(name);
        } else {
            return new MenuItem(name);
        }
    }
}
