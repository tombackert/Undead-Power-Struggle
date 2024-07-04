package ups.view;

import java.util.List;
import javafx.util.Pair;
import ups.controller.KingdomBuilderCardsController;

/**
 * This class is responsible for displaying the Kingdom Builder Cards view.
 */
public class KingdomBuilderCardsView extends BaseMenuView{
    
    /**
     * Returns the title of the view.
     * 
     * @return the title of the view
     */
    @Override
    protected String getTitle() {
        return "Kingdom Builder Cards";
    }

    /**
     * Returns the title text of the view.
     * 
     * @return the title text of the view
     */
    @Override
    protected String getTitleText() {
        return "Kingdom Builder Cards";
    }

    /**
     * Returns the background image of the view.
     * 
     * @return the background image of the view
     */
    @Override
    protected String getBackgroundImage() {
        return "bg8.png";
    }

    /**
     * Returns the menu data of the view.
     * 
     * @return the menu data of the view
     */
    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return KingdomBuilderCardsController.getMenuData(primaryStage);
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
