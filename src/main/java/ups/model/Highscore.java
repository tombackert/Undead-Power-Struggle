package ups.model;

import java.io.Serializable;

/**
 * Highscore class to store player name and score.
 */
public class Highscore implements Serializable {
    private static final long serialVersionUID = 8846393618553702593L;
    private final String playerName;
    private int score;

    /**
     * Constructor for Highscore class.
     * @param playerName name of the player.
     * @param score score of the player.
     */
    public Highscore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    /**
     * Getter for player name.
     * @return player name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Getter for player score.
     * @return player score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter for player score.
     * @param score player score.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Override toString method to print Highscore object.
     * @return Highscore object as string.
     */
    @Override
    public String toString() {
        return "Highscore{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}
