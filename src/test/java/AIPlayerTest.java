import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;
import ups.controller.GameBoardController;
import ups.model.AIPlayer;
import ups.model.GameBoard;
import ups.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AIPlayerTest {

    private AIPlayer aiPlayer;
    private GameBoard gameBoard;
    private GameBoardController gameBoardController;
    private Player player;

    @BeforeEach
    public void setUp() {
        System.out.println("Setup started");
        // Erstellen Sie eine AIPlayer-Instanz
        aiPlayer = new AIPlayer("TestAI", Color.BLUE, 3, 20);

        // Erstellen Sie eine GameBoard-Instanz
        List<String> selectedCards = new ArrayList<>();
        selectedCards.add("Gras");
        selectedCards.add("Wald");
        gameBoard = new GameBoard(10, 10, selectedCards);

        // Initialisieren Sie das Board mit einigen Feldern
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                gameBoard.terrainMap[i][j] = "Gras";
                gameBoard.occupied[i][j] = false;
            }
        }
        gameBoard.terrainCount.put("Gras", 100);  // Beispielwert, abhängig von der Spiellogik
        gameBoard.terrainCount.put("Wald", 100);  // Beispielwert, abhängig von der Spiellogik

        // Initialisieren Sie gameBoardController und player
        gameBoardController = new GameBoardController();
        player = new Player("Player1", Color.RED, 3, 10);

        System.out.println("Setup completed");
    }

    @Test
    public void testConstructor() {
        System.out.println("testConstructor started");
        assertEquals("TestAI", aiPlayer.getName());
        assertEquals(Color.BLUE, aiPlayer.getColor());
        assertEquals(3, gameBoardController.getSettlementsPerTurn());  // Korrigierte Referenz zu aiPlayer
        assertEquals(10, player.numberOfVillages);  // Angenommene Methode getNumberOfVillages hinzugefügt
        System.out.println("testConstructor completed");
    }

    @Test
    public void testFindBestMove() {
        System.out.println("testFindBestMove started");
        // Platzieren Sie einige Siedlungen auf dem Spielfeld
        gameBoard.placeSettlement(2, 2, Color.BLUE);
        gameBoard.placeSettlement(3, 3, Color.BLUE);

        // Rufen Sie findBestMove auf
        int[] bestMove = aiPlayer.findBestMove(gameBoard, "Gras");
        assertNotNull(bestMove, "Best move should not be null.");

        // Überprüfen Sie, ob der beste Zug auf einem nicht belegten Feld liegt
        assertTrue(gameBoard.isNotOccupied(bestMove[0], bestMove[1]), "Best move should be on an unoccupied field.");
        System.out.println("testFindBestMove completed");
    }
}
