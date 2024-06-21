package ups.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.utils.LanguageSettings;
import ups.view.GameMenuView;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameMenuController {
    private static final Logger logger = Logger.getLogger(GameMenuController.class.getName());
    private Stage menuStage;

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
    @FXML
    private MenuButton languageButton;
    @FXML
    private Button newGameButton;
    @FXML
    private Button continueGameButton;
    @FXML
    private Button exitGameButton;
    @FXML
    private Button loadGameState;
    @FXML
    private Button saveGameState;
    @FXML
    private Button highscoreButton;
    @FXML
    private MenuButton themeButton;
    @FXML
    private CheckBox aiPlayer1;
    @FXML
    private CheckBox aiPlayer2;
    @FXML
    private CheckBox aiPlayer3;
    @FXML
    private CheckBox aiPlayer4;
    @FXML
    private TextField settlementsCount;
    @FXML
    private TextField settlementsPerTurn;

    private ResourceBundle bundle;

    public void setMenuStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

    @FXML
    public void initialize() {
        setLanguage(Locale.GERMAN); // Default to German

        for (MenuItem item : languageButton.getItems()) {
            item.setOnAction(event -> {
                switch (item.getText()) {
                    case "Deutsch":
                        setLanguage(Locale.GERMAN);
                        break;
                    case "English":
                        setLanguage(Locale.ENGLISH);
                        break;
                    default:
                        logger.log(Level.WARNING, "Unsupported language: " + item.getText());
                        break;
                }
            });
        }
    }

    private void setLanguage(Locale locale) {
        LanguageSettings.setCurrentLocale(locale);
        bundle = ResourceBundle.getBundle("messages", locale);
        if (bundle == null) {
            logger.log(Level.SEVERE, "ResourceBundle could not be loaded for locale: " + locale);
            return;
        }
        updateTexts();
    }

    private void updateTexts() {
        if (bundle == null) {
            logger.log(Level.SEVERE, "bundle is null.");
            return;
        }

        setButtonText(languageButton, "choose_language");
        setButtonText(newGameButton, "new_game");
        setButtonText(continueGameButton, "continue_game");
        setButtonText(exitGameButton, "exit_game");
        setButtonText(loadGameState, "load_game");
        setButtonText(saveGameState, "save_game");
        setButtonText(highscoreButton, "show_highscore");
        setButtonText(themeButton, "choose_theme");

        setPromptText(player1Field, "prompt_player1_name");
        setPromptText(player2Field, "prompt_player2_name");
        setPromptText(player3Field, "prompt_player3_name");
        setPromptText(player4Field, "prompt_player4_name");

        setPromptText(color1ComboBox, "prompt_color_select");
        setPromptText(color2ComboBox, "prompt_color_select");
        setPromptText(color3ComboBox, "prompt_color_select");
        setPromptText(color4ComboBox, "prompt_color_select");

        setText(aiPlayer1, "ai_player1");
        setText(aiPlayer2, "ai_player2");
        setText(aiPlayer3, "ai_player3");
        setText(aiPlayer4, "ai_player4");

        setPromptText(settlementsCount, "settlements_count");
        setPromptText(settlementsPerTurn, "settlements_per_turn");

        updateColorOptions(color1ComboBox);
        updateColorOptions(color2ComboBox);
        updateColorOptions(color3ComboBox);
        updateColorOptions(color4ComboBox);
    }

    private void setButtonText(ButtonBase button, String key) {
        if (button != null) {
            button.setText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " button is null.");
        }
    }

    private void setPromptText(Control control, String key) {
        if (control instanceof TextInputControl) {
            ((TextInputControl) control).setPromptText(bundle.getString(key));
        } else if (control instanceof ComboBox) {
            ((ComboBox<?>) control).setPromptText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " control is not a valid type.");
        }
    }

    private void setText(Labeled labeled, String key) {
        if (labeled != null) {
            labeled.setText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " labeled control is null.");
        }
    }

    private void updateColorOptions(ComboBox<String> comboBox) {
        if (comboBox != null) {
            String selectedColor = comboBox.getValue();
            comboBox.getItems().setAll(bundle.getString("red"), bundle.getString("black"), bundle.getString("blue"), bundle.getString("white"));
            comboBox.setValue(selectedColor); // Re-set the selected color
        } else {
            logger.log(Level.SEVERE, "ComboBox is null.");
        }
    }

    @FXML
    public void startNewGame() {
        List<String> playerNames = getPlayerNames();
        List<Color> playerColors = getPlayerColors();

        if (playerNames.size() < 2) {
            showAlert("Es müssen mindestens zwei Spielernamen eingegeben werden.");
            return;
        }

        if (playerColors == null) {
            showAlert("Jeder Spieler muss eine eindeutige Farbe auswählen.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameBoardView.fxml"), bundle);
            Parent root = loader.load();

            GameBoardController gameBoardController = loader.getController();
            gameBoardController.setPlayers(playerNames.toArray(new String[0]), playerColors.toArray(new Color[0]));
            gameBoardController.setResourceBundle(bundle);

            Stage gameStage = new Stage();
            GameBoardController.setGameStage(gameStage);

            Scene scene = new Scene(root, 1024, 768);
            gameStage.setScene(scene);
            gameStage.setTitle(bundle.getString("title"));

            GameMenuView.setGameStage(gameStage);

            menuStage.hide();
            gameStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start new game", e);
        }
    }

    @FXML
    public void continueGame() {
        GameMenuView.continueGame();

        GameBoardController gameBoardController = GameBoardController.getInstance();
        if (gameBoardController != null) {
            gameBoardController.refreshTexts();
        } else {
            logger.log(Level.SEVERE, "GameBoardController ist null.");
        }
    }

    @FXML
    public void exitGame() {
        System.exit(0);
    }

    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        addPlayerName(names, player1Field);
        addPlayerName(names, player2Field);
        addPlayerName(names, player3Field);
        addPlayerName(names, player4Field);
        return names;
    }

    private void addPlayerName(List<String> names, TextField playerField) {
        if (playerField != null && !playerField.getText().trim().isEmpty()) {
            names.add(playerField.getText().trim());
        }
    }

    private List<Color> getPlayerColors() {
        List<String> colorNames = new ArrayList<>();
        addPlayerColor(colorNames, player1Field, color1ComboBox);
        addPlayerColor(colorNames, player2Field, color2ComboBox);
        addPlayerColor(colorNames, player3Field, color3ComboBox);
        addPlayerColor(colorNames, player4Field, color4ComboBox);

        if (colorNames.size() != new HashSet<>(colorNames).size()) {
            return null; // Duplicate colors found
        }

        List<Color> colors = new ArrayList<>();
        for (String colorName : colorNames) {
            colors.add(ColorMapping.getColorFromString(colorName));
        }

        return colors;
    }

    private void addPlayerColor(List<String> colorNames, TextField playerField, ComboBox<String> colorComboBox) {
        if (playerField != null && !playerField.getText().trim().isEmpty() && colorComboBox != null && colorComboBox.getValue() != null) {
            colorNames.add(colorComboBox.getValue());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
