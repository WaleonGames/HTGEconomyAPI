package pl.htgmc.hTGEconomyAPI;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class EconomyAPI {

    // Mapowanie gracza na jego saldo
    private static final Map<Player, Double> playerBalances = new HashMap<>();

    // Zwraca saldo gracza
    public static double getBalance(Player player) {
        return playerBalances.getOrDefault(player, 0.0);
    }

    // Dodaje pieniądze do salda gracza
    public static void addMoney(Player player, double amount) {
        double newBalance = getBalance(player) + amount;
        playerBalances.put(player, newBalance);
    }

    // Usuwa pieniądze z salda gracza
    public static void removeMoney(Player player, double amount) {
        double newBalance = getBalance(player) - amount;
        playerBalances.put(player, newBalance);
    }

    // Przesyła pieniądze między graczami
    public static void transferMoney(Player from, Player to, double amount) {
        if (getBalance(from) >= amount) {
            removeMoney(from, amount);
            addMoney(to, amount);
        }
    }
}
