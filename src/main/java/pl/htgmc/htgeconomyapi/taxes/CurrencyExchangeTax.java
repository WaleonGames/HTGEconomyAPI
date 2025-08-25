package pl.htgmc.htgeconomyapi.taxes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class CurrencyExchangeTax {

    private final Plugin plugin;
    private FileConfiguration taxConfig;

    public CurrencyExchangeTax(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        File file = new File(plugin.getDataFolder(), "taxes.yml");
        if (!file.exists()) {
            plugin.saveResource("taxes.yml", false);
        }
        taxConfig = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Zwraca stałą stawkę podatku z pliku taxes.yml
     */
    public double getTaxRate() {
        return taxConfig.getDouble("currency_exchange_tax.rate", 0.05);
    }

    /**
     * Zwraca kwotę po odjęciu podatku.
     */
    public double applyTax(double amount) {
        double rate = getTaxRate();
        return amount * (1.0 - rate);
    }
}
