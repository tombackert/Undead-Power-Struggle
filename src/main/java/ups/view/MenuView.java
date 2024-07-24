package ups.view;

import javafx.scene.layout.Pane;
import javafx.util.Pair;
import ups.controller.MenuController;
import java.util.List;
import ups.controller.MenuController;

/**
 * The start menu view.
 */
public class MenuView extends BaseMenuView {

    /**
     * Returns the title of the view.
     *
     * @return the title of the view
     */
    @Override
    protected String getTitle() {
        return "Undead Power Struggle";
    }

    /**
     * Returns the title text of the view.
     *
     * @return the title text of the view
     */
    @Override
    protected String getTitleText() {
        return "Undead Power Struggle";
    }

    /**
     * Returns the background image of the view.
     *
     * @return the background image of the view
     */
    @Override
    protected String getBackgroundImage() {
        if (MenuController.theme == 0) {
            return "bg6.png";
        } else {
            return "zombie1.png";
        }
    }

    /**
     * Returns the menu data of the view.
     *
     * @return the menu data of the view
     */
    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return MenuController.getMenuData(primaryStage);
    }

    /**
     * The main method of the view.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MenuController.initLanguage();
        MenuController.initTheme();
        launch(args);
    }

}
