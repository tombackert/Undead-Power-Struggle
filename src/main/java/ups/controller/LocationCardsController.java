package ups.controller;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;
import ups.view.LocationCardsView;

/**
 * Controls the location cards view.
 */
public class LocationCardsController {

    public static LocationCardsView locationCardsView;
    
    public static String[][] menuItems = {
        {"Gras", "Grass"},
        {"Blumen", "Flowers"},
        {"Wald", "Forest"},
        {"Canyon", "Canyon"},
        {"Wüste", "Desert"},
        {"Zurück", "Back"}
    };

    public static String cardPath = "src/main/resources/location-cards/";

    public static String[][] cardPaths = {
        
        {"Gras.png", "Gras_blood.png" },
        {"Blumen.png", "Blumen_blood.png"},
        {"Wald.png", "Wald_blood.png"},
        {"Canyon.png", "Canyon_blood.png"},
        {"Wüste.png", "Wüste_blood.png"}
    };


    /**
     * Returns the menu data of the view.
     *
     * @param primaryStage the primary stage
     * @return the menu data of the view
     */
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        
        return Arrays.asList(

            // Show Grass card
            new Pair<>(menuItems[0][MenuController.languageIndex], () -> {
                try {
                    if (MenuController.theme == 0) {
                        locationCardsView.showCard(cardPath + cardPaths[0][0]);
                    } else {
                        locationCardsView.showCard(cardPath + cardPaths[0][1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                            
            // Show Flowers card
            new Pair<>(menuItems[1][MenuController.languageIndex], () -> {
                try {
                    if (MenuController.theme == 0) {
                        locationCardsView.showCard(cardPath + cardPaths[1][0]);
                    } else {
                        locationCardsView.showCard(cardPath + cardPaths[1][1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                            
            // Show Forest card
            new Pair<>(menuItems[2][MenuController.languageIndex], () -> {
                try {
                    if (MenuController.theme == 0) {
                        locationCardsView.showCard(cardPath + cardPaths[2][0]);
                    } else {
                        locationCardsView.showCard(cardPath + cardPaths[2][1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                            
            // Show Canyon card
            new Pair<>(menuItems[3][MenuController.languageIndex], () -> {
                try {
                    if (MenuController.theme == 0) {
                        locationCardsView.showCard(cardPath + cardPaths[3][0]);
                    } else {
                        locationCardsView.showCard(cardPath + cardPaths[3][1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
        
            // Show Desert card
            new Pair<>(menuItems[4][MenuController.languageIndex], () -> {
                try {
                    if (MenuController.theme == 0) {
                        locationCardsView.showCard(cardPath + cardPaths[4][0]);
                    } else {
                        locationCardsView.showCard(cardPath + cardPaths[4][1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }),
                
            
            // Go back one stage
            new Pair<>(menuItems[5][MenuController.languageIndex], () -> {
                try {
                    new MenuView().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
        );
    }
}
