package ups.controller;

import java.util.Arrays;
import java.util.List;

import javafx.stage.Stage;
import javafx.util.Pair;
import ups.view.MenuView;

public class CreatorsController {
    
    public static List<Pair<String, Runnable>> getMenuData(Stage primaryStage) {
        return Arrays.asList(
                
            new Pair<>("@Christopher Horn", () -> System.out.println("christopher.horn@student.uni-luebeck.de")),
                    
            new Pair<>("@Baris Kaya", () -> System.out.println("baris.kaya@student.uni-luebeck.de")),
                    
            new Pair<>("@Marvin Detzkeit", () -> System.out.println("marvin.detzkeit@student.uni-luebeck.de")),
                    
            new Pair<>("@Tom Backert", () -> System.out.println("tom.backert@student.uni-luebeck.de")),
                    
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
