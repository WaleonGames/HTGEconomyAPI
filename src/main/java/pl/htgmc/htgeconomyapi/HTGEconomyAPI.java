// === pl/htgmc/htgeconomyapi/HTGEconomyAPI.java ===
package pl.htgmc.htgeconomyapi;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pl.htgmc.htgeconomyapi.commands.CoinsCommand;
import pl.htgmc.htgeconomyapi.data.CoinStorage;
import pl.htgmc.htgeconomyapi.penalty.PenaltyManager;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;

public final class HTGEconomyAPI extends JavaPlugin {

    private static HTGEconomyAPI instance;

    @Override
    public void onEnable() {
        instance = this;
        CoinStorage.load(getDataFolder());
        CurrencyConfig.load(getDataFolder());
        PenaltyManager.init(getDataFolder());

        PluginCommand coinsCmd = getCommand("coins");
        if (coinsCmd != null) {
            CoinsCommand command = new CoinsCommand();
            coinsCmd.setExecutor(command);
            coinsCmd.setTabCompleter(command);
            coinsCmd.setUsage("/coins <gracz> [dodaj|usun|ustaw|kara] <kwota> <powód>");
            coinsCmd.setDescription("Zarządzaj monetami graczy.");
        }

        getLogger().info("HTGEconomyAPI uruchomione ✔");
    }

    @Override
    public void onDisable() {
        CoinStorage.save();
        getLogger().info("Coins zapisane ✔");
    }

    public static HTGEconomyAPI getInstance() {
        return instance;
    }
}
