package ups.view;

import javafx.util.Pair;
import ups.controller.CreditsController;

import java.util.List;



public class CreditsMenuView extends BaseMenuView {
    
    @Override
    protected String getTitle() {
        return "Credits";
    }

    @Override
    protected String getTitleText() {
        return "Credits";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg3.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return CreditsController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
