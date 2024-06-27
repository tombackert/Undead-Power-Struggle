package ups.view;

import javafx.util.Pair;
import ups.controller.MenuController;

import java.util.List;

public class MenuView extends BaseMenuView {

    @Override
    protected String getTitle() {
        return "Undead Power Struggle";
    }

    @Override
    protected String getTitleText() {
        return "Undead Power Struggle";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg6.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return MenuController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
