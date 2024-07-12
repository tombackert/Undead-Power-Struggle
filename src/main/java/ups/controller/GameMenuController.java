package ups.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.model.AIPlayer;
import ups.model.Highscore;
import ups.model.Player;
import ups.utils.AlertManager;
import ups.utils.HighscoreManager;
import ups.utils.LanguageSettings;
import ups.view.GameMenuView;
import ups.view.HighscoreView;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The GameMenuController class is responsible for controlling the game menu.
 */
public class GameMenuController {

    private static final Logger logger = Logger.getLogger(GameMenuController.class.getName());

    private Stage menuStage;
    private Stage primaryStage;
    private ResourceBundle bundle;

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
    @FXML
    private VBox spinnerContainer;
    @FXML
    private Spinner<Integer> cardCountSpinner;

    private GameBoardController gameBoardController;

    /**
     * Sets the menu stage.
     *
     * @param menuStage the menu stage
     */
    public void setMenuStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Initializes the game menu.
     */
    @FXML
    public void initialize() {

        if (MenuController.language == "de") {
            setLanguage(Locale.GERMAN);
        } else {
            setLanguage(Locale.ENGLISH);
        }

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

    /**
     * Sets the language to the given locale.
     *
     * @param locale the locale
     */
    private void setLanguage(Locale locale) {
        
        MenuController.language = locale.getLanguage();
        if (MenuController.language == "de") {
            MenuController.languageIndex = 0;
        } else {
            MenuController.languageIndex = 1;
        }

        //System.out.println("Setting language to: " + MenuController.language);

        LanguageSettings.setCurrentLocale(locale);
        bundle = ResourceBundle.getBundle("messages", locale);
        if (bundle == null) {
            logger.log(Level.SEVERE, "ResourceBundle could not be loaded for locale: " + locale);
            return;
        }
        updateTexts();
    }

    /**
     * Updates the texts in the game menu.
     */
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

    /**
     * Sets the text of the given button to the value of the given key in the resource bundle.
     *
     * @param button the button
     * @param key    the key
     */
    private void setButtonText(ButtonBase button, String key) {
        if (button != null) {
            button.setText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " button is null.");
        }
    }

