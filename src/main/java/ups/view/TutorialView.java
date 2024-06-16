package ups.view;

import java.util.List;

import javafx.util.Pair;
import ups.controller.TutorialController;

public class TutorialView extends BaseMenuView{
    
    @Override
    protected String getTitle() {
        return "Tutorial";
    }

    @Override
    protected String getTitleText() {
        return "Tutorial";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg1.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return TutorialController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
