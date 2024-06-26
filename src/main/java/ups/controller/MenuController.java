package ups.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.CreditsMenuView;
import ups.view.KingdomBuilderCardsView;
import ups.view.MultiPlayerMenuView;
import ups.view.OptionsMenuView;
import ups.view.SinglePlayerMenuView;
import ups.view.TutorialView;
import ups.view.LocationCardsView;
import ups.view.GameMenuView;

import java.util.Arrays;
import java.util.List;

public class MenuController {

    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                
                new Pair<>("Start Game", () -> {
                    try {
                        new GameMenuView().start(primaryStage);
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
                        
                new Pair<>("Kingdom Builder Cards", () -> {
                    try {
                        new KingdomBuilderCardsView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                        
                new Pair<>("Location Cards", () -> {
                    try {
                        new LocationCardsView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),


                new Pair<>("Tutorial", () -> {
                    try {
                        new TutorialView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                        
                new Pair<>("Credits", () -> {
                    try {
                        new CreditsMenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                        
                new Pair<>("Exit to Desktop", Platform::exit)
        );
    }
}
