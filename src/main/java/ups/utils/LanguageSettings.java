package ups.utils;

import java.util.Locale;

public class LanguageSettings {
    private static Locale currentLocale = Locale.GERMAN; // Default to German

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }
}
