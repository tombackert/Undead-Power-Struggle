package ups.model;

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final Color color;
    private final List<Settlement> settlements;
    private String currentTerrainCard;
    private int remainingSettlements;
    private int settlementsPlacedThisTurn;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.settlements = new ArrayList<>();
        this.remainingSettlements = 40; // Initiale Anzahl der Siedlungen
        this.settlementsPlacedThisTurn = 0; // Initialisiert mit 0
    }

    public boolean canPlaceSettlement() {
        return settlementsPlacedThisTurn < 3 && remainingSettlements > 0;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public void drawTerrainCard(String terrainType) {
        this.currentTerrainCard = terrainType;
    }

    public List<Settlement> getSettlements() {
        return settlements;
    }

    public String getCurrentTerrainCard() {
        return currentTerrainCard;
    }

    public int getRemainingSettlements() {
        return remainingSettlements;
    }

    public void placeSettlement() {
        if (canPlaceSettlement()) {
            remainingSettlements--;
            settlementsPlacedThisTurn++;
        } else {
            throw new IllegalStateException("Maximale Anzahl von Siedlungen für diesen Zug erreicht oder keine Siedlungen mehr verfügbar.");
        }
    }

    public void resetSettlementsPlacedThisTurn() {
        settlementsPlacedThisTurn = 0;
    }
}
