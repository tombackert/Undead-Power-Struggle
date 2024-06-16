package ups.view;

import java.util.List;

import javafx.util.Pair;
import ups.controller.KingdomBuilderCardsController;

public class KingdomBuilderCardsView extends BaseMenuView{
    
    @Override
    protected String getTitle() {
        return "Kingdom Builder Cards";
    }

    @Override
    protected String getTitleText() {
        return "Kingdom Builder Cards";
    }

    @Override
    protected String getBackgroundImage() {
        return "bg8.png";
    }

    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return KingdomBuilderCardsController.getMenuData(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
