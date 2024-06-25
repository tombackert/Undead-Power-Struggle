package ups.utils;

import java.io.Serializable;

public class HighscoreEntry implements Serializable {
    private static final long serialVersionUID = 8846393618553702593L; // Ensure this matches the serialized version
    private final String playerName;
    private int score;

    public HighscoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "HighscoreEntry{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}