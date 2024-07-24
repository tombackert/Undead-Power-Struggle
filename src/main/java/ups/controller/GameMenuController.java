package ups.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import ups.gui.ColorMapping;
import ups.model.AIPlayer;
import ups.model.GameBoard;
import ups.model.Highscore;
import ups.model.Player;
import ups.utils.*;
import ups.view.GameMenuView;
import ups.view.HighscoreView;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
/**
 * The GameMenuController class is responsible for controlling the game menu.
 */
public class GameMenuController {

    private static final Logger logger = Logger.getLogger(GameMenuController.class.getName());

    private Stage menuStage;
    private Stage primaryStage;
    private ResourceBundle bundle;
    private GameBoardController gameBoardController;
    private static GameServer gameServer;
    private static GameClient gameClient;
    public static boolean serverThreadIsRunning = false;
    public static boolean clientThreadIsRunning = false;
    public static String gameBoardString = null;

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
    private TextField player5Field;
    @FXML
    private ComboBox<String> color5ComboBox;
    @FXML
    private TextField player6Field;
    @FXML
    private ComboBox<String> color6ComboBox;
    @FXML
    private TextField player7Field;
    @FXML
    private ComboBox<String> color7ComboBox;
    @FXML
    private TextField player8Field;
    @FXML
    private ComboBox<String> color8ComboBox;
    @FXML
    private MenuButton languageButton;
    @FXML
    private Button newGameButton;
    @FXML
    private Button continueGameButton;
    @FXML
    private Button exitGameButton;
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
    private CheckBox aiPlayer5;
    @FXML
    private CheckBox aiPlayer6;
    @FXML
    private CheckBox aiPlayer7;
    @FXML
    private CheckBox aiPlayer8;
    @FXML
    private TextField settlementsCount;
    @FXML
    private TextField settlementsPerTurn;
    @FXML
    private GridPane spinnerContainer;
    @FXML
    private Spinner<Integer> cardCountSpinner;
    @FXML
    private CheckBox networkPlayCheckBox;
    @FXML
    private TextField serverIpField;
    @FXML
    private TextField serverPortField;
    @FXML
    private RadioButton serverRadioButton;
    @FXML
    private RadioButton clientRadioButton;
    @FXML
    private ToggleGroup serverClientToggleGroup;
    @FXML
    private TextField networkPlayerNameField;
    @FXML
    private ComboBox<String> networkPlayerColorComboBox;
    @FXML
    private BorderPane mainMenuBorderPane;
    @FXML
    private CheckBox proceduralGameboardCheckbox;
    @FXML
    private Button AnzahlKarten;
    @FXML
    private Label Label;

    @FXML
    private Button randomCardsButton;


    /**
     * Sets the menu stage.
     *
     * @param menuStage the menu stage
     */
    public void setMenuStage(Stage menuStage) {
        this.menuStage = menuStage;
    }

