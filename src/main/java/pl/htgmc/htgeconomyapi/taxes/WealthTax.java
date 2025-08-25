package pl.htgmc.htgeconomyapi.taxes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WealthTax {

    private static Plugin plugin;
    private static FileConfiguration taxConfig;

    public WealthTax(Plugin pl) {
        plugin = pl;
        loadConfig();
    }

    public static void apply() {
        if (plugin == null) return;

        plugin.getLogger().info("[TAX] Rozpoczynam naliczanie podatku od trzymania pieniędzy...");

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            UUID uuid = offlinePlayer.getUniqueId();
            double balance = CoinStorage.getCoins(uuid);

            double rate = getWealthTaxRate(balance);
            if (rate <= 0) continue;

            double tax = balance * rate;
            CoinStorage.removeCoins(uuid, tax);

            logTax(uuid, balance, tax);
        }

        plugin.getLogger().info("[TAX] Naliczanie podatku zakończone.");
    }

    private static void loadConfig() {
        File file = new File(plugin.getDataFolder(), "taxes.yml");
        if (!file.exists()) {
            plugin.saveResource("taxes.yml", false);
        }
        taxConfig = YamlConfiguration.loadConfiguration(file);
    }

    private static double getWealthTaxRate(double balance) {
        List<Map<?, ?>> thresholds = taxConfig.getMapList("wealth_tax.thresholds");
        double rate = 0.0;
        for (Map<?, ?> entry : thresholds) {
            double amount = ((Number) entry.get("amount")).doubleValue();
            double entryRate = ((Number) entry.get("rate")).doubleValue();
            if (balance >= amount) {
                rate = entryRate;
            }
        }
        return rate;
    }

    private static void logTax(UUID player, double baseAmount, double tax) {
        String name = plugin.getServer().getOfflinePlayer(player).getName();
        plugin.getLogger().info(String.format("[TAX] %s zapłacił %.2f (%.1f%%) podatku od majątku (saldo: %.2f)",
                name, tax, (tax / baseAmount * 100), baseAmount));
    }
}
