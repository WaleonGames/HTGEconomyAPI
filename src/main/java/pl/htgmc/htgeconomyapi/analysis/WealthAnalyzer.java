package pl.htgmc.htgeconomyapi.analysis;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;

import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WealthAnalyzer {

    public static double getTotalCoins() {
        return CoinStorage.getAllBalances().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public static double getAverageCoins() {
        Map<UUID, Double> map = CoinStorage.getAllBalances();
        if (map.isEmpty()) return 0.0;
        return getTotalCoins() / map.size();
    }

    public static int getPlayerCount() {
        return CoinStorage.getAllBalances().size();
    }

    public static Map<String, Integer> getGroupDistribution() {
        Map<String, Integer> groupCount = new HashMap<>();

        LuckPerms luckPerms;
        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            return Collections.emptyMap(); // LuckPerms niedostępny
        }

        UserManager userManager = luckPerms.getUserManager();

        for (UUID uuid : CoinStorage.getAllBalances().keySet()) {
            try {
                CompletableFuture<User> future = userManager.loadUser(uuid);
                User user = future.get(); // blokujące, ale OK przy starcie
                String group = user.getPrimaryGroup();

                groupCount.put(group, groupCount.getOrDefault(group, 0) + 1);
            } catch (Exception e) {
                System.err.println("[HTGEconomyAPI] Błąd odczytu rangi dla " + uuid + ": " + e.getMessage());
            }
        }

        return groupCount;
    }

    public static double getCombinedDynamicMultiplier() {
        // 1. Czynnik bogactwa
        double avgCoins = getAverageCoins();
        double wealthFactor = Math.min(1 + (avgCoins / 10000), 2.0); // max x2

        // 2. Czynnik rang
        Map<String, Integer> groups = getGroupDistribution();
        if (groups.isEmpty()) return wealthFactor; // tylko bogactwo

        Map<String, Double> weights = new HashMap<>();
        weights.put("default", 1.0);
        weights.put("vip", 1.2);
        weights.put("vip+", 1.5);

        int totalPlayers = 0;
        double totalWeight = 0.0;

        for (Map.Entry<String, Integer> entry : groups.entrySet()) {
            String group = entry.getKey().toLowerCase();
            int count = entry.getValue();
            double weight = weights.getOrDefault(group, 1.0);

            totalPlayers += count;
            totalWeight += weight * count;
        }

        double rankFactor = totalPlayers > 0 ? (totalWeight / totalPlayers) : 1.0;

        // 3. Mnożnik końcowy = coins * rangi
        double combined = wealthFactor * rankFactor;

        return Math.min(combined, 3.0); // maksymalnie x3
    }
}