    /**
     * Sets the primary stage.
     *
     * @param primaryStage the primary stage
     */
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
        setTheme();
        switchBackground(MenuController.theme == 0 ? "light" : "dark");

    }

    /**
     * switch the background depending on the theme
     *
     * @param theme the actual theme
     */
    public void switchBackground(String theme) {
        mainMenuBorderPane.getStyleClass().removeIf(styleClass -> styleClass.startsWith("background-pane-"));
        if ("dark".equals(theme)) {
            mainMenuBorderPane.getStyleClass().add("background-pane-dark");
        } else {
            mainMenuBorderPane.getStyleClass().add("background-pane-light");
        }
    }

    /**
     * sets the theme on default
     */
    @FXML
    private void setThemeDefault() {
        MenuController.theme = 0;
        switchBackground("light");
        generateSpinners();
    }
    /**
     * sets the theme on zombie
     */
    @FXML
    private void setThemeZombie() {
        MenuController.theme = 1;
        switchBackground("dark");
        generateSpinners();
    }

    /**
     * The theme is set to the value of the selected menu item.
     */
    @FXML
    private void setTheme() {
        for (MenuItem item : themeButton.getItems()) {
            item.setOnAction(event -> {
                switch (item.getText()) {
                    case "Default":
                        setThemeDefault();
                        System.out.println("Theme: " + item.getText());
                        break;
                    case "Zombie":
                        setThemeZombie();
                        System.out.println("Theme: " + item.getText());
                        break;
                    default:
                        logger.log(Level.WARNING, "Unsupported theme: " + item.getText());
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
        generateSpinners();
    }



    /**
     * Updates the texts in the game menu.
     */
    private void updateTexts() {
        if (bundle == null) {
            logger.log(Level.SEVERE, "bundle is null.");
            return;
        }
        if(MenuController.languageIndex == 0){
            proceduralGameboardCheckbox.setText("Prozedurales Spielfeld");
        }
        if(MenuController.languageIndex == 1){
            proceduralGameboardCheckbox.setText("Procedural Gameboard");
        }
        if (MenuController.languageIndex == 0) {
            AnzahlKarten.setText("Anzahl Karten");
        }
        if (MenuController.languageIndex == 1) {
            AnzahlKarten.setText("Card number");
        }
        if (MenuController.languageIndex == 0) {
            Label.setText("Wähle die Anzahl der Karten aus");
        }
        if (MenuController.languageIndex == 1) {
            Label.setText("Select the number of cards");
        }
        if (MenuController.languageIndex == 0) {
            randomCardsButton.setText("Zufällige Karten");
        }
        if (MenuController.languageIndex == 1) {
            randomCardsButton.setText("Random cards");
        }

        setButtonText(languageButton, "choose_language");
        setButtonText(newGameButton, "new_game");
        setButtonText(continueGameButton, "continue_game");
        setButtonText(exitGameButton, "exit_game");
        setButtonText(highscoreButton, "show_highscore");
        setButtonText(themeButton, "choose_theme");

        setPromptText(player1Field, "prompt_player1_name");
        setPromptText(player2Field, "prompt_player2_name");
        setPromptText(player3Field, "prompt_player3_name");
        setPromptText(player4Field, "prompt_player4_name");
        setPromptText(player5Field, "prompt_player5_name");
        setPromptText(player6Field, "prompt_player6_name");
        setPromptText(player7Field, "prompt_player7_name");
        setPromptText(player8Field, "prompt_player8_name");

        setPromptText(color1ComboBox, "prompt_color_select");
        setPromptText(color2ComboBox, "prompt_color_select");
        setPromptText(color3ComboBox, "prompt_color_select");
        setPromptText(color4ComboBox, "prompt_color_select");
        setPromptText(color5ComboBox, "prompt_color_select");
        setPromptText(color6ComboBox, "prompt_color_select");
        setPromptText(color7ComboBox, "prompt_color_select");
        setPromptText(color8ComboBox, "prompt_color_select");

        setText(aiPlayer1, "ai_player1");
        setText(aiPlayer2, "ai_player2");
        setText(aiPlayer3, "ai_player3");
        setText(aiPlayer4, "ai_player4");
        setText(aiPlayer5, "ai_player5");
        setText(aiPlayer6, "ai_player6");
        setText(aiPlayer7, "ai_player7");
        setText(aiPlayer8, "ai_player8");

        setPromptText(settlementsCount, "settlements_count");
        setPromptText(settlementsPerTurn, "settlements_per_turn");

        updateColorOptions(color1ComboBox);
        updateColorOptions(color2ComboBox);
        updateColorOptions(color3ComboBox);
        updateColorOptions(color4ComboBox);
        updateColorOptions(color5ComboBox);
        updateColorOptions(color6ComboBox);
        updateColorOptions(color7ComboBox);
        updateColorOptions(color8ComboBox);
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
            comboBox.getItems().setAll(bundle.getString("red"), bundle.getString("black"), bundle.getString("blue"), bundle.getString("orange"), bundle.getString("purple"), bundle.getString("white"), bundle.getString("magenta"), bundle.getString("babyblue"));
            comboBox.setValue(selectedColor); // Re-set the selected color
        } else {
            logger.log(Level.SEVERE, "ComboBox is null.");
        }
    }

    /**
     * Starts a new game.
     * @throws IOException if an I/O error occurs
     */
    @FXML
    public void startNewGame() throws IOException{
        boolean isNetworkPlay = networkPlayCheckBox.isSelected();
        if (isNetworkPlay) {
            startNetworkGame();
        } else {
            startLocalGame();
        }
    }

    private static String[][] parsePlayerInfo(String input) {
        // Split the input string by "::"
        String[] playerEntries = input.split("::");
        int numOfPlayers = playerEntries.length;

        // Initialize the 2D array
        String[][] playerInfoArray = new String[numOfPlayers][2];

        // Fill the 2D array with player names and colors
        for (int i = 0; i < numOfPlayers; i++) {
            // Split each player entry by ":"
            String[] playerData = playerEntries[i].split(":");
            if (playerData.length == 2) {
                playerInfoArray[i][0] = playerData[0]; // Player name
                playerInfoArray[i][1] = playerData[1]; // Player color
            }
        }

        return playerInfoArray;
    }

    private int[] parseSettlementInfo(String info) {
        int[] settlementInfo = new int[2];
        settlementInfo[0] = Integer.parseInt(info.split(":")[0]);
        settlementInfo[1] = Integer.parseInt(info.split(":")[1]);
        return settlementInfo;
    }

//192.168.178.129
    private void startNetworkGame() throws IOException{
        ClientGameConnection.setMessageToClient(null);
        ClientGameConnection.setMessageToGame(null);
        clientThreadIsRunning = false;
        serverThreadIsRunning = false;
        try {
            Thread.sleep(300);
        } catch (Exception e) {}
        if (serverRadioButton.isSelected()) {
            List<String> cards = getSelectedCards();
            if (hasDuplicateCards(cards)) {
                AlertManager.showAlert("alert.duplicate_cards");
                clientThreadIsRunning = false;
                serverThreadIsRunning = false;
                return;
            }
            int settlementsPerTurnValue = getSettlementsPerTurn();
            int settlementsCountValue = getSettlementsCount();
            // Start the server in its own thread
            Thread serverThread = new Thread(() -> {
                try {
                    serverThreadIsRunning = true;
                    new Server(Integer.parseInt(serverPortField.getText()), settlementsPerTurnValue, settlementsCountValue, cards).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverThread.start();
        }
        String playerName = player1Field.getText();
        String playerColor = color1ComboBox.getValue();
        Thread clientThread = new Thread(() -> {
            try {
                clientThreadIsRunning = true;
                new Client(serverIpField.getText(), Integer.parseInt(serverPortField.getText()), playerName, playerColor).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
        String initString = null;
        //wait for sever to send game info
        long startTime = System.currentTimeMillis();
        while (((initString = ClientGameConnection.getMessageToGame()) == null) && System.currentTimeMillis() - startTime < 15000);
        //Parse Player info
        System.out.println("InitString: " + initString);
        if (initString == null) {
            System.out.println("Verbindung zum Server fehlgeschlagen");
            clientThreadIsRunning = false;
            serverThreadIsRunning = false;
            return;

        }
        String[][] infoStr = parsePlayerInfo(initString.split(":::")[1]);
        List<String> playerNames = new ArrayList<String>();
        List<Color> playerColors = new ArrayList<Color>();
        boolean[] which = new boolean[8];
        int whichPlayer = Integer.parseInt(initString.split(":::")[initString.split(":::").length - 1]);
        for (int i = 0; i < infoStr.length; i++) {
            playerNames.add(infoStr[i][0]);
            playerColors.add(ColorMapping.getColorFromString(infoStr[i][1]));
            which[i] = whichPlayer == i;
        }
        //Parse settlement info
        System.out.println(initString.split(":::")[2]);
        int[] settlementInfo = parseSettlementInfo(initString.split(":::")[2]);
        int settlementsPerTurn = settlementInfo[0];
        int settlementsCount = settlementInfo[1];
        System.out.println("Per turn: " + Integer.toString(settlementsPerTurn) + "Gesamt: " + Integer.toString(settlementsCount));
        //Parse kingdom builder card info
        List<String> selectedCards = new ArrayList<String>();
        System.out.println("Cards: " + initString.split(":::")[3]);
        for (String c : initString.split(":::")[3].split(":")) {
            selectedCards.add(c);
        }
        //Parse procedural gameboard
        if (initString.split(":::").length > 5) {
            gameBoardString = initString.split(":::")[4];
        }
        //start the game
        if (playerNames.size() < 2) {
            AlertManager.showAlert("alert.minimum_players");
            clientThreadIsRunning = false;
            serverThreadIsRunning = false;
            return;
        }

        if (hasDuplicateCards(selectedCards)) {
            AlertManager.showAlert("alert.duplicate_cards");
            clientThreadIsRunning = false;
            serverThreadIsRunning = false;
            return;
        }

        if (playerColors == null || playerColors.size() != playerNames.size()) {
            AlertManager.showAlert("alert.duplicate_colors");
            clientThreadIsRunning = false;
            serverThreadIsRunning = false;
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameBoardView.fxml"), bundle);
        Parent root = loader.load();

        GameBoardController gameBoardController = loader.getController();
        gameBoardController.setResourceBundle(bundle); // Ensure this is called before anything else

        int totalSettlementsNeeded = settlementsCount * playerNames.size();
        if (!gameBoardController.checkAvailableBuildableFields(totalSettlementsNeeded)) {
            AlertManager.showAlert("alert.not_enough_buildable_fields");
            clientThreadIsRunning = false;
            serverThreadIsRunning = false;
            return;
        }

        gameBoardController.setSettlementsPerTurn(settlementsPerTurn);
        gameBoardController.setSettlementsCount(settlementsCount);
        gameBoardController.setPlayersForNetwork(playerNames.toArray(new String[0]), playerColors.toArray(new Color[0]), which, settlementsPerTurn, settlementsCount);
        gameBoardController.setSelectedCards(selectedCards);

        Stage gameStage = new Stage();
        GameBoardController.setGameStage(gameStage);

        Scene scene = new Scene(root, 1488, 850);
        gameStage.setScene(scene);
        gameStage.setTitle(bundle.getString("title"));

        GameMenuView.setGameStage(gameStage);

        menuStage.hide();
        gameStage.show();
        if (!which[0]) gameBoardController.startOnlineGame();

    }

    private void startLocalGame() {
        List<String> playerNames = getPlayerNames();
        List<Color> playerColors = getPlayerColors();
        boolean[] aiPlayers = getAIPlayers();
        int settlementsPerTurnValue = getSettlementsPerTurn();
        int settlementsCountValue = getSettlementsCount();

        if (playerNames.size() < 2) {
            AlertManager.showAlert("alert.minimum_players");
            return;
        }

        if (playerColors == null || playerColors.size() != playerNames.size()) {
            AlertManager.showAlert("alert.duplicate_colors");
            return;
        }
        List<String> selectedCards = getSelectedCards();
        if (hasDuplicateCards(selectedCards)) {
            AlertManager.showAlert("alert.duplicate_cards");
            return;
        }

        try {
            startGame(playerNames, playerColors, aiPlayers, settlementsPerTurnValue, settlementsCountValue, selectedCards);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start new game", e);
        }
    }

    private void startGame(List<String> playerNames, List<Color> playerColors, boolean[] aiPlayers, int settlementsPerTurn, int settlementsCount, List<String> selectedCards) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameBoardView.fxml"), bundle);
        Parent root = loader.load();

        GameBoardController gameBoardController = loader.getController();
        gameBoardController.setResourceBundle(bundle); // Ensure this is called before anything else

        int totalSettlementsNeeded = settlementsCount * playerNames.size();
        if (!gameBoardController.checkAvailableBuildableFields(totalSettlementsNeeded)) {
            AlertManager.showAlert("alert.not_enough_buildable_fields");
            return;
        }

        gameBoardController.setSettlementsPerTurn(settlementsPerTurn);
        gameBoardController.setSettlementsCount(settlementsCount);
        gameBoardController.setPlayers(playerNames.toArray(new String[0]), playerColors.toArray(new Color[0]), aiPlayers);
        gameBoardController.setSelectedCards(selectedCards);
        List<Player> players = new ArrayList<>();

        Stage gameStage = new Stage();
        GameBoardController.setGameStage(gameStage);

        Scene scene = new Scene(root, 1488, 850);
        gameStage.setScene(scene);
        gameStage.setTitle(bundle.getString("title"));

        GameMenuView.setGameStage(gameStage);

        menuStage.hide();
        gameStage.show();

        if (areAllEnteredPlayersAI(playerNames, aiPlayers) || isAIPlayerBeforeHuman(aiPlayers)) {
            System.out.println("Ja, sind alles KI-Spieler");
            gameBoardController.startAiGame();
        }
    }

    /**
     * Starts the server.
     *
     * @param port         the port
     * @param playerName   the player name
     * @param playerColor  the player color
     */
    public void startServer(int port, String playerName, String playerColor) {
        int settlementsPerTurnValue = getSettlementsPerTurn();
        int settlementsCountValue = getSettlementsCount();

        try {
            gameServer = new GameServer();
            gameServer.start(port, playerName, playerColor);

            new Thread(() -> {
                try {
                    Thread.sleep(10000);

                    Platform.runLater(() -> {
                        List<String> playerNames = new ArrayList<>(gameServer.getPlayerNames());
                        List<Color> playerColors = gameServer.getPlayerColors();
                        List<String> selectedCards = getSelectedCards();

                        // Notify clients to start the game
                        System.out.println("Notifying clients to start game"); // Debug output
                        gameServer.notifyClientsToStartGame();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the client.
     *
     * @param ip          the IP address
     * @param port        the port
     * @param playerName   the player name
     * @param playerColor  the player color
     */
    public void startClient(String ip, int port, String playerName, String playerColor) {
        try {
            GameClient gameClient = new GameClient();
            gameClient.setResourceBundle(bundle); // Set the resource bundle here
            gameClient.start(ip, port, playerName, playerColor);
        } catch (IOException e) {
            e.printStackTrace();
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
            gameBoardController.resumeGame(); // Resume the game
            gameBoardController.refreshTexts();
            gameBoardController.refreshBackground();
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
        addPlayerName(names, player5Field);
        addPlayerName(names, player6Field);
        addPlayerName(names, player7Field);
        addPlayerName(names, player8Field);
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
        playerColorMap.put(player5Field, color5ComboBox);
        playerColorMap.put(player6Field, color6ComboBox);
        playerColorMap.put(player7Field, color7ComboBox);
        playerColorMap.put(player8Field, color8ComboBox);

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
        boolean[] aiPlayers = new boolean[]{
            aiPlayer1.isSelected(),
            aiPlayer2.isSelected(),
            aiPlayer3.isSelected(),
            aiPlayer4.isSelected(),
            aiPlayer5.isSelected(),
            aiPlayer6.isSelected(),
            aiPlayer7.isSelected(),
            aiPlayer8.isSelected()
        };
     
        return aiPlayers;
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

    /**
     * Generates ComboBox spinners based on the number selected in cardCountSpinner.
     * Clears existing spinners before generating new ones.
     * Uses card names based on the theme and language settings.
     */
    @FXML
    private void generateSpinners() {
        spinnerContainer.getChildren().clear(); // Löscht vorherige Spinner, falls vorhanden
        int numberOfSpinners = cardCountSpinner.getValue();

        List<String> cardOptions;
        if (MenuController.theme == 1) {
            cardOptions = loadZombieCardNames();
        } else {
            cardOptions = loadCardNames();
        }
        for (int i = 0; i < numberOfSpinners; i++) {
            ComboBox<String> cardComboBox = new ComboBox<>();
            cardComboBox.getItems().addAll(cardOptions);
            if (MenuController.languageIndex ==0){
                cardComboBox.setPromptText("Karte auswählen");

            } else {
                cardComboBox.setPromptText("Select Card");
            }


            int row = i / 4;
            int col = i % 4;
            GridPane.setConstraints(cardComboBox, col, row);
            spinnerContainer.getChildren().add(cardComboBox);
        }
    }

    /**
     * Retrieves the list of selected cards from the ComboBoxes in the spinnerContainer.
     *
     * @return A list of strings representing the selected cards. The list may contain null elements if no card is selected.
     */
    private List<String> getSelectedCards() {
        List<String> selectedCards = new ArrayList<>();
        for (Node node : spinnerContainer.getChildren()) {
            if (node instanceof ComboBox) {
                ComboBox<String> comboBox = (ComboBox<String>) node;
                String selectedCard = comboBox.getValue();
                if (selectedCard != null) {
                    selectedCards.add(selectedCard);
                }
            }
        }
        return selectedCards;
    }

    /**
     * Checks if the given list of selected cards contains duplicates.
     *
     * @param selectedCards The list of selected cards to check.
     * @return true = if duplicates are found, false otherwise.
     */
    private boolean hasDuplicateCards(List<String> selectedCards) {
        Set<String> cardSet = new HashSet<>(selectedCards);
        return cardSet.size() < selectedCards.size();
    }

    /**
     * Loads and returns the names of standard game cards from the resource bundle.
     * The card names are fetched using keys common across all game themes.
     *
     * @return A list of standard game card names.
     */
    private List<String> loadCardNames() {
        return Arrays.asList(
                bundle.getString("card.fischer"),
                bundle.getString("card.bergleute"),
                bundle.getString("card.arbeiter"),
                bundle.getString("card.einsiedler"),
                bundle.getString("card.haendler"),
                bundle.getString("card.entdecker"),
                bundle.getString("card.ritter"),
                bundle.getString("card.lords"),
                bundle.getString("card.buerger"),
                bundle.getString("card.bauern")
        );
    }

    /**
     * Loads and returns the names of Zombie-themed cards from the resource bundle.
     * The card names are fetched using keys specific to the Zombie theme.
     *
     * @return A list of Zombie-themed card names.
     */
    private List<String> loadZombieCardNames() {
        return Arrays.asList(
                bundle.getString("card.Z.fischer"),
                bundle.getString("card.Z.bergleute"),
                bundle.getString("card.Z.arbeiter"),
                bundle.getString("card.Z.einsiedler"),
                bundle.getString("card.Z.haendler"),
                bundle.getString("card.Z.entdecker"),
                bundle.getString("card.Z.ritter"),
                bundle.getString("card.Z.lords"),
                bundle.getString("card.Z.buerger"),
                bundle.getString("card.Z.bauern")
        );
    }


    @FXML
    private void handleProceduralGameboardCheckbox() {
        GameBoard.makeProcedural = proceduralGameboardCheckbox.isSelected();
    }

    /**
     * Gets the game server.
     * @return the game server
     */
    public static GameServer getGameServer() {
        return gameServer;
    }

    /**
     * Gets the game client.
     * @return the game client
     */
    public static GameClient getGameClient() {
        return gameClient;
    }
    /**
     * handles the random generated cards
     */
    @FXML
    private void handleRandomCardsButton() {
        generateRandomCards();
    }
    /**
     * generate Random cards
     */
    private void generateRandomCards() {
        int numberOfCards = cardCountSpinner.getValue();
        List<String> cardOptions;
    
        if (MenuController.theme == 1) {
            cardOptions = loadZombieCardNames();
        } else {
            cardOptions = loadCardNames();
        }
    
        Collections.shuffle(cardOptions);  // Mischen Sie die Liste
    
        spinnerContainer.getChildren().clear();  // Löschen Sie bestehende Spinner
    
        for (int i = 0; i < numberOfCards; i++) {
            ComboBox<String> cardComboBox = new ComboBox<>();
            cardComboBox.getItems().addAll(cardOptions);
            cardComboBox.setValue(cardOptions.get(i));  // Setzen Sie eine zufällige Karte
    
            if (MenuController.languageIndex == 0){
                cardComboBox.setPromptText("Karte auswählen");
            } else {
                cardComboBox.setPromptText("Select Card");
            }
    
            int row = i / 4;
            int col = i % 4;
            GridPane.setConstraints(cardComboBox, col, row);
            spinnerContainer.getChildren().add(cardComboBox);
        }
    }

}
