package ups.model;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Objects;



public class Title extends Pane {
    
    // Text for title
    private Text text;

    // Constructor for title
    public Title(String name) {
       
        // Create spread string
        String spread = "";
        for (char c : name.toCharArray()) {
            spread += c + " ";
        }

        // Title text
        text = new Text(spread);
        
        // Set font and fill color
        text.setFont(Font.loadFont(Objects.requireNonNull(Title.class.getResource("Penumbra-HalfSerif-Std_35114.ttf")).toExternalForm(), 48));
        text.setFill(Color.WHITE);

        // Set effect
        text.setEffect(new DropShadow(30, Color.BLACK));

        // Add text to pane
        getChildren().addAll(text);
    }

    // Get width of title
    public double getTitleWidth() {
        return text.getLayoutBounds().getWidth();
    }

    // Get height of title
    public double getTitleHeight() {
        return text.getLayoutBounds().getHeight();
    }
}