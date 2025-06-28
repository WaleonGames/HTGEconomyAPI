package pl.htgmc.htgeconomyapi.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import pl.htgmc.htgeconomyapi.api.EconomyAPI;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;

import java.util.UUID;

public class HTGEconomyExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "HTGEconomyAPI";
    }

    @Override
    public String getAuthor() {
        return "ToJaWGYT";
    }

    @Override
    public String getVersion() {
        return "0.0.3-beta";
    }

    @Override
    public boolean persist() {
        return true; // nie wyłącza się przy reloadzie
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        UUID uuid = player.getUniqueId();

        if (params.equalsIgnoreCase("coins")) {
            double coins = EconomyAPI.get(uuid);
            String tag = CurrencyConfig.getTag("coins");
            return coins + " " + tag;
        }

        return null;
    }
}
