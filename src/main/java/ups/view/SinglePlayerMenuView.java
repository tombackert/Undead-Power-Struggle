package ups.view;

import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import ups.controller.MultiPlayerController;
import ups.controller.SinglePlayerController;
import ups.model.MenuItem;

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

    @Override
    protected void addMenu(double x, double y) {
        menuBox.setTranslateX(x);
        menuBox.setTranslateY(y);

        List<Pair<String, Runnable>> menuData = getMenuData();
        for (int i = 0; i < menuData.size(); i++) {
            Pair<String, Runnable> data = menuData.get(i);
            boolean isPlayer = data.getKey().startsWith("Player");
            MenuItem item = MultiPlayerController.createMenuItem(data.getKey(), isPlayer);
            item.setOnAction(data.getValue());
            item.setTranslateX(-300);

            Rectangle clip = new Rectangle(isPlayer ? 150 : 300, 30); // Shorter for player items
            clip.translateXProperty().bind(item.translateXProperty().negate());

            item.setClip(clip);

            menuBox.getChildren().addAll(item);
        }

        root.getChildren().add(menuBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}