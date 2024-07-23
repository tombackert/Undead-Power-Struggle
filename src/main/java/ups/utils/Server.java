package ups.utils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import ups.controller.GameMenuController;
import ups.gui.ColorMapping;
import ups.model.GameBoard;
import ups.utils.ProceduralGameboard;
import ups.utils.ProceduralZone;

public class Server {
    private static final int WAIT_TIME = 10000; // 10 seconds

    private List<String> clientInfoList = Collections.synchronizedList(new ArrayList<>());

    private ServerSocket serverSocket;
    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private List<String> playerNames;
    private List<String> playerColors;

    //Game variables
    private int settlementsPerTurn, settlementCount;
    private List<String> selectedCards;

    public Server(int port, int settlementsPerTurnValue, int settlementsCountValue, List<String> selectedCards) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000); // Set a timeout on the ServerSocket
        this.settlementsPerTurn = settlementsPerTurnValue;
        this.settlementCount = settlementsCountValue;
        this.selectedCards = selectedCards;
    }

    public void start() {
        System.out.println("Server started. Waiting for clients...");
        long startTime = System.currentTimeMillis();

        // Accept clients for a limited time
        while (System.currentTimeMillis() - startTime < WAIT_TIME) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
            } catch (SocketTimeoutException e) {
                // Timeout exception is expected due to setSoTimeout
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Send all clients info about other clients
        sendClientInfoToAll();
        System.out.println("Going into main loop");

        // Main server loop to handle messages
        while (GameMenuController.serverThreadIsRunning) {
            for (ClientHandler client : clients) {
                try {
                    if (client.hasMessage()) {
                        String message = client.getMessage();
                        System.out.println("Server received: " + message);
                        broadcastMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(100); // Add sleep to prevent tight loop and reduce CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        shutdown();
    }

    public void shutdown() {
        try {
            // Close all client sockets
            for (ClientHandler client : clients) {
                client.close();
            }
            // Close the server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server shut down successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void filterPlayerInfo() {
        playerNames = new ArrayList<String>();
        playerColors = new ArrayList<String>();
        Random r = new Random();
        for (String info : clientInfoList) {
            String buf = info.split(":")[0];
            int count = 2;
            while (playerNames.contains(buf)) {
                buf = info.split(":")[0];
                buf = buf + "-" + Integer.toString(count);
                count++;
            }
            playerNames.add(buf);
            buf = ColorMapping.getStringFromColor(ColorMapping.getColorFromString(info.split(":")[1]));
            while (playerColors.contains(buf)) {
                buf = ColorMapping.getStringFromColor(ColorMapping.getColorFromInt(1 + r.nextInt(8)));
            }
            playerColors.add(buf);
        }
    }

    private void sendClientInfoToAll() {
        filterPlayerInfo();
        StringBuilder clientInfo = new StringBuilder("INIT:::");
        boolean first = true;
        synchronized (clientInfoList) {
            for (int i = 0; i < playerNames.size(); i++) {
                if (first) {
                    clientInfo.append(playerNames.get(i) + ":" + playerColors.get(i));
                    first = false;
                }
                else clientInfo.append("::").append(playerNames.get(i) + ":" + playerColors.get(i));
            }
        }
        clientInfo.append(":::" + Integer.toString(this.settlementsPerTurn) + ":" + Integer.toString(this.settlementCount));
        first = true;
        for (String card : selectedCards) {
            if (first) {
                clientInfo.append(":::").append(card);
                first = false;
            } else clientInfo.append(":").append(card);
        }
        if (selectedCards.size() == 0) clientInfo.append(":::Fischer:Arbeiter:Bergleute");
        if (GameBoard.makeProcedural) {
            ProceduralGameboard g = new ProceduralGameboard(20, 20);
            int[][] board = g.generateProceduralGameboardInt();
            first = true;
            for (int x = 0; x < 20; x++) {
                for (int y = 0; y < 20; y++) {
                    if (first) {
                        clientInfo.append(":::").append(Integer.toString(board[x][y]));
                        first = false;
                    } else clientInfo.append(":").append(Integer.toString(board[x][y]));
                }
            }
            
        }
        broadcastFirstMessage(clientInfo.toString());
    }

    private void broadcastMessage(String message) {
        synchronized (clients) {
            System.out.println("Server Broadcasting message: " + message);
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    private void broadcastFirstMessage(String message) {
        synchronized (clients) {
            System.out.println("Server Broadcasting message: " + message);
            int c = 0;
            for (ClientHandler client : clients) {
                client.sendMessage(message + ":::" + Integer.toString(c));
                c++;
            }
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            // Read client info and store it
            String clientInfo = in.readLine();
            System.out.println("Server received: " + clientInfo);
            clientInfoList.add(clientInfo);
        }

        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }

        public Socket getSocket() {
            return socket;
        }


        public boolean hasMessage() throws IOException {
            if (in.ready()) {
                String msg = in.readLine();
                if (msg != null) {
                    messages.add(msg);
                    return true;
                }
            }
            return false;
        }

        public String getMessage() {
            return messages.poll();
        }

        public void sendMessage(String message) {
            System.out.println("Server sends message: " + message);
            out.println(message);
            out.flush();
        }

        @Override
        public void run() {
            try {
                while (GameMenuController.serverThreadIsRunning) {
                    String message = in.readLine();
                    if (message != null) {
                        messages.add(message);
                        System.out.println("Server handler received: " + message);
                        broadcastMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}