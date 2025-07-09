package pl.htgmc.htgeconomyapi.analysis;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * MultiEconomyAnalyzer – analizuje saldo gracza z różnych ekonomii.
 */
public class MultiEconomyAnalyzer {

    private static Economy vaultEconomy;

    public static void setVaultEconomy(Economy economy) {
        vaultEconomy = economy;
    }

    public static double getVaultBalance(UUID uuid) {
        if (vaultEconomy == null) return 0.0;
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return vaultEconomy.getBalance(player);
    }

    public static double getHTGBalance(UUID uuid) {
        return CoinStorage.getCoins(uuid);
    }

    public static double getCombinedBalance(UUID uuid) {
        return getVaultBalance(uuid) + getHTGBalance(uuid);
    }

    public static Map<String, Double> getBalanceBreakdown(UUID uuid) {
        Map<String, Double> breakdown = new LinkedHashMap<>();
        breakdown.put("Vault", getVaultBalance(uuid));
        breakdown.put("HTG", getHTGBalance(uuid));
        breakdown.put("Suma", getCombinedBalance(uuid));
        return breakdown;
    }

    public static String getDominantEconomy(UUID uuid) {
        double vault = getVaultBalance(uuid);
        double htg = getHTGBalance(uuid);

        if (vault > htg) return "Vault";
        if (htg > vault) return "HTG";
        return "Równa";
    }
}
