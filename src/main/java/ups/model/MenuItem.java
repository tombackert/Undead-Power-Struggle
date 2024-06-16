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


public class MenuItem extends Pane {
    
    // Text for menu item
    private Text text;

    // Effects for menu item
    private Effect shadow = new DropShadow(10, Color.BLACK);
    private Effect blur = new BoxBlur(1, 1, 3);

    // Constructor for menu item
    public MenuItem(String name) {
        // Background polygon for menu item
        Polygon bg = new Polygon(0, 0, 200, 0, 215, 15, 200, 30, 0, 30);

        // Set stroke color and effect
        bg.setStroke(Color.color(1, 1, 1, 0.75));

        // Set blur effect
        bg.setEffect(new GaussianBlur());

        // Bind fill property to pressed property
        bg.fillProperty().bind(
                Bindings.when(pressedProperty())
                        .then(Color.color(0, 0, 0, 0.75))
                        .otherwise(Color.color(0, 0, 0, 0.75)));

        // Text for menu item
        text = new Text(name);

        // Set text position
        text.setTranslateX(5);
        text.setTranslateY(20);

        // Set font and fill color
        text.setFont(Font.loadFont(
                Objects.requireNonNull(MenuItem.class.getResource("Penumbra-HalfSerif-Std_35114.ttf")).toExternalForm(),
                14));

        // Set text color
        text.setFill(Color.WHITE);

        // Bind effect property to hover property
        text.effectProperty().bind(
                Bindings.when(hoverProperty())
                        .then(shadow)
                        .otherwise(blur));

        // Add background and text to menu item
        getChildren().addAll(bg, text);
    }

    // Function to set action on menu item click
    public void setOnAction(Runnable action) {
        setOnMouseClicked(e -> action.run());
    }
}

