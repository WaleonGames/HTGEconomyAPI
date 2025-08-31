package pl.htgmc.htgeconomyapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class VersionChecker {

    private static final String GITHUB_API = "https://api.github.com/repos/WaleonGames/HTGEconomyAPI/releases";

    /**
     * Sprawdza, czy aktualna wersja pluginu jest wspierana na podstawie GitHub API.
     *
     * @param plugin instancja pluginu
     * @return true jeśli wersja jest wspierana, false jeśli nie
     */
    public static boolean checkVersion(Plugin plugin) {
        String localVersion = plugin.getDescription().getVersion();
        String localSeries = normalizeVersion(localVersion);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API).openConnection();
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("User-Agent", "HTGEconomyAPI-VersionChecker");
            connection.setConnectTimeout(5000);

            try (Scanner scanner = new Scanner(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder json = new StringBuilder();
                while (scanner.hasNextLine()) json.append(scanner.nextLine());

                List<String> versions = new ArrayList<>();
                for (String entry : json.toString().split("\\{")) {
                    if (entry.contains("\"tag_name\"") &&
                            !entry.contains("\"draft\":true") &&
                            !entry.contains("\"prerelease\":true")) {
                        String tag = entry.split("\"tag_name\":\"")[1].split("\"")[0];
                        versions.add(tag);
                    }
                }

                if (versions.isEmpty()) {
                    Bukkit.getLogger().warning("Nie znaleziono żadnych oficjalnych wydań HTGEconomyAPI.");
                    return true;
                }

                // sortujemy po stringach (np. 0.0.8.2p-beta → 0.0.8.2p-beta)
                versions.sort(Comparator.reverseOrder());

                // bierzemy najnowsze 5
                List<String> supported = versions.subList(0, Math.min(5, versions.size()));

                // normalizujemy serie
                Set<String> supportedSeries = new HashSet<>();
                for (String v : supported) {
                    supportedSeries.add(normalizeVersion(v));
                }

                if (!supportedSeries.contains(localSeries)) {
                    Bukkit.getLogger().severe("⚠ Wersja HTGEconomyAPI (" + localVersion + ", seria " + localSeries + ") nie jest już wspierana!");
                    Bukkit.getLogger().severe("Obsługiwane obecnie serie:");
                    for (String s : supportedSeries) {
                        Bukkit.getLogger().severe("  - " + s);
                    }
                    return false;
                }

                Bukkit.getLogger().info("✅ Wersja HTGEconomyAPI (" + localVersion + ") jest wspierana (seria: " + localSeries + ").");
                return true;
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("⚠ Nie udało się sprawdzić wersji HTGEconomyAPI z GitHub. Kontynuacja mimo błędu.");
            return true;
        }
    }

    /**
     * Normalizuje wersję do serii.
     * Przykłady:
     *  - 0.0.8.2p-beta → 0.0.8.x
     *  - 0.0.8.1 → 0.0.8.x
     *  - 0.0.8 → 0.0.8
     *  - 0.0.7.1 → 0.0.7.x
     *  - 0.0.7 → 0.0.7
     */
    public static String normalizeVersion(String version) {
        if (version == null || version.isEmpty()) return "unknown";

        // usuwamy np. "-beta", "-p"
        String clean = version.replaceAll("[-a-zA-Z]", "");
        String[] parts = clean.split("\\.");

        if (parts.length >= 4) {
            return parts[0] + "." + parts[1] + "." + parts[2] + ".x";
        }
        if (parts.length == 3) {
            return parts[0] + "." + parts[1] + "." + parts[2];
        }
        if (parts.length == 2) {
            return parts[0] + "." + parts[1];
        }
        return clean;
    }
}
