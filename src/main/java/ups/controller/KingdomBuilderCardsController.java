package ups.controller;

import java.util.Arrays;
import java.util.List;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.KingdomBuilderCardsView;
import ups.view.MenuView;

/**
 * Controls the kingdom builder cards view.
 */
public class KingdomBuilderCardsController {

    public static KingdomBuilderCardsView kingdomBuilderCardsView;

    public static String[][] menuItems = {
        
        // Default Theme
        {"Fischer", "Fishermen"},
        {"Bergleute", "Miners"},
        {"Händler", "Merchants"},
        {"Arbeiter", "Workers"},
        {"Entdecker", "Explorers"},
        {"Ritter", "Knights"},
        {"Einsiedler", "Hermits"},
        {"Lords", "Lords"},
        {"Bürger", "Citizens"},
        {"Bauern", "Farmers"},
        
        // Zombie Theme
        {"Zombiefisch", "Zombiefish"},
        {"Zombiebergleute", "Zombieminers"},
        {"Zombiehändler", "Zombiemerchants"},
        {"Zombiearbeiter", "Zombieworkers"},
        {"Zombieentdecker", "Zombieexplorers"},
        {"Zombieritter", "Zombieknights"},
        {"Zombieeinsiedler", "Zombiehermits"},
        {"Zombielords", "Zombielords"},
        {"Zombiebürger", "Zombiecitizens"},
        {"Zombiebauern", "Zombiefarmers"},        
                
        {"Zurück", "Back"}
    };

    public static String cardPath = "src/main/resources/kingdom-builder-cards/";

    public static String[][] cardPaths = {
        {"Fischer/Fischer.png",
            "Fischer/Fishermen.png",
                "Fischer/ZombieFischer.png",
                    "Fischer/ZombieFishermen.png" },
        {"Bergleute/Bergleute.png",
            "Bergleute/Miners.png",
                "Bergleute/ZombieBergleute.png",
                    "Bergleute/ZombieMiners.png"},
        {"Händler/Händler.png",
            "Händler/Merchants.png",
                "Händler/ZombieHändler.png",
                    "Händler/ZombieMerchants.png" },
        {"Arbeiter/Arbeiter.png",
            "Arbeiter/Worker.png",
                "Arbeiter/ZombieArbeiter.png",
                    "Arbeiter/ZombieWorker.png"},
        {"Entdecker/Entdecker.png",
            "Entdecker/Explorers.png",
                "Entdecker/ZombieEntdecker.png",
                    "Entdecker/ZombieExplorers.png" },
        {"Ritter/Ritter.png",
            "Ritter/Knights.png",
                "Ritter/ZombieRitter.png",
                    "Ritter/ZombieKnights.png" },
        {"Einsiedler/Einsiedler.png",
            "Einsiedler/Hermits.png",
                "Einsiedler/ZombieEinsiedler.png",
                    "Einsiedler/ZombieHermits.png"},
        {"Lords/Lords_ger.png",
            "Lords/Lords_eng.png",
                "Lords/ZombieLords_ger.png",
                    "Lords/ZombieLords_eng.png"},
        {"Bürger/Bürger.png",
            "Bürger/Citizens.png",
                "Bürger/ZombieBürger.png",
                    "Bürger/ZombieCitizens.png"},
        {"Bauern/Bauern.png",
            "Bauern/Farmers.png",
                "Bauern/ZombieBauern.png",
                    "Bauern/ZombieFarmers.png"},
    };
    
    /**
     * Returns the menu data of the view.
     *
     * @param primaryStage the primary stage
     * @return the menu data of the view
     */
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {

        return Arrays.asList(

                // Show Fisher card
                new Pair<>(menuItems[0 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[0][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[0][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[0][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[0][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                                
                // Show Miners card
                new Pair<>(menuItems[1 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[1][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[1][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[1][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[1][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
        
                // Show Merchants card
                new Pair<>(menuItems[2 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[2][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[2][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[2][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[2][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                                
                // Show Workers card
                new Pair<>(menuItems[3 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[3][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[3][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[3][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[3][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                                
                // Show Explorers card
                new Pair<>(menuItems[4 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[4][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[4][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[4][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[4][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                                
                // Show Knights card
                new Pair<>(menuItems[5 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[5][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[5][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[5][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[5][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                                
                // Show Hermits card
                new Pair<>(menuItems[6 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[6][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[6][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[6][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[6][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Show Lord card
                new Pair<>(menuItems[7 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[7][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[7][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[7][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[7][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                        
                // Show Citizen card
                new Pair<>(menuItems[8 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[8][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[8][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[8][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[8][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                                
                // Show Farmer card
                new Pair<>(menuItems[9 + MenuController.theme * 10][MenuController.languageIndex], () -> {
                    try {
                        if (MenuController.theme == 0) {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[9][0]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[9][1]);
                            }
                        } else {
                            if (MenuController.languageIndex == 0) {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[9][2]);
                            } else {
                                kingdomBuilderCardsView.showCard(cardPath + cardPaths[9][3]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),

                // Go back one stage
                new Pair<>(menuItems[20][MenuController.languageIndex], () -> {
                    try {
                        new MenuView().start(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));

    }

}
