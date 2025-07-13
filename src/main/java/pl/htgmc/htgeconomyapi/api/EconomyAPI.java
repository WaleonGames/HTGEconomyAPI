package pl.htgmc.htgeconomyapi.api;

import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EconomyAPI {

    public static double get(UUID uuid) {
        return CoinStorage.getCoins(uuid);
    }

    public static void set(UUID uuid, double amount) {
        CoinStorage.setCoins(uuid, amount);
        log(uuid, "SET", amount);
    }

    public static void add(UUID uuid, double amount) {
        CoinStorage.addCoins(uuid, amount);
        log(uuid, "ADD", amount);
    }

    public static void remove(UUID uuid, double amount) {
        CoinStorage.removeCoins(uuid, amount);
        log(uuid, "REMOVE", amount);
    }

    public static void punish(UUID uuid, double amount) {
        CoinStorage.punishCoins(uuid, amount);
        log(uuid, "PUNISH", amount);
    }

    public static boolean has(UUID uuid, double amount) {
        return get(uuid) >= amount;
    }

    public static void save() {
        CoinStorage.save();
    }

    private static void log(UUID uuid, String action, double amount) {
        try {
            File folder = new File("plugins/HTGEconomyAPI/logs");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "economy.log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + time + "] [" + action + "] UUID=" + uuid + " AMOUNT=" + String.format("%.2f", amount));
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
