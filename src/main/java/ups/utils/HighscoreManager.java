package ups.utils;

import ups.model.Highscore;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
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

    public void saveHighscore(Highscore entry) {
        List<Highscore> existingHighscores = loadHighscores();

        Optional<Highscore> existingEntry = existingHighscores.stream()
                .filter(e -> e.getPlayerName().equals(entry.getPlayerName()))
                .findFirst();

        if (existingEntry.isPresent()) {
            if (entry.getScore() > existingEntry.get().getScore()) {
                existingEntry.get().setScore(entry.getScore());
                System.out.printf("Updated highscore for player %s: %d%n", entry.getPlayerName(), entry.getScore());
            }
        } else {
            existingHighscores.add(entry);
            System.out.printf("Added new highscore for player %s: %d%n", entry.getPlayerName(), entry.getScore());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE))) {
            oos.writeObject(existingHighscores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Highscore> loadHighscores() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Highscore> highscores = (List<Highscore>) ois.readObject();
            highscores.sort(Comparator.comparingInt(Highscore::getScore).reversed()); // Sortieren nach Punktzahl absteigend
            return highscores;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
