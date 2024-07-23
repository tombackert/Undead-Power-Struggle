package ups.utils;

import javafx.scene.paint.Color;
import ups.gui.ColorMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private List<String> playerNames = new ArrayList<>();
    private List<Color> playerColors = new ArrayList<>();

    public void start(int port, String playerName, String playerColor) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port: " + port);

        // Add the server player
        playerNames.add(playerName);
        playerColors.add(ColorMapping.getColorFromString(playerColor));

        new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void notifyClientsToStartGame() {
        for (ClientHandler client : clients) {
            client.sendMessage("START_GAME");
            System.out.println("Sent START_GAME message to client");
        }
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public List<Color> getPlayerColors() {
        return playerColors;
    }

    public void broadcastTerrainCardDrawn(String playerName, String terrainType) {
        for (ClientHandler client : clients) {
            client.sendMessage("TERRAIN_CARD_DRAWN:" + playerName + ":" + terrainType);
            System.out.println("Sent TERRAIN_CARD_DRAWN message to client: " + playerName + ", " + terrainType);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Receive client player information
                String clientPlayerName = in.readLine().split(": ")[1];
                String clientPlayerColor = in.readLine().split(": ")[1];

                playerNames.add(clientPlayerName);
                playerColors.add(ColorMapping.getColorFromString(clientPlayerColor));

                System.out.println("Client connected: " + clientPlayerName + " with color " + clientPlayerColor);

                // Send game parameters immediately after connection
                sendGameParameters(playerNames, playerColors, getSettlementsPerTurn(), getSettlementsCount());
                System.out.println("Sent GAME_PARAMS to client");

                // Send START_GAME message immediately after sending game parameters
                sendMessage("START_GAME");

                // Listen for client messages
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received message from client: " + message);
                    for (ClientHandler client : clients) {
                        client.sendMessage(message);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendGameParameters(List<String> playerNames, List<Color> playerColors, int settlementsPerTurn, int settlementsCount) {
            out.println("GAME_PARAMS");
            out.println("PlayerNames: " + String.join(",", playerNames));
            out.println("PlayerColors: " + playerColors.stream().map(Color::toString).collect(Collectors.joining(",")));
            out.println("SettlementsPerTurn: " + settlementsPerTurn);
            out.println("SettlementsCount: " + settlementsCount);
            out.flush();  // Ensure the messages are sent immediately
        }

        public void sendMessage(String message) {
            out.println(message);
            out.flush();  // Ensure the message is sent immediately
            System.out.println("Sent message to client: " + message);
        }
    }

    private int getSettlementsPerTurn() {
        return 3; // Replace with actual logic to get settlements per turn
    }

    private int getSettlementsCount() {
        return 40; // Replace with actual logic to get settlements count
    }
}
