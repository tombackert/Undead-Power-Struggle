package ups.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ups.controller.GameBoardController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GameClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<String> playerNames = new ArrayList<>();
    private List<Color> playerColors = new ArrayList<>();
    private int settlementsPerTurn;
    private int settlementsCount;
    private ResourceBundle bundle;

    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void start(String ip, int port, String playerName, String playerColor) throws IOException {
        System.out.println("Trying to connect to server at IP: " + ip + ", Port: " + port);
        System.out.println("Player Name: " + playerName + ", Player Color: " + playerColor);

        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Send client player information to the server
            out.println("ClientPlayerName: " + playerName);
            out.println("ClientPlayerColor: " + playerColor);

            // Start a thread to listen for server messages
            new Thread(this::listenForServerMessages).start();

            System.out.println("Connected to server as: " + playerName + " with color " + playerColor);
        } catch (IOException e) {
            System.out.println("Error while trying to connect to the server.");
            e.printStackTrace();
            throw e;
        }
    }

    private void listenForServerMessages() {
        try {
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Received message from server: " + serverMessage); // Debug output
                if (serverMessage.equals("START_GAME")) {
                    Platform.runLater(this::startGame);
                } else if (serverMessage.startsWith("TERRAIN_CARD_DRAWN")) {
                    String[] parts = serverMessage.split(":");
                    if (parts.length == 3) {
                        String playerName = parts[1];
                        String terrainType = parts[2];
                        System.out.println("Processing TERRAIN_CARD_DRAWN message: " + playerName + ", " + terrainType); // Debug output
                        Platform.runLater(() -> handleTerrainCardDrawnMessage(playerName, terrainType));
                    }
                } else if (serverMessage.equals("GAME_PARAMS")) {
                    processGameParameters();
                }
            }
            System.out.println("Connection closed by server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processGameParameters() throws IOException {
        System.out.println("Processing game parameters");
        String playerNamesLine = in.readLine();
        String playerColorsLine = in.readLine();
        String settlementsPerTurnLine = in.readLine();
        String settlementsCountLine = in.readLine();

        System.out.println("PlayerNames line: " + playerNamesLine);
        System.out.println("PlayerColors line: " + playerColorsLine);
        System.out.println("SettlementsPerTurn line: " + settlementsPerTurnLine);
        System.out.println("SettlementsCount line: " + settlementsCountLine);

        playerNames = Arrays.asList(playerNamesLine.split(": ")[1].split(","));
        playerColors = Arrays.stream(playerColorsLine.split(": ")[1].split(",")).map(Color::valueOf).collect(Collectors.toList());
        settlementsPerTurn = Integer.parseInt(settlementsPerTurnLine.split(": ")[1]);
        settlementsCount = Integer.parseInt(settlementsCountLine.split(": ")[1]);

        System.out.println("Processed game parameters:");
        System.out.println("Player Names: " + playerNames);
        System.out.println("Player Colors: " + playerColors);
        System.out.println("Settlements Per Turn: " + settlementsPerTurn);
        System.out.println("Settlements Count: " + settlementsCount);
    }

    public void handleTerrainCardDrawnMessage(String playerName, String terrainType) {
        System.out.println("Handling terrain card drawn message for player: " + playerName + ", terrain: " + terrainType); // Debug output
        GameBoardController gameBoardController = GameBoardController.getInstance();
        if (gameBoardController != null) {
            gameBoardController.handleTerrainCardDrawnMessage(new TerrainCardDrawnMessage(playerName, terrainType));
            gameBoardController.updateTerrainLabel();
        } else {
            System.out.println("GameBoardController instance is null");
        }
    }

    private void startGame() {
        // Use the stored game parameters to load the game stage
        System.out.println("Inside Platform.runLater for startGame...");
        System.out.println("Game is starting with parameters:");
        System.out.println("Player Names: " + playerNames);
        System.out.println("Player Colors: " + playerColors);
        System.out.println("Settlements Per Turn: " + settlementsPerTurn);
        System.out.println("Settlements Count: " + settlementsCount);

        // Load and display the game stage
        try {
            System.out.println("Loading GameBoardView.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ups/view/GameBoardView.fxml"), bundle);
            Parent root = loader.load();

            System.out.println("GameBoardView.fxml loaded successfully.");
            GameBoardController gameBoardController = loader.getController();
            gameBoardController.setResourceBundle(bundle); // Ensure this is called before anything else
            gameBoardController.setPlayers(playerNames.toArray(new String[0]), playerColors.toArray(new Color[0]), new boolean[playerNames.size()]);
            gameBoardController.setSettlementsPerTurn(settlementsPerTurn);
            gameBoardController.setSettlementsCount(settlementsCount);

            Stage gameStage = new Stage();
            Scene scene = new Scene(root, 1488, 850);
            gameStage.setScene(scene);
            gameStage.setTitle(bundle.getString("title"));

            gameStage.show();
            System.out.println("Game stage shown");

            // Set the instance of GameBoardController
            GameBoardController.setInstance(gameBoardController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    public void sendTerrainCardDrawn(String terrainType) {
        out.println("TERRAIN_CARD_DRAWN:" + terrainType);
        out.flush(); // Ensure the message is sent immediately
        System.out.println("Sent TERRAIN_CARD_DRAWN message to server: " + terrainType);
    }
}
