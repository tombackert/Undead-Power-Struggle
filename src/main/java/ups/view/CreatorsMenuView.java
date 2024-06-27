package ups.view;

import javafx.util.Pair;
import ups.controller.CreatorsController;

import java.util.List;



public class CreatorsMenuView extends BaseMenuView {
    
    @Override
    protected String getTitle() {
        return "Creators";
    }

    @Override
    protected String getTitleText() {
        return "Creators";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg3.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return CreatorsController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
