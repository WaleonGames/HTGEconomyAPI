// === pl/htgmc/htgeconomyapi/HTGEconomyAPI.java ===
package pl.htgmc.htgeconomyapi;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import pl.htgmc.htgeconomyapi.analysis.WealthAnalyzer;
import pl.htgmc.htgeconomyapi.api.EconomyAPI;
import pl.htgmc.htgeconomyapi.commands.CoinsCommand;
import pl.htgmc.htgeconomyapi.commands.DynamicStatsCommand;
import pl.htgmc.htgeconomyapi.commands.TransferCommand;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;
import pl.htgmc.htgeconomyapi.data.CoinStorage;
import pl.htgmc.htgeconomyapi.penalty.PenaltyManager;
import pl.htgmc.htgeconomyapi.placeholder.HTGEconomyExpansion;
import pl.htgmc.htgeconomyapi.stats.EconomyStatsSender;

public final class HTGEconomyAPI extends JavaPlugin {

    private static HTGEconomyAPI instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("=====[ HTG Economy API ]=====");

        // === INTEGRACJE Z ZEWNĘTRZNYMI PLUGINAMI ===
        getLogger().info("===[ Obsługiwane Pluginy ]===");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HTGEconomyExpansion().register();
            getLogger().info("Zintegrowano z PlaceholderAPI.");
        } else {
            getLogger().warning("PlaceholderAPI nie wykryto, serwer wyłącza sie.");
            getServer().shutdown();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            getLogger().info("Wykryto poprawnie LuckPerms.");
        } else {
            getLogger().warning("LuckPerms nie wykryto, serwer wyłącza sie.");
            getServer().shutdown();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().info("Wykryto poprawnie Vault.");
        } else {
            getLogger().warning("Vault nie wykryto, serwer wyłącza sie.");
            getServer().shutdown();
        }

        // === WCZYTYWANIE KONFIGURACJI I DANYCH ===
        getLogger().info("===[ Wczytywanie Konfiguracji Dan ]===");

        getLogger().info("Ładowanie danych monet i konfiguracji...");
        CoinStorage.load(getDataFolder());
        CurrencyConfig.load(getDataFolder());
        EconomyStatsSender.loadHistory();
        PenaltyManager.init(getDataFolder());
        Economy vaultEconomy = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();

        // === KOMENDY ===
        getLogger().info("===[ Obsługa Komendów ]===");

        setupCommand("coins", new CoinsCommand(), "/coins <gracz> [dodaj|usun|ustaw|kara] <kwota> <powód>", "Zarządzaj monetami graczy.");
        setupCommand("dynamics", new DynamicStatsCommand(), "/dynamics", "Pokazuje statystyki dynamicznej ekonomii.");
        setupCommand("transfer", new TransferCommand(vaultEconomy), "/transfer [tohtg|tovault|toplayer] <kwota>", "Przewalutuj środki między Vault a HTG lub przekaż HTG innemu graczowi");

        // === HARMONOGRAM: Wysyłka statystyk ekonomii na Discord co 60 sekund ===
        getLogger().info("Rozpoczynanie automatycznej wysyłki statystyk na Discord...");

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                EconomyStatsSender.sendStats();
            } catch (Exception e) {
                getLogger().warning("Błąd podczas wysyłania statystyk ekonomii: " + e.getMessage());
            }
        }, 20L * 60, 20L * 60);

        getLogger().info("Plugin HTGEconomyAPI został pomyślnie uruchomiony.");
        getLogger().info("===============================");
    }

    @Override
    public void onDisable() {
        CoinStorage.save();
        getLogger().info("Coins zapisane przed wyłączeniem.");
    }

    public static HTGEconomyAPI getInstance() {
        return instance;
    }

    private void setupCommand(String name, Object executor, String usage, String description) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            if (executor instanceof CommandExecutor exec) {
                command.setExecutor(exec);
            }
            if (executor instanceof TabCompleter completer) {
                command.setTabCompleter(completer);
            }
            command.setUsage(usage);
            command.setDescription(description);
            getLogger().info("Komenda /" + name + " zarejestrowana.");
        } else {
            getLogger().warning("Komenda /" + name + " nie została znaleziona w plugin.yml!");
        }
    }
}
