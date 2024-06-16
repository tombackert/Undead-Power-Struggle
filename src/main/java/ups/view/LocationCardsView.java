package ups.view;

import java.util.List;

import javafx.util.Pair;
import ups.controller.LocationCardsController;

public class LocationCardsView extends BaseMenuView{
    
    @Override
    protected String getTitle() {
        return "Location Cards";
    }

    @Override
    protected String getTitleText() {
        return "Location Cards";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg1.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return LocationCardsController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
