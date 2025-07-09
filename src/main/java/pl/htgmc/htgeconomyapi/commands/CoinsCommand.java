// === pl/htgmc/htgeconomyapi/commands/CoinsCommand.java ===
package pl.htgmc.htgeconomyapi.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import pl.htgmc.htgeconomyapi.api.EconomyAPI;
import pl.htgmc.htgeconomyapi.config.CurrencyConfig;
import pl.htgmc.htgeconomyapi.penalty.PenaltyManager;

import java.util.*;

public class CoinsCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("dodaj", "usun", "ustaw", "kara", "info");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String currency = "coins";
        String tag = CurrencyConfig.getTag(currency);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("info"))) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "§a§lEkonomia &cUżycie: /coins <gracz> [dodaj|usun|ustaw|kara|info] <kwota> <powód>"));
            return true;
        }

        // /coins <nick>
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            UUID uuid = target.getUniqueId();
            double balance = EconomyAPI.get(uuid);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lEkonomia &7Saldo gracza &f" + target.getName() + "&7: &e" + balance + " " + tag));
            return true;
        }

        // /coins <nick> info
        if (args.length == 2 && args[1].equalsIgnoreCase("info")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            UUID uuid = target.getUniqueId();
            double balance = EconomyAPI.get(uuid);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lEkonomia &7Saldo gracza &f" + target.getName() + "&7: &e" + balance + " " + tag));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "§a§lEkonomia &cUżycie: /coins <gracz> [dodaj|usun|ustaw|kara] <kwota> <powód>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        UUID uuid = target.getUniqueId();
        String action = args[1].toLowerCase();
        double amount = parseDouble(args[2], sender);
        String reason = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        switch (action) {
            case "dodaj" -> {
                EconomyAPI.add(uuid, amount);
                EconomyAPI.save();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lEkonomia &7Dodano &e" + amount + " " + tag + " &7graczowi &f" + target.getName() + "&7. Powód: &f" + reason));
            }
            case "usun" -> {
                EconomyAPI.remove(uuid, amount);
                EconomyAPI.save();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lEkonomia &7Usunięto &e" + amount + " " + tag + " &7graczowi &f" + target.getName() + "&7. Powód: &f" + reason));
            }
            case "ustaw" -> {
                EconomyAPI.set(uuid, amount);
                EconomyAPI.save();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lEkonomia &7Ustawiono saldo gracza &f" + target.getName() + " &7na &e" + amount + " " + tag + "&7. Powód: &f" + reason));
            }
            case "kara" -> {
                PenaltyManager.apply(uuid, amount, reason);
                EconomyAPI.save();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lEkonomia &7Ukarano gracza &f" + target.getName() + "&7 kwotą &c-" + amount + " " + tag + "&7. Powód: &f" + reason));}
            default -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "§a§lEkonomia &cNieznana akcja. Użyj: dodaj | usun | ustaw | kara | info"));
        }

        return true;
    }

    private double parseDouble(String input, CommandSender sender) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "§a§lEkonomia &cPodana kwota nie jest liczbą."));
            throw e;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String name = p.getName();
                if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(name);
                }
            }
        }

        if (args.length == 2) {
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[1].toLowerCase())) {
                    completions.add(sub);
                }
            }
        }

        return completions;
    }
}
