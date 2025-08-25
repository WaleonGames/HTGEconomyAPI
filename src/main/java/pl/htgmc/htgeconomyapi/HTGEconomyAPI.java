// === pl/htgmc/htgeconomyapi/HTGEconomyAPI.java ===
package pl.htgmc.htgeconomyapi;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import pl.htgmc.htgeconomyapi.commands.BankCommand;
import pl.htgmc.htgeconomyapi.commands.CoinsCommand;
import pl.htgmc.htgeconomyapi.commands.DynamicStatsCommand;
import pl.htgmc.htgeconomyapi.commands.TransferCommand;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;
import pl.htgmc.htgeconomyapi.data.CoinStorage;
import pl.htgmc.htgeconomyapi.listener.BankGuiListener;
import pl.htgmc.htgeconomyapi.listener.HistorieGuiListener;
import pl.htgmc.htgeconomyapi.penalty.PenaltyManager;
import pl.htgmc.htgeconomyapi.placeholder.HTGEconomyExpansion;
import pl.htgmc.htgeconomyapi.stats.EconomyStatsSender;
import pl.htgmc.htgeconomyapi.utils.VersionChecker;
import pl.htgmc.htgeconomyapi.taxes.WealthTax;

// === gratisy ===
import pl.htgmc.htgeconomyapi.gratisy.DailyRewardManager;
import pl.htgmc.htgeconomyapi.gratisy.GoldenWeekManager;

// === API ===
import pl.htgmc.htgeconomyapi.api.PluginSupportAPI;

import java.io.File;
import java.time.DayOfWeek;

public final class HTGEconomyAPI extends JavaPlugin {

    private static HTGEconomyAPI instance;

    // === MENEDŻERY GRATISÓW ===
    private DailyRewardManager dailyRewardManager;
    private GoldenWeekManager goldenWeekManager;

    // === API ===
    private PluginSupportAPI pluginSupportAPI;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("=====[ HTG Economy API ]=====");

        if (!VersionChecker.checkVersion(this)) {
            getLogger().severe("Plugin zostanie wyłączony z powodu niekompatybilnej wersji.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // === Inicjalizacja API wsparcia ===
        this.pluginSupportAPI = new PluginSupportAPI();
        getLogger().info("Sprawdzanie kompatybilności API...");
        getLogger().info(pluginSupportAPI.getStatusMessage("0.0.7.1-beta")); // tu np. minimalna wymagana

        // === INTEGRACJE Z ZEWNĘTRZNYMI PLUGINAMI ===
        getLogger().info("===[ Obsługiwane Pluginy ]===");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new HTGEconomyExpansion().register();
            getLogger().info("Zintegrowano z PlaceholderAPI.");
        } else {
            getLogger().warning("PlaceholderAPI nie wykryto, serwer wyłącza się.");
            getServer().shutdown();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            getLogger().info("Wykryto poprawnie LuckPerms.");
        } else {
            getLogger().warning("LuckPerms nie wykryto, serwer wyłącza się.");
            getServer().shutdown();
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().info("Wykryto poprawnie Vault.");
        } else {
            getLogger().warning("Vault nie wykryto, serwer wyłącza się.");
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

        // === GRATISY ===
        File gratisyDir = new File(getDataFolder(), "gratisy");
        if (!gratisyDir.exists()) gratisyDir.mkdirs();

        this.dailyRewardManager = new DailyRewardManager(getDataFolder());
        this.goldenWeekManager = new GoldenWeekManager(getDataFolder(), 0.01, 5.0, DayOfWeek.MONDAY);

        // scheduler – sprawdzanie złotego tygodnia co godzinę
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                goldenWeekManager.tick();
            } catch (Exception e) {
                getLogger().warning("Błąd podczas sprawdzania GoldenWeek: " + e.getMessage());
            }
        }, 20L, 20L * 60 * 60);

        // === KOMENDY ===
        getLogger().info("===[ Obsługa Komendów ]===");

        setupCommand("coins", new CoinsCommand(), "/coins <gracz> [dodaj|usun|ustaw|kara] <kwota> <powód>", "Zarządzaj monetami graczy.");
        setupCommand("dynamics", new DynamicStatsCommand(), "/dynamics", "Pokazuje statystyki dynamicznej ekonomii.");
        setupCommand("transfer", new TransferCommand(vaultEconomy), "/transfer [tohtg|tovault|toplayer] <kwota>", "Przewalutuj środki między Vault a HTG lub przekaż HTG innemu graczowi");
        setupCommand("bank", new BankCommand(), "", "");

        Bukkit.getPluginManager().registerEvents(new BankGuiListener(), this);
        Bukkit.getPluginManager().registerEvents(new HistorieGuiListener(), this);

        // === HARMONOGRAM: Wysyłka statystyk ekonomii na Discord co 60 sekund ===
        getLogger().info("Rozpoczynanie automatycznej wysyłki statystyk na Discord...");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                EconomyStatsSender.sendStats();
            } catch (Exception e) {
                getLogger().warning("Błąd podczas wysyłania statystyk ekonomii: " + e.getMessage());
            }
        }, 20L * 60, 20L * 60);

        // === HARMONOGRAM: Podatek od trzymania pieniędzy co 6 godzin ===
        getLogger().info("Rozpoczynanie naliczania podatku od trzymania pieniędzy...");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            try {
                WealthTax.apply();
                getLogger().info("Podatek od trzymania pieniędzy został naliczony.");
            } catch (Exception e) {
                getLogger().warning("Błąd podczas naliczania podatku od trzymania pieniędzy: " + e.getMessage());
            }
        }, 20L, 20L * 60 * 60 * 6);

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

    public DailyRewardManager getDailyRewardManager() {
        return dailyRewardManager;
    }

    public GoldenWeekManager getGoldenWeekManager() {
        return goldenWeekManager;
    }

    public PluginSupportAPI getPluginSupportAPI() {
        return pluginSupportAPI;
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
