package ups.view;

import java.util.List;
import javafx.util.Pair;
import ups.controller.LocationCardsController;
import ups.controller.MenuController;

/**
 * This class is responsible for displaying the Location Cards view.
 */
public class LocationCardsView extends BaseMenuView {

    public static String[][] title = {
        {"Ortskarten", "Location Cards"}
    };
    
    /**
     * Returns the title of the view.
     * 
     * @return the title of the view
     */
    @Override
    protected String getTitle() {
        return title[0][MenuController.languageIndex];
    }

    /**
     * Returns the title text of the view.
     * 
     * @return the title text of the view
     */
    @Override
    protected String getTitleText() {
        return title[0][MenuController.languageIndex];
    }

    /**
     * Returns the background image of the view.
     * 
     * @return the background image of the view
     */
    @Override
    protected String getBackgroundImage() {
        if (MenuController.theme == 0) {
            return "bg1.png";
        } else {
            return "zombie4.png";
        }
    }

    /**
     * Returns the menu data of the view.
     * 
     * @return the menu data of the view
     */
    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return LocationCardsController.getMenuData(primaryStage);
    }

    /**
     * The main method of the view.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
