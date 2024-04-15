package ups.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ups.model.GameBoard;

import java.io.IOException;

public class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    public void setUp() throws IOException {
        board = new GameBoard(10, 10, 0); // Angenommen, dies initialisiert das Board korrekt
    }

    @Test
    public void testColorAtPosition() {
        assertNotNull(board.getColor(1, 1), "Color at position (1,1) should not be null");
    }
}
