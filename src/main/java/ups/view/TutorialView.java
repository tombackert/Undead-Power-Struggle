package ups.view;

import java.util.List;
import javafx.util.Pair;
import ups.controller.TutorialController;
import ups.controller.MenuController;


/**
 * Tutorial View. Displays the game rules and how to play the game.
 */
public class TutorialView extends BaseMenuView {
    
    public static String[][] title = {
        {"Anleitung", "Tutorial"}
    };
    
    /**
     * Returns the title of the view.
     * @return the title of the view
     */
    @Override
    protected String getTitle() {
        return title[0][MenuController.languageIndex];
    }

    /**
     * Returns the title text of the view.
     * @return the title text of the view
     */
    @Override
    protected String getTitleText() {
        return title[0][MenuController.languageIndex];
    }

    /**
     * Returns the background image of the view.
     * @return the background image of the view
     */
    @Override
    protected String getBackgroundImage() {
        return "bg1.png";
    }

    /**
     * Returns the menu data of the view.
     * @return the menu data of the view
     */
    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return TutorialController.getMenuData(primaryStage);
    }

    /**
     * The main method of the view.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
