import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;
import ups.model.GameBoard;
import ups.model.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {

    private GameBoard gameBoard;

    @BeforeEach
    public void setUp() {
        System.out.println("Setup started");
        List<String> selectedCards = new ArrayList<>();
        selectedCards.add("Gras");
        selectedCards.add("Wald");
        gameBoard = new GameBoard(10, 10, selectedCards);

        // Initialisiere das Board mit einigen Feldern
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                gameBoard.terrainMap[i][j] = "Gras";
                gameBoard.occupied[i][j] = false;
            }
        }
        gameBoard.terrainCount.put("Gras", 100);  // Beispielwert, abhängig von der Spiellogik
        gameBoard.terrainCount.put("Wald", 100);  // Beispielwert, abhängig von der Spiellogik
        System.out.println("Setup completed");
    }

    @Test
    public void testPlaceSettlement() {
        System.out.println("testPlaceSettlement started");
        int x = 2;
        int y = 2;
        Color color = Color.BLUE;

        assertTrue(gameBoard.isNotOccupied(x, y), "Field should be unoccupied before placing settlement.");
        gameBoard.placeSettlement(x, y, color);
        assertFalse(gameBoard.isNotOccupied(x, y), "Field should be occupied after placing settlement.");
        assertEquals(color, gameBoard.getColor(x, y), "Field color should match the placed settlement color.");
        System.out.println("testPlaceSettlement completed");
    }

    @Test
    public void testGetTerrainType() {
        System.out.println("testGetTerrainType started");
        gameBoard.terrainMap[0][0] = "Gras";
        String terrainType = gameBoard.getTerrainType(0, 0);
        assertNotNull(terrainType, "Terrain type should not be null.");
        assertEquals("Gras", terrainType, "Terrain type should be 'Gras'.");
        System.out.println("testGetTerrainType completed");
    }

    @Test
    public void testIsNotOccupied() {
        System.out.println("testIsNotOccupied started");
        assertTrue(gameBoard.isNotOccupied(2, 2), "Field should be unoccupied.");
        gameBoard.placeSettlement(2, 2, Color.BLUE);
        assertFalse(gameBoard.isNotOccupied(2, 2), "Field should be occupied after placing settlement.");
        System.out.println("testIsNotOccupied completed");
    }

    @Test
    public void testSetOwner() {
        System.out.println("testSetOwner started");
        Player player = new Player("TestPlayer", Color.BLUE, 3, 20);
        gameBoard.setOwner(0, 0, player);
        assertEquals(player, gameBoard.hexagonOwnership.get(new Point(0, 0)), "Owner should be set to the player.");
        System.out.println("testSetOwner completed");
    }

    @Test
    public void testIsTerrainAvailable() {
        System.out.println("testIsTerrainAvailable started");
        gameBoard.terrainCount.put("Gras", 1);
        assertTrue(gameBoard.isTerrainAvailable("Gras"), "Terrain 'Gras' should be available.");
        gameBoard.terrainCount.put("Gras", 0);
        assertFalse(gameBoard.isTerrainAvailable("Gras"), "Terrain 'Gras' should not be available.");
        System.out.println("testIsTerrainAvailable completed");
    }

    @Test
    public void testGetNeighbourTerrain() {
        System.out.println("testGetNeighbourTerrain started");
        String[] expectedTerrains = {"Gras", "Gras", "Gras", "Gras", "Gras", "Gras"};
        assertArrayEquals(expectedTerrains, gameBoard.getNeighbourTerrain(5, 5), "Neighbour terrains should match.");
        System.out.println("testGetNeighbourTerrain completed");
    }

    @Test
    public void testInitialize() {
        System.out.println("testInitialize started");
        try {
            gameBoard.initialize(0, 0, 0);
        } catch (Exception e) {
            fail("Initialization should not throw an exception.");
        }
        System.out.println("testInitialize completed");
    }
}
