package pl.htgmc.htgeconomyapi.api;

import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EconomyAPI {

    /**
     * Pobiera aktualny balans gracza.
     *
     * @param uuid UUID gracza
     * @return ilość monet gracza, 0 jeśli brak zapisu
     */
    public static double get(UUID uuid) {
        return CoinStorage.getCoins(uuid);
    }

    /**
     * Ustawia balans gracza na podaną wartość.
     * Loguje operację jako "SET".
     *
     * @param uuid   UUID gracza
     * @param amount nowa ilość monet
     */
    public static void set(UUID uuid, double amount) {
        CoinStorage.setCoins(uuid, amount);
        log(uuid, "SET", amount);
    }

    /**
     * Dodaje graczowi określoną ilość monet.
     * Loguje operację jako "ADD".
     *
     * @param uuid   UUID gracza
     * @param amount ilość monet do dodania
     */
    public static void add(UUID uuid, double amount) {
        CoinStorage.addCoins(uuid, amount);
        log(uuid, "ADD", amount);
    }

    /**
     * Odejmuje graczowi określoną ilość monet (nie spada poniżej 0).
     * Loguje operację jako "REMOVE".
     *
     * @param uuid   UUID gracza
     * @param amount ilość monet do odjęcia
     */
    public static void remove(UUID uuid, double amount) {
        CoinStorage.removeCoins(uuid, amount);
        log(uuid, "REMOVE", amount);
    }

    /**
     * Kara finansowa – odejmuje monety bez sprawdzania dolnej granicy (saldo może być ujemne).
     * Loguje operację jako "PUNISH".
     *
     * @param uuid   UUID gracza
     * @param amount ilość monet do odjęcia
     */
    public static void punish(UUID uuid, double amount) {
        CoinStorage.punishCoins(uuid, amount);
        log(uuid, "PUNISH", amount);
    }

    /**
     * Sprawdza, czy gracz posiada co najmniej podaną ilość monet.
     *
     * @param uuid   UUID gracza
     * @param amount ilość monet do sprawdzenia
     * @return true, jeśli saldo >= amount
     */
    public static boolean has(UUID uuid, double amount) {
        return get(uuid) >= amount;
    }

    /**
     * Zapisuje aktualny stan wszystkich balansów do pliku (coins.yml).
     */
    public static void save() {
        CoinStorage.save();
    }

    /**
     * Zwraca mapę wszystkich graczy i ich aktualnych sald.
     *
     * @return mapa UUID → saldo
     */
    public static Map<UUID, Double> getAllBalances() {
        return new HashMap<>(CoinStorage.getAllBalances());
    }

    /**
     * Zwraca listę graczy posortowaną malejąco po ilości monet.
     *
     * @param limit maksymalna liczba graczy w liście (np. 10 dla TOP 10)
     * @return lista wpisów UUID → saldo posortowana malejąco
     */
    public static List<Map.Entry<UUID, Double>> getTopBalances(int limit) {
        return CoinStorage.getAllBalances().entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .toList();
    }

    /**
     * Zapisuje operację ekonomiczną do logu (economy.log).
     *
     * @param uuid   UUID gracza
     * @param action typ operacji (SET, ADD, REMOVE, PUNISH)
     * @param amount wartość zmiany
     */
    private static void log(UUID uuid, String action, double amount) {
        try {
            File folder = new File("plugins/HTGEconomyAPI/logs");
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folder, "economy.log");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.write("[" + time + "] [" + action + "] UUID=" + uuid +
                        " AMOUNT=" + String.format("%.2f", amount));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
