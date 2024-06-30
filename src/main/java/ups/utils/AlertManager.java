package ups.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.ResourceBundle;
import ups.utils.LanguageSettings;

public class AlertManager {

    private static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("messages", LanguageSettings.getCurrentLocale());
    }

    public static void showAlert(String messageKey) {
        ResourceBundle bundle = getBundle();
        String message = bundle.getString(messageKey);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(bundle.getString("alert.title"));  // Annahme, dass "alert_title" im ResourceBundle definiert ist
        alert.setHeaderText(null);  // Kein Header-Text
        alert.setContentText(message);
        alert.showAndWait();
    }
}
