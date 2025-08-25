package pl.htgmc.htgeconomyapi.api;

import java.io.InputStream;
import java.util.Properties;

public class PluginSupportAPI {

    /**
     * Wersja obecnie działającego HTGEconomyAPI.
     */
    public String getCurrentVersion() {
        return pl.htgmc.htgeconomyapi.HTGEconomyAPI.getInstance().getDescription().getVersion();
    }

    /**
     * Wersja HTGEconomyAPI, którą plugin X miał w pom.xml (wyciągnięta z jego pom.properties).
     */
    public String getDependencyVersion(Class<?> pluginMainClass) {
        try {
            String path = "/META-INF/maven/com.github.WaleonGames/HTGEconomyAPI/pom.properties";
            InputStream in = pluginMainClass.getResourceAsStream(path);
            if (in == null) return "unknown";

            Properties props = new Properties();
            props.load(in);
            return props.getProperty("version", "unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Sprawdza, czy plugin korzysta z tej samej wersji API co obecna.
     */
    public boolean isCompatible(Class<?> pluginMainClass) {
        String required = getDependencyVersion(pluginMainClass);
        return required.equals(getCurrentVersion());
    }

    /**
     * Zwraca komunikat statusu kompatybilności w czytelnej formie.
     *
     * @param requiredVersion wersja minimalna/wymagana
     * @return status (❌ / ⚠️ / ✅)
     */
    public String getStatusMessage(String requiredVersion) {
        String current = getCurrentVersion();

        if (current.equals(requiredVersion)) {
            return "✅ Pełna kompatybilność – używana wersja API: " + current;
        }

        // jeśli wersje różne, ale przynajmniej major się zgadza
        try {
            String[] cur = current.split("\\.");
            String[] req = requiredVersion.split("\\.");

            int curMajor = Integer.parseInt(cur[0]);
            int reqMajor = Integer.parseInt(req[0]);

            if (curMajor == reqMajor) {
                return "⚠️ Niepełna kompatybilność – wymagane: " + requiredVersion + ", posiadane: " + current;
            }
        } catch (Exception ignored) {}

        return "❌ Brak kompatybilności – wymagane: " + requiredVersion + ", posiadane: " + current;
    }
}
