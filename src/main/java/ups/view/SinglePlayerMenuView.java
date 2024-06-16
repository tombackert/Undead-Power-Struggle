package ups.view;

import javafx.util.Pair;
import ups.controller.SinglePlayerController;

import java.util.List;

public class SinglePlayerMenuView extends BaseMenuView {

    @Override
    protected String getTitle() {
        return "Single Player Settings";
    }

    @Override
    protected String getTitleText() {
        return "Single Player";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg7.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return SinglePlayerController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}