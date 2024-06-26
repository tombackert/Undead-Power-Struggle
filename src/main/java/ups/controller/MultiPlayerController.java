package ups.controller;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.model.MenuItem;
import ups.model.PlayerMenuItem;
import ups.view.MenuView;

import java.util.Arrays;
import java.util.List;

public class MultiPlayerController {

    private static String player1Name = "Player 1";
    private static String player2Name = "Player 2";
    private static String player3Name = "Player 3";
    private static String player4Name = "Player 4";

    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(

                new Pair<>("Start Game", () -> System.out.println("Game started!!!")),
                new Pair<>(player1Name, () -> updatePlayerName(1)),
                new Pair<>(player2Name, () -> updatePlayerName(2)),
                new Pair<>(player3Name, () -> updatePlayerName(3)),
                new Pair<>(player4Name, () -> updatePlayerName(4)),
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
            PlayerMenuItem.currentColorIndex = (PlayerMenuItem.currentColorIndex + 1) % PlayerMenuItem.colors.length;
            return new PlayerMenuItem(name);
        } else {
            return new MenuItem(name);
        }
    }
}
