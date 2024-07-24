package ups.utils;

public class ClientGameConnection {
    // Message from client to game
    private static String messageToGame = null;
    private static int messageToGameCount = 0;

    // Message from game to client
    private static String messageToClient = null;
    private static int messageToClientCount = 0;

    public synchronized static String getMessageToGame() {
        String m = messageToGame;
        messageToGame = null;
        return m;
    }

    public synchronized static void setMessageToGame(String m) {
        System.out.println("Write message to game: " + m);
        messageToGame = m;
        messageToGameCount++;
    }

    public synchronized static int getMessageToGameCount() {
        return messageToGameCount;
    }

    public synchronized static String getMessageToClient() {
        String m = messageToClient;
        messageToClient = null;
        return m;
    }

    public synchronized static void setMessageToClient(String m) {
        System.out.println("Write message to client: " + m);
        messageToClient = m;
        messageToClientCount++;
    }

    public synchronized static int getMessageToClientCount() {
        return messageToClientCount;
    }
}
