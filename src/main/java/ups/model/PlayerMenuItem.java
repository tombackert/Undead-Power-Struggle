package ups.model;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;


public class PlayerMenuItem extends MenuItem {

    private TextField textField;
    public static Color[] colors = {Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED};
    private static boolean[] colorTaken = {false, false, false, false}; // to track selected colors
    public static int currentColorIndex = 0;

    public PlayerMenuItem(String name) {
        super(name);
        customizeAppearance();
    }

    private void customizeAppearance() {
        // Change the size of the background polygon
        bg.getPoints().setAll(0.0, 0.0, 150.0, 0.0, 165.0, 15.0, 150.0, 30.0, 0.0, 30.0);

        text.setFill(colors[currentColorIndex]);

        textField = new TextField(getText());
        textField.setTranslateX(5);
        textField.setTranslateY(5);
        textField.setVisible(false);

        text.setOnMouseClicked(e -> {
            text.setVisible(false);
            textField.setVisible(true);
            textField.requestFocus();
        });

        textField.setOnAction(e -> updateText());
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                updateText();
            }
        });

        bg.setOnMouseClicked(e -> changeColor());

        getChildren().addAll(textField);
    }

    private void updateText() {
        String newText = textField.getText();
        text.setText(newText);
        textField.setVisible(false);
        text.setVisible(true);
        System.out.println("Player name changed to: " + newText);
    }

    private void changeColor() {
        int oldColorIndex = currentColorIndex;
        do {
            currentColorIndex = (currentColorIndex + 1) % colors.length;
        } while (colorTaken[currentColorIndex] && currentColorIndex != oldColorIndex);

        if (currentColorIndex != oldColorIndex) {
            colorTaken[oldColorIndex] = false;
            colorTaken[currentColorIndex] = true;
            text.setFill(colors[currentColorIndex]);
            System.out.println("Player color changed to: " + colors[currentColorIndex]);
        }
    }

    @Override
    public void setOnAction(Runnable action) {
        bg.setOnMouseClicked(e -> {
            changeColor();
            action.run();
        });
    }
}
