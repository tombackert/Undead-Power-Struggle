package ups.view;

import javafx.util.Pair;
import ups.controller.CreatorsController;
import java.util.List;
import ups.controller.MenuController;

/**
 * Displays the creators of the game.
 */
public class CreatorsMenuView extends BaseMenuView {

    public static String[][] title = {
        {"Erbauer", "Creators"}
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
        return "bg3.png";
    }

    /**
     * Returns the menu data of the view.
     * @return the menu data of the view
     */
    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return CreatorsController.getMenuData(primaryStage);
    }

    /**
     * The main method of the view.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
