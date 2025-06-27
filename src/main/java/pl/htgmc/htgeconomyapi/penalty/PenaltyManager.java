// === pl/htgmc/htgeconomyapi/penalty/PenaltyManager.java ===
package pl.htgmc.htgeconomyapi.penalty;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pl.htgmc.htgeconomyapi.HTGEconomyAPI;
import pl.htgmc.htgeconomyapi.api.EconomyAPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class PenaltyManager {

    private static File logFile;

    public static void init(File pluginFolder) {
        File folder = new File(pluginFolder, "logs");
        if (!folder.exists()) folder.mkdirs();

        logFile = new File(folder, "penalties.log");
        try {
            if (!logFile.exists()) logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void apply(UUID uuid, double amount, String reason) {
        EconomyAPI.punish(uuid, amount);
        EconomyAPI.save();

        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        String name = target.getName() != null ? target.getName() : uuid.toString();

        String logEntry = String.format("[%s] - %s ukarany: -%.2f coins | Powód: %s\n",
                java.time.LocalDateTime.now(), name, amount, reason);

        HTGEconomyAPI.getInstance().getLogger().warning("[KARA] " + name + " - " + reason + " (-" + amount + " coins)");
        writeToLog(logEntry);
    }

    private static void writeToLog(String line) {
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
