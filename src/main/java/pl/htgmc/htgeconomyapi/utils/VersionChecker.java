package pl.htgmc.htgeconomyapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class VersionChecker {

    private static final String GITHUB_API = "https://api.github.com/repos/WaleonGames/HTGEconomyAPI/releases";

    public static boolean checkVersion(Plugin plugin) {
        String localVersion = plugin.getDescription().getVersion();

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
                    if (entry.contains("\"tag_name\"") && !entry.contains("\"draft\":true") && !entry.contains("\"prerelease\":true")) {
                        String tag = entry.split("\"tag_name\":\"")[1].split("\"")[0];
                        versions.add(tag);
                    }
                }

                if (versions.isEmpty()) {
                    Bukkit.getLogger().warning("Nie znaleziono żadnych oficjalnych wydań HTGEconomyAPI.");
                    return true;
                }

                versions.sort(Comparator.reverseOrder());
                List<String> supported = versions.subList(0, Math.min(3, versions.size()));

                if (!supported.contains(localVersion)) {
                    Bukkit.getLogger().severe("Wersja HTGEconomyAPI (" + localVersion + ") nie jest wspierana!");
                    Bukkit.getLogger().severe("Aktualne wspierane wersje to:");
                    for (int i = 0; i < supported.size(); i++) {
                        Bukkit.getLogger().severe("  " + i + ": " + supported.get(i));
                    }
                    return false;
                }

                Bukkit.getLogger().info("Wersja HTGEconomyAPI (" + localVersion + ") jest wspierana (" + supported.indexOf(localVersion) + ": najnowsze).");
                return true;
            }

        } catch (Exception e) {
            Bukkit.getLogger().warning("Nie udało się sprawdzić wersji HTGEconomyAPI z GitHub. Kontynuacja mimo błędu.");
            return true;
        }
    }
}
