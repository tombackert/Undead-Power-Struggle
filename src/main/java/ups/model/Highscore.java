package ups.model;

import java.io.Serializable;

public class Highscore implements Serializable {
    private static final long serialVersionUID = 8846393618553702593L;
    private final String playerName;
    private int score;

    public Highscore(String playerName, int score) {
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
        return "Highscore{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}
