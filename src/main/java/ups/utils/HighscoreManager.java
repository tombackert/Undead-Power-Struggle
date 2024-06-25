package ups.utils;

import ups.model.GameBoard;
import ups.model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HighscoreManager {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String HIGHSCORE_FILE = USER_HOME + File.separator + "highscores.dat";

    public HighscoreManager() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IllegalStateException("Could not create directory for highscore file.");
        }
    }

    public void saveHighscore(List<Player> players, GameBoard board) {
        List<HighscoreEntry> existingHighscores = loadHighscores();

        for (Player player : players) {
            int newGold = player.calculateGold(board);
            Optional<HighscoreEntry> existingEntry = existingHighscores.stream()
                    .filter(entry -> entry.getPlayerName().equals(player.getName()))
                    .findFirst();

            if (existingEntry.isPresent()) {
                if (newGold > existingEntry.get().getScore()) {
                    existingEntry.get().setScore(newGold);
                    System.out.printf("Updated highscore for player %s: %d%n", player.getName(), newGold); // Debug output
                }
            } else {
                existingHighscores.add(new HighscoreEntry(player.getName(), newGold));
                System.out.printf("Added new highscore for player %s: %d%n", player.getName(), newGold); // Debug output
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE))) {
            oos.writeObject(existingHighscores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<HighscoreEntry> loadHighscores() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<HighscoreEntry> highscoreEntries = (List<HighscoreEntry>) ois.readObject();
            System.out.println("Loaded Highscores: " + highscoreEntries); // Debug output
            return highscoreEntries;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}