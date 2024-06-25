package ups.view;

import javafx.util.Pair;
import ups.controller.OptionsController;

import java.util.List;

public class OptionsMenuView extends BaseMenuView {

    @Override
    protected String getTitle() {
        return "Game Options";
    }

    @Override
    protected String getTitleText() {
        return "OPTIONS";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg5.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return OptionsController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}