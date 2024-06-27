package ups.controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.CreatorsMenuView;
import ups.view.KingdomBuilderCardsView;
import ups.view.TutorialView;
import ups.view.LocationCardsView;
import ups.view.GameMenuView;
import java.util.Arrays;
import java.util.List;

/**
 * Controls the start menu view.
 */
public class MenuController {

    /**
     * Returns the menu data of the view.
     *
     * @param primaryStage the primary stage
     * @return the menu data of the view
     */
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        
        return Arrays.asList(
            
            // Start the game
            new Pair<>("Start Game", () -> {
                try {
                    new GameMenuView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
               
            // Display the kingdom builder cards
            new Pair<>("Kingdom Builder Cards", () -> {
                try {
                    new KingdomBuilderCardsView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                 
            // Display the location cards
            new Pair<>("Location Cards", () -> {
                try {
                    new LocationCardsView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),

            // Display the tutorial (game instructions)
            new Pair<>("Tutorial", () -> {
                try {
                    new TutorialView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
               
            // Display the creators of the game
            new Pair<>("Creators", () -> {
                try {
                    new CreatorsMenuView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
            
            // Exit to desktop
            new Pair<>("Exit to Desktop", Platform::exit)
            
        );

    }
}
