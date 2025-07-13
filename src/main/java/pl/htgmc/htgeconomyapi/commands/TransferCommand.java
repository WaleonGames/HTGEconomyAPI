package pl.htgmc.htgeconomyapi.commands;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.htgmc.htgeconomyapi.analysis.WealthAnalyzer;
import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.util.*;

public class TransferCommand implements CommandExecutor, TabCompleter {

    private final Economy vault;

    private static final double CONFIRM_THRESHOLD = 1000.0; // w przyszłości: do systemu confirm
    private static final int CONVERSION_COOLDOWN_SECONDS = 60;

    private final Map<UUID, Long> lastConversion = new HashMap<>();

    public TransferCommand(Economy vault) {
        this.vault = vault;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§a§lEkonomia §cTylko gracz może użyć tej komendy.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§a§lEkonomia §cUżycie: /transfer <tohtg|tovault|toplayer> <kwota> [nick]");
            return true;
        }

        String direction = args[0].toLowerCase();
        UUID uuid = player.getUniqueId();
        double multiplier = WealthAnalyzer.getCombinedDynamicMultiplier();

        // === PRZEWALUTOWANIE: tohtg / tovault ===
        if (direction.equals("tohtg") || direction.equals("tovault")) {
            long now = System.currentTimeMillis();
            if (lastConversion.containsKey(uuid)) {
                long last = lastConversion.get(uuid);
                long elapsed = (now - last) / 1000;
                if (elapsed < CONVERSION_COOLDOWN_SECONDS) {
                    player.sendMessage("§a§lEkonomia §7Musisz odczekać " + (CONVERSION_COOLDOWN_SECONDS - elapsed) +
                            "s przed kolejnym przewalutowaniem.");
                    return true;
                }
            }

            String amountRaw = args[1].replace(",", ".");
            double amount;
            try {
                amount = Double.parseDouble(amountRaw);
            } catch (NumberFormatException e) {
                player.sendMessage("§a§lEkonomia §cNiepoprawna kwota.");
                return true;
            }

            if (amount <= 0) {
                player.sendMessage("§a§lEkonomia §cKwota musi być większa niż 0.");
                return true;
            }

            if (direction.equals("tohtg")) {
                double vaultBalance = vault.getBalance(player);
                if (vaultBalance < amount) {
                    player.sendMessage("§a§lEkonomia §cNie masz tylu środków w Vault. Masz: " +
                            String.format("%.2f", vaultBalance));
                    return true;
                }

                double converted = amount / multiplier;
                vault.withdrawPlayer(player, amount);
                CoinStorage.addCoins(uuid, converted);
                CoinStorage.save();

                lastConversion.put(uuid, now);
                player.sendMessage("§a§lEkonomia §7Przewalutowano §a" + amount + "⛁ Vault → §e" +
                        String.format("%.2f", converted) + " HTG §7(kurs x" +
                        String.format("%.2f", multiplier) + ")");
                return true;

            } else { // tovault
                double htgBalance = CoinStorage.getCoins(uuid);
                if (htgBalance < amount) {
                    player.sendMessage("§a§lEkonomia §cNie masz tylu środków w HTG. Masz: " +
                            String.format("%.2f", htgBalance));
                    return true;
                }

                double converted = amount * multiplier;
                CoinStorage.removeCoins(uuid, amount);
                CoinStorage.save();
                vault.depositPlayer(player, converted);

                lastConversion.put(uuid, now);
                player.sendMessage("§a§lEkonomia §7Przewalutowano §e" + amount + " HTG → §a" +
                        String.format("%.2f", converted) + "⛁ Vault §7(kurs x" +
                        String.format("%.2f", multiplier) + ")");
                return true;
            }

        }

        else if (direction.equals("toplayer")) {
            if (args.length != 3) {
                player.sendMessage("§a§lEkonomia §7Użycie: /transfer toplayer <kwota> <nick>");
                return true;
            }

            String amountStr = args[1].replace(",", ".");
            String targetName = args[2];
            double amount;

            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                player.sendMessage("§c§lEkonomia §cNiepoprawna kwota.");
                return true;
            }

            if (amount <= 0) {
                player.sendMessage("§a§lEkonomia §cKwota musi być większa niż 0.");
                return true;
            }

            UUID senderId = player.getUniqueId();
            double senderBalance = CoinStorage.getCoins(senderId);

            if (senderBalance < amount) {
                player.sendMessage("§a§lEkonomia §cNie masz wystarczająco HTG. Masz: " +
                        String.format("%.2f", senderBalance));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayerExact(targetName);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage("§a§lEkonomia §cGracz '" + targetName + "' musi być online, aby wykonać przelew.");
                return true;
            }

            UUID receiverId = targetPlayer.getUniqueId();

            CoinStorage.removeCoins(senderId, amount);
            CoinStorage.addCoins(receiverId, amount);
            CoinStorage.save();

            player.sendMessage("§a§lEkonomia §7Przesłałeś §e" + amount + " HTG §7do §a" + targetPlayer.getName());
            targetPlayer.sendMessage("§a§lEkonomia §7Otrzymałeś §e" + amount +
                    " HTG §7od §a" + player.getName());

            Bukkit.getLogger().info("[Ekonomia] Gracz " + player.getName() + " przelał " +
                    amount + " HTG do " + targetPlayer.getName() + ".");

            return true;
        }

        player.sendMessage("§a§lEkonomia §cNiepoprawny kierunek. Użyj `tohtg`, `tovault` lub `toplayer`.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("tohtg", "tovault", "toplayer");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("toplayer")) {
            List<String> playerNames = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                playerNames.add(p.getName());
            }
            return playerNames;
        }

        return Collections.emptyList();
    }
}
