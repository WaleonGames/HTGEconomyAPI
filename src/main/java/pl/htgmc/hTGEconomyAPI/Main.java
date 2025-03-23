package pl.htgmc.hTGEconomyAPI;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("EconomyAPI Plugin włączony!");

        // Możemy zarejestrować API, ale nie musimy robić nic szczególnego, jeśli nie mamy komend
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("EconomyAPI Plugin wyłączony!");
    }
}
