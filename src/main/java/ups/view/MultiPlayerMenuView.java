package ups.view;

import javafx.util.Pair;
import ups.controller.MultiPlayerController;

import java.util.List;

public class MultiPlayerMenuView extends BaseMenuView {

    @Override
    protected String getTitle() {
        return "Multiplayer Settings";
    }

    @Override
    protected String getTitleText() {
        return "Multiplayer";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg2.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return MultiPlayerController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
