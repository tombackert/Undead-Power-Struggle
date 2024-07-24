import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ups.model.Highscore;

import static org.junit.jupiter.api.Assertions.*;

public class HighscoreTest {

    private Highscore highscore;

    @BeforeEach
    public void setUp() {
        highscore = new Highscore("Player1", 100);
    }

    @Test
    public void testConstructor() {
        assertNotNull(highscore);
        assertEquals("Player1", highscore.getPlayerName());
        assertEquals(100, highscore.getScore());
    }

    @Test
    public void testGetPlayerName() {
        assertEquals("Player1", highscore.getPlayerName());
    }

    @Test
    public void testGetScore() {
        assertEquals(100, highscore.getScore());
    }

    @Test
    public void testSetScore() {
        highscore.setScore(200);
        assertEquals(200, highscore.getScore());
    }

    @Test
    public void testToString() {
        String expectedString = "Highscore{playerName='Player1', score=100}";
        assertEquals(expectedString, highscore.toString());
    }
}
