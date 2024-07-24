package ups.utils;

import java.io.Serializable;

public class TerrainCardDrawnMessage implements Serializable {
    private final String playerName;
    private final String terrainType;

    public TerrainCardDrawnMessage(String playerName, String terrainType) {
        this.playerName = playerName;
        this.terrainType = terrainType;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTerrainType() {
        return terrainType;
    }
}
