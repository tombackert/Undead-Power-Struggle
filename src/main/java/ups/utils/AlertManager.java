package ups.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class AlertManager {

    private static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("messages", LanguageSettings.getCurrentLocale());
    }

    public static void showAlert(String messageKey) {
        ResourceBundle bundle = getBundle();
        String message = bundle.getString(messageKey);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(bundle.getString("alert.title")); // Angenommen "alert.title" ist im ResourceBundle definiert
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWinner(String messageKey, String winnerName, int goldAmount) {
        ResourceBundle bundle = getBundle();
        String messageFormat = bundle.getString(messageKey);
        String message = MessageFormat.format(messageFormat, winnerName, goldAmount);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("alert.winner_title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
