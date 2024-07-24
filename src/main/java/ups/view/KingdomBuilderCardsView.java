package ups.view;

import ups.view.BaseMenuView;
import java.util.List;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;
import ups.controller.KingdomBuilderCardsController;
import ups.controller.MenuController;

/**
 * This class is responsible for displaying the Kingdom Builder Cards view.
 */
public class KingdomBuilderCardsView extends BaseMenuView {
    
    public static String[][] title = {
        {"Kingdom Builder Karten", "Kingdom Builder Cards"}
    };
    
    /**
     * Returns the title of the view.
     * 
     * @return the title of the view
     */
    @Override
    protected String getTitle() {
        return title[0][MenuController.languageIndex];
    }

    /**
     * Returns the title text of the view.
     * 
     * @return the title text of the view
     */
    @Override
    protected String getTitleText() {
        return title[0][MenuController.languageIndex];
    }

    /**
     * Returns the background image of the view.
     * 
     * @return the background image of the view
     */
    @Override
    protected String getBackgroundImage() {
        if (MenuController.theme == 0) {
            return "bg8.png";
        } else {
            return "zombie3.png";
        }
    }

    /**
     * Returns the menu data of the view.
     * 
     * @return the menu data of the view
     */
    @Override
    protected List<Pair<String, Runnable>> getMenuData() {
        return KingdomBuilderCardsController.getMenuData(primaryStage);
    }

    /**
     * Add line to menu view.
     */
    @Override
    protected void addLine() {
        BaseMenuView.line = new Line(BaseMenuView.WIDTH / 2, BaseMenuView.HEIGHT / 3 + 50, BaseMenuView.WIDTH / 2, BaseMenuView.HEIGHT / 3 + 500);
        BaseMenuView.line.setStrokeWidth(3);
        BaseMenuView.line.setStroke(Color.color(1, 1, 1, 0.75));
        BaseMenuView.line.setEffect(new DropShadow(5, Color.BLACK));
        BaseMenuView.line.setScaleY(0);

        // Bind the line's X position to be slightly left of the menuBox
        BaseMenuView.line.startXProperty().bind(menuBox.translateXProperty().subtract(10));
        BaseMenuView.line.endXProperty().bind(menuBox.translateXProperty().subtract(10));

        root.getChildren().add(line);
    }

    /**
     * The main method of the view.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
