// GameMenuController.java
package ups.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The GameMenuController class is responsible for handling the game menu.
 */
public class GameMenuController {
    private static final Logger logger = Logger.getLogger(GameMenuController.class.getName());
    private Stage primaryStage;

    @FXML
    private TextField player1Field;
    @FXML
    private ComboBox<String> color1ComboBox;
    @FXML
    private TextField player2Field;
    @FXML
    private ComboBox<String> color2ComboBox;
    @FXML
    private TextField player3Field;
    @FXML
    private ComboBox<String> color3ComboBox;
    @FXML
    private TextField player4Field;
    @FXML
    private ComboBox<String> color4ComboBox;

    /**
     * Sets the primary stage for the game menu.
     *
     * @param primaryStage the primary stage
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Starts a new game with the entered player names and colors.
     */
    @FXML
    public void startNewGame() {
        List<String> playerNames = getPlayerNames(); // Get player names
        List<Color> playerColors = getPlayerColors(); // Get player colors

        if (playerNames.size() < 2) { // Check if at least two player names are entered
            System.out.println("Es müssen mindestens zwei Spielernamen eingegeben werden.");
            return;
        }

        if (playerColors == null) { // Check if player colors are valid
            System.out.println("Jeder Spieler muss eine eindeutige Farbe auswählen.");
            return;
        }

        try {
            GameBoardController gameBoardGui = new GameBoardController(); // Create new game board
            gameBoardGui.setPlayers(playerNames.toArray(new String[0]), playerColors.toArray(new Color[0])); // Set player names and colors
            gameBoardGui.start(new Stage()); // Start game
            primaryStage.hide(); // Hide game menu
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start new game", e);
        }
    }

    /**
     * Exits the game.
     */
    public void exitGame() {
        System.exit(0);
    }

    /**
     * Returns a list of player names.
     *
     * @return a list of player names
     */
    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>(); // Create new list of player names
        if (!player1Field.getText().trim().isEmpty()) { // Check if player name is not empty
            names.add(player1Field.getText().trim()); // Add player name to list
        }
        if (!player2Field.getText().trim().isEmpty()) {
            names.add(player2Field.getText().trim());
        }
        if (!player3Field.getText().trim().isEmpty()) {
            names.add(player3Field.getText().trim());
        }
        if (!player4Field.getText().trim().isEmpty()) {
            names.add(player4Field.getText().trim());
        }
        return names; // Return list of player names
    }

    /**
     * Returns a list of player colors.
     *
     * @return a list of player colors
     */
    private List<Color> getPlayerColors() {
        List<String> colorNames = new ArrayList<>(); // Create new list of color names
        if (!player1Field.getText().trim().isEmpty() && color1ComboBox.getValue() != null) { // Check if player name and color are not empty
            colorNames.add(color1ComboBox.getValue()); // Add color name to list
        }
        if (!player2Field.getText().trim().isEmpty() && color2ComboBox.getValue() != null) {
            colorNames.add(color2ComboBox.getValue());
        }
        if (!player3Field.getText().trim().isEmpty() && color3ComboBox.getValue() != null) {
            colorNames.add(color3ComboBox.getValue());
        }
        if (!player4Field.getText().trim().isEmpty() && color4ComboBox.getValue() != null) {
            colorNames.add(color4ComboBox.getValue());
        }

        if (colorNames.size() != new HashSet<>(colorNames).size()) { // Check for duplicate colors
            return null; // Duplicate colors found
        }

        List<Color> colors = new ArrayList<>(); // Create new list of colors
        for (String colorName : colorNames) { // Iterate over color names
            colors.add(ColorMapping.getColorFromString(colorName)); // Add color to list
        }

        return colors; // Return list of colors
    }
}
