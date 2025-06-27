// === pl/htgmc/htgeconomyapi/data/CoinStorage.java ===
package pl.htgmc.htgeconomyapi.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * CoinStorage – przechowuje monety graczy w pliku YAML.
 */
public class CoinStorage {

    private static final Map<UUID, Double> balances = new HashMap<>();
    private static File file;
    private static FileConfiguration config;

    // Wczytywanie monet z pliku
    public static void load(File pluginFolder) {
        file = new File(pluginFolder, "coins.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        balances.clear(); // WAŻNE: czyścimy poprzednie dane

        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double value = config.getDouble(key);
                balances.put(uuid, value);
                System.out.println("[DEBUG] Załadowano: " + uuid + " => " + value);
            } catch (IllegalArgumentException ex) {
                System.err.println("[HTGEconomyAPI] Błąd parsowania UUID: " + key);
            }
        }
    }

    // Zapis monet do pliku
    public static void save() {
        if (config == null || file == null) return;
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            System.err.println("[HTGEconomyAPI] Błąd zapisu coins.yml: " + e.getMessage());
        }
    }

    // Pobierz ilość monet
    public static double getCoins(UUID uuid) {
        return balances.getOrDefault(uuid, 0.0);
    }

    // Ustaw monety
    public static void setCoins(UUID uuid, double amount) {
        balances.put(uuid, amount);
    }

    // Dodaj monety
    public static void addCoins(UUID uuid, double amount) {
        balances.put(uuid, getCoins(uuid) + amount);
    }

    // Odejmij monety (nie mniej niż 0)
    public static void removeCoins(UUID uuid, double amount) {
        setCoins(uuid, Math.max(0.0, getCoins(uuid) - amount));
    }

    public static void punishCoins(UUID uuid, double amount) {
        balances.put(uuid, getCoins(uuid) - amount);
    }
}
