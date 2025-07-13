package pl.htgmc.htgeconomyapi.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import pl.htgmc.htgeconomyapi.api.EconomyAPI;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;

import java.util.UUID;

public class HTGEconomyExpansion extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "htgeconomyapi"; // %htgeconomyapi_<parametr>%
    }

    @Override
    public String getAuthor() {
        return "ToJaWGYT";
    }

    @Override
    public String getVersion() {
        return "0.0.4-beta";
    }

    @Override
    public boolean persist() {
        return true; // Placeholder działa po /reload
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || !player.hasPlayedBefore()) return "0";

        UUID uuid = player.getUniqueId();

        // coins → np. %htgeconomyapi_coins%
        if (params.equalsIgnoreCase("coins")) {
            double coins = EconomyAPI.get(uuid);
            return CurrencyConfig.format("coins", coins); // np. 1 500.0 💰
        }

        // coins_plain → bez formatu, np. do użytku matematycznego
        if (params.equalsIgnoreCase("coins_plain")) {
            return String.valueOf(EconomyAPI.get(uuid));
        }

        return null;
    }
}