    /**
     * Sets the prompt text of the given control to the value of the given key in the resource bundle.
     *
     * @param control the control
     * @param key     the key
     */
    private void setPromptText(Control control, String key) {
        if (control instanceof TextInputControl) {
            ((TextInputControl) control).setPromptText(bundle.getString(key));
        } else if (control instanceof ComboBox) {
            ((ComboBox<?>) control).setPromptText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " control is not a valid type.");
        }
    }

    /**
     * Sets the text of the given labeled control to the value of the given key in the resource bundle.
     *
     * @param labeled the labeled control
     * @param key     the key
     */
    private void setText(Labeled labeled, String key) {
        if (labeled != null) {
            labeled.setText(bundle.getString(key));
        } else {
            logger.log(Level.SEVERE, key + " labeled control is null.");
        }
    }

    /**
     * Updates the color options for the given combo box.
     *
     * @param comboBox the combo box
     */
    private void updateColorOptions(ComboBox<String> comboBox) {
        if (comboBox != null) {
            String selectedColor = comboBox.getValue();
            comboBox.getItems().setAll(bundle.getString("red"), bundle.getString("black"), bundle.getString("blue"), bundle.getString("orange"));
            comboBox.setValue(selectedColor); // Re-set the selected color
        } else {
            logger.log(Level.SEVERE, "ComboBox is null.");
        }
    }

    /**
     * Starts a new game.
     */
    @FXML
    public void startNewGame() {
        List<String> playerNames = getPlayerNames();
        List<Color> playerColors = getPlayerColors();
        boolean[] aiPlayers = getAIPlayers();
        int settlementsPerTurnValue = getSettlementsPerTurn();
        int settlementsCountValue = getSettlementsCount();

        // Überprüfen, ob die Mindestanzahl von Spielern erreicht ist
        if (playerNames.size() < 2) {
            AlertManager.showAlert("alert.minimum_players");
            return;
        }

        if (playerColors == null || playerColors.size() != getPlayerNames().size()) {
            AlertManager.showAlert("alert.duplicate_colors");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameBoardView.fxml"), bundle);
            Parent root = loader.load();

            GameBoardController gameBoardController = loader.getController();

            int totalSettlementsNeeded = settlementsCountValue * playerNames.size();
            if (!gameBoardController.checkAvailableBuildableFields(totalSettlementsNeeded)) {
                AlertManager.showAlert("alert.not_enough_buildable_fields");
                return;
            }

            gameBoardController.setSettlementsPerTurn(settlementsPerTurnValue); // Set the value here
            gameBoardController.setSettlementsCount(settlementsCountValue); // Set the total settlements value here
            gameBoardController.setPlayers(playerNames.toArray(new String[0]), playerColors.toArray(new Color[0]), aiPlayers);
            gameBoardController.setResourceBundle(bundle);

            List<Player> players = new ArrayList<>();

            Stage gameStage = new Stage();
            GameBoardController.setGameStage(gameStage);

            Scene scene = new Scene(root, 1488, 850);
            gameStage.setScene(scene);
            gameStage.setTitle(bundle.getString("title"));

            GameMenuView.setGameStage(gameStage);

            menuStage.hide();
            gameStage.show();

            // Prüfen, ob alle eingegebenen Spieler KI-Spieler sind
            if (areAllEnteredPlayersAI(playerNames, aiPlayers) || isAIPlayerBeforeHuman(aiPlayers)) {
                System.out.println("Ja, sind alles KI-Spieler");
                gameBoardController.startAiGame();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start new game", e);
        }
    }

    private boolean areAllEnteredPlayersAI(List<String> playerNames, boolean[] aiPlayers) {
        for (int i = 0; i < playerNames.size(); i++) {
            if (!aiPlayers[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isAIPlayerBeforeHuman(boolean[] isAIPlayer) {
        for (int i = 0; i < isAIPlayer.length - 1; i++) {  // Durchlaufe das Array bis zum vorletzten Element
            if (isAIPlayer[i] && !isAIPlayer[i + 1]) {
                return true;  // Ein KI-Spieler ist direkt vor einem menschlichen Spieler
            }
        }
        return false;  // Kein KI-Spieler ist direkt vor einem menschlichen Spieler
    }

    private int getSettlementsPerTurn() {
        try {
            System.out.println("SpT: " + settlementsPerTurn.getText());
            return Integer.parseInt(settlementsPerTurn.getText());
        } catch (NumberFormatException e) {
            return 3; // Default value if parsing fails
        }
    }

    private int getSettlementsCount() {
        try {
            int value = Integer.parseInt(settlementsCount.getText());
            //System.out.println("Total settlements from TextField: " + value); // Debug statement
            return value;
        } catch (NumberFormatException e) {
            //System.out.println("Invalid number format for total settlements, using default value 40."); // Debug statement
            return 40; // Default value if parsing fails
        }
    }

    /**
     * Continues the game.
     */
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

    /**
     * Exits the game.
     */
    @FXML
    public void exitGame() {
        GameMenuView.switchToMenu(primaryStage);
    }

    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        addPlayerName(names, player1Field);
        addPlayerName(names, player2Field);
        addPlayerName(names, player3Field);
        addPlayerName(names, player4Field);
        return names;
    }

    /**
     * Adds the name of the player to the list of names.
     *
     * @param names       the list of names
     * @param playerField the player field
     */
    private void addPlayerName(List<String> names, TextField playerField) {
        if (playerField != null && !playerField.getText().trim().isEmpty()) {
            names.add(playerField.getText().trim());
        }
    }

    private List<Color> getPlayerColors() {
        // Verwenden einer Map, um die Zuordnung von Spielerfeld zu Farbe sicherzustellen
        Map<TextField, ComboBox<String>> playerColorMap = new LinkedHashMap<>();
        playerColorMap.put(player1Field, color1ComboBox);
        playerColorMap.put(player2Field, color2ComboBox);
        playerColorMap.put(player3Field, color3ComboBox);
        playerColorMap.put(player4Field, color4ComboBox);

        List<String> colorNames = new ArrayList<>();
        for (Map.Entry<TextField, ComboBox<String>> entry : playerColorMap.entrySet()) {
            if (!entry.getKey().getText().trim().isEmpty() && entry.getValue().getValue() != null) {
                colorNames.add(entry.getValue().getValue());
            }
        }

        // Überprüfen, ob es Duplikate in den Farbauswahlen gibt
        if (colorNames.size() != new HashSet<>(colorNames).size()) {
            return null; // Frühzeitiger Rückkehr, wenn Duplikate gefunden wurden
        }

        // Konvertieren der Farbnamen in Color-Objekte
        List<Color> colors = colorNames.stream()
                .map(ColorMapping::getColorFromString)
                .collect(Collectors.toList());

        return colors;
    }


    /**
     * Adds the color of the player to the list of color names.
     *
     * @param colorNames    the list of color names
     * @param playerField   the player field
     * @param colorComboBox the color combo box
     */
    private void addPlayerColor(List<String> colorNames, TextField playerField, ComboBox<String> colorComboBox) {
        if (playerField != null && !playerField.getText().trim().isEmpty() && colorComboBox != null && colorComboBox.getValue() != null) {
            colorNames.add(colorComboBox.getValue());
        }
    }

    /**
     * Returns an array of booleans indicating whether each player is an AI player.
     *
     * @return the array of booleans
     */
    private boolean[] getAIPlayers() {
        return new boolean[]{
                aiPlayer1.isSelected(),
                aiPlayer2.isSelected(),
                aiPlayer3.isSelected(),
                aiPlayer4.isSelected()
        };
    }

    @FXML
    private void showHighscore() {
        HighscoreManager highscoreManager = new HighscoreManager();
        List<Highscore> highscores = highscoreManager.loadHighscores();

        if (highscores.isEmpty()) {
            // Eine Methode, um eine leere Highscore-Anzeige zu behandeln
            showEmptyHighscoreMessage();
            return;
        }

        HighscoreView highscoreView = new HighscoreView();
        highscoreView.showHighscoreView(highscores); // Angepasst, um sortierte Highscores zu akzeptieren
    }

    @FXML
    private void showEmptyHighscoreMessage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/EmptyHighscoreView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Highscores");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void generateSpinners() {
        spinnerContainer.getChildren().clear(); // Löscht vorherige Spinner, falls vorhanden
        int numberOfSpinners = cardCountSpinner.getValue();

        for (int i = 0; i < numberOfSpinners; i++) {
            Spinner<Integer> spinner = new Spinner<>(1, 10, 1); // Hier können die Parameter angepasst werden
            spinnerContainer.getChildren().add(spinner);

        }
    }
}
