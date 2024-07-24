package ups.model;

import javafx.beans.binding.Bindings;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Objects;

/**
 * Item objet for start menu. Extends Pane class.
 */
public class MenuItem extends Pane {

    // Attributes of MenuItem object
    protected Text text;
    protected Polygon bg;
    private Effect shadow = new DropShadow(10, Color.WHITE);
    private Effect blur = new BoxBlur(1, 1, 3);

    /**
     * Constructor for MenuItem object.
     * @param name name of the menu item
     */
    public MenuItem(String name) {

        bg = new Polygon(0, 0, 200, 0, 215, 15, 200, 30, 0, 30);
        
        bg.setStroke(Color.color(1, 1, 1, 0.75));
        bg.setEffect(new GaussianBlur());

        bg.fillProperty().bind(
            Bindings.when(pressedProperty())
                    .then(Color.color(0, 0, 0, 0.75))
                    .otherwise(Color.color(0, 0, 0, 0.75))
        );

        text = new Text(name);
        text.setTranslateX(5);
        text.setTranslateY(20);
        
        text.setFont(Font.loadFont(
            Objects.requireNonNull(MenuItem.class.getResource("Penumbra-HalfSerif-Std_35114.ttf")).toExternalForm(),
                13));
        
        text.setFill(Color.WHITE);
        
        text.effectProperty().bind(
            Bindings.when(hoverProperty())
                    .then(shadow)
                    .otherwise(blur)
        );

        getChildren().addAll(bg, text);
    }

    /**
     * Set action for menu item.
     * @param action action to be set
     */
    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    }

    /**
     * Get text of menu item.
     * @return text of menu item
     */
    public String getText() {
        return text.getText();
    }

    /**
     * Set text of menu item.
     * @param newText new text of menu item
     */
    public void setText(String newText) {
        text.setText(newText);
    }
}
