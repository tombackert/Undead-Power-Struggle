package ups.utils;

import java.io.*;
import java.net.*;

import ups.controller.GameMenuController;
import ups.utils.ClientGameConnection;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String serverAddress, int port, String name, String color) throws IOException {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        // Send initial client info to the server
        sendMessage(name + ":" + color);
        System.out.println("Client connected to server at " + serverAddress + ":" + port);
    }

    public void start() {
        new Thread(new IncomingMessageHandler()).start();
        new Thread(new OutgoingMessageHandler()).start();
    }

    public void sendMessage(String message) {
        System.out.println("Client Sending message: " + message);
        out.println(message);
        out.flush();
    }

    private class IncomingMessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while (GameMenuController.clientThreadIsRunning && (message = in.readLine()) != null) {
                    System.out.println("Client received: " + message);
                    ClientGameConnection.setMessageToGame(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class OutgoingMessageHandler implements Runnable {
        @Override
        public void run() {
            try {
                while (GameMenuController.clientThreadIsRunning) {
                    String message = ClientGameConnection.getMessageToClient();
                    if (message != null) {
                        sendMessage(message);
                        System.out.println("Client sent: " + message);
                    }
                    // Sleep briefly to prevent tight loop and reduce CPU usage
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                System.out.println("OutgoingMessageHandler thread interrupted");
            }
        }
    }

    
}