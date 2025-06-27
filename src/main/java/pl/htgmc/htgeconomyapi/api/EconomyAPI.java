// === pl/htgmc/htgeconomyapi/api/EconomyAPI.java ===
package pl.htgmc.htgeconomyapi.api;

import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.util.UUID;

public class EconomyAPI {

    public static double get(UUID uuid) {
        return CoinStorage.getCoins(uuid);
    }

    public static void set(UUID uuid, double amount) {
        CoinStorage.setCoins(uuid, amount);
    }

    public static void add(UUID uuid, double amount) {
        CoinStorage.addCoins(uuid, amount);
    }

    public static void remove(UUID uuid, double amount) {
        CoinStorage.removeCoins(uuid, amount);
    }

    public static void punish(UUID uuid, double amount) {
        CoinStorage.punishCoins(uuid, amount);
    }

    public static boolean has(UUID uuid, double amount) {
        return get(uuid) >= amount;
    }

    public static void save() {
        CoinStorage.save();
    }
}
