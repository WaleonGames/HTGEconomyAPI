package pl.htgmc.htgeconomyapi.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConfig {

    private static final Map<String, String> currencyTags = new HashMap<>();
    private static File file;
    private static FileConfiguration config;
    private static final DecimalFormat numberFormat = new DecimalFormat("#,##0.00");

    public static void load(File pluginFolder) {
        file = new File(pluginFolder, "currencies.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                config.set("coins", "🪙");
                config.set("tokens", "✴️");
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            currencyTags.put(key, config.getString(key));
        }
    }

    public static String getTag(String currencyId) {
        return currencyTags.getOrDefault(currencyId, "");
    }

    public static Map<String, String> getAll() {
        return currencyTags;
    }

    public static String format(String currencyId, double amount) {
        String tag = getTag(currencyId);

        // Formatowanie: np. 1,234,567.89
        String formatted = numberFormat.format(amount);

        return formatted + " " + tag;
    }
}
