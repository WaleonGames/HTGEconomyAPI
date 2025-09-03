package pl.htgmc.htgeconomyapi.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;
import pl.htgmc.htgeconomyapi.database.DatabaseManager;
import pl.htgmc.htgeconomyapi.database.MySQLDatabase;
import pl.htgmc.htgeconomyapi.database.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * CoinStorage – obsługuje monety graczy w YAML lub bazie danych (MySQL/SQLite).
 */
public class CoinStorage {

    private static final Map<UUID, Double> balances = new HashMap<>();
    private static File file;
    private static FileConfiguration config;

    private static DatabaseManager databaseManager;
    private static boolean useDatabase = false;

    // Inicjalizacja
    public static void load(File pluginFolder, boolean databaseEnabled,
                            String type, String host, int port, String db, String user, String pass) {
        if (databaseEnabled) {
            if (type.equalsIgnoreCase("mysql")) {
                databaseManager = new MySQLDatabase(host, port, db, user, pass);
            } else {
                databaseManager = new SQLiteDatabase();
            }
            databaseManager.connect();
            databaseManager.setupTables();
            useDatabase = true;
        } else {
            loadFromYaml(pluginFolder);
        }
    }

    // YAML ładowanie
    private static void loadFromYaml(File pluginFolder) {
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
        balances.clear();

        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double value = config.getDouble(key);
                balances.put(uuid, value);
            } catch (IllegalArgumentException ex) {
                System.err.println("[HTGEconomyAPI] Błąd UUID w coins.yml: " + key);
            }
        }
    }

    // YAML zapis
    public static void save() {
        if (useDatabase) return; // w DB zapis idzie od razu
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

    // Pobierz monety
    public static double getCoins(UUID uuid) {
        if (useDatabase) return databaseManager.getBalance(uuid);
        return balances.getOrDefault(uuid, 0.0);
    }

    // Ustaw monety
    public static void setCoins(UUID uuid, double amount) {
        if (amount > 1_000_000) amount = 1_000_000;
        if (useDatabase) {
            databaseManager.setBalance(uuid, amount);
        } else {
            balances.put(uuid, amount);
        }
    }

    // Dodaj monety
    public static void addCoins(UUID uuid, double amount) {
        if (useDatabase) {
            databaseManager.addBalance(uuid, amount);
        } else {
            double total = getCoins(uuid) + amount;
            if (total > 1_000_000) total = 1_000_000;
            balances.put(uuid, total);
        }
    }

    // Odejmij monety
    public static void removeCoins(UUID uuid, double amount) {
        if (useDatabase) {
            databaseManager.removeBalance(uuid, amount);
        } else {
            setCoins(uuid, Math.max(0.0, getCoins(uuid) - amount));
        }
    }

    public static void punishCoins(UUID uuid, double amount) {
        removeCoins(uuid, amount);
    }

    // Pobierz wszystkie salda
    public static Map<UUID, Double> getAllBalances() {
        if (useDatabase && databaseManager instanceof SQLiteDatabase sqLite) {
            return sqLite.getAllBalances();
        }
        return new HashMap<>(balances);
    }

    // Formatowanie waluty
    public static String getFormattedCoins(UUID uuid) {
        double coins = getCoins(uuid);
        return CurrencyConfig.format("coins", coins);
    }

    // Suma monet
    public static double getAllCoins() {
        return getAllBalances().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // Zamknij połączenie
    public static void shutdown() {
        if (useDatabase && databaseManager != null) {
            databaseManager.disconnect();
        } else {
            save();
        }
    }
}
