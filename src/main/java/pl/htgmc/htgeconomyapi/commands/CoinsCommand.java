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

        // /coins – saldo gracza
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Ta komenda jest tylko dla graczy.");
                return true;
            }

            UUID uuid = player.getUniqueId();
            double balance = EconomyAPI.get(uuid);
            player.sendMessage(ChatColor.GREEN + "💰 Twoje saldo: " + ChatColor.GOLD + balance + " " + tag);
            return true;
        }

        // /coins manager <nick> ...
        if (args.length >= 2 && args[0].equalsIgnoreCase("manager")) {
            if (!sender.hasPermission("htgcoins.admin")) {
                sender.sendMessage(ChatColor.RED + "⛔ Brak uprawnień: htgcoins.admin");
                return true;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            UUID uuid = target.getUniqueId();

            if (args.length == 3 && args[2].equalsIgnoreCase("info")) {
                double balance = EconomyAPI.get(uuid);
                sender.sendMessage(ChatColor.GREEN + "💰 Saldo gracza " + target.getName() + ": " + ChatColor.GOLD + balance + " " + tag);
                return true;
            }

            if (args.length < 5) {
                sender.sendMessage(ChatColor.RED + "⛔ Użycie: /coins manager <nick> [dodaj|usun|ustaw|kara] <kwota> <powód>");
                return true;
            }

            String action = args[2].toLowerCase();
            double amount = parseDouble(args[3], sender);
            String reason = String.join(" ", Arrays.copyOfRange(args, 4, args.length));

            switch (action) {
                case "dodaj" -> {
                    EconomyAPI.add(uuid, amount);
                    EconomyAPI.save();
                    sender.sendMessage(ChatColor.GREEN + "✅ Dodano " + amount + " " + tag + " graczowi " + target.getName() + ". Powód: " + reason);

                    if (target.isOnline()) {
                        ((Player) target).sendMessage(ChatColor.GREEN + "💸 Otrzymałeś +" + amount + " " + tag + ". Powód: " + reason);
                    }
                }
                case "usun" -> {
                    EconomyAPI.remove(uuid, amount);
                    EconomyAPI.save();
                    sender.sendMessage(ChatColor.YELLOW + "➖ Usunięto " + amount + " " + tag + " od gracza " + target.getName() + ". Powód: " + reason);

                    if (target.isOnline()) {
                        ((Player) target).sendMessage(ChatColor.RED + "💸 Zabrano -" + amount + " " + tag + ". Powód: " + reason);
                    }
                }
                case "ustaw" -> {
                    EconomyAPI.set(uuid, amount);
                    EconomyAPI.save();
                    sender.sendMessage(ChatColor.AQUA + "🔁 Ustawiono saldo gracza " + target.getName() + " na " + amount + " " + tag + ". Powód: " + reason);

                    if (target.isOnline()) {
                        ((Player) target).sendMessage(ChatColor.AQUA + "💰 Twoje saldo zostało ustawione na " + amount + " " + tag + ". Powód: " + reason);
                    }
                }
                case "kara" -> {
                    PenaltyManager.apply(uuid, amount, reason);
                    EconomyAPI.save();
                    sender.sendMessage(ChatColor.DARK_RED + "⚠️ Ukarano gracza " + target.getName() + " kwotą -" + amount + " " + tag + ". Powód: " + reason);

                    if (target.isOnline()) {
                        ((Player) target).sendMessage(ChatColor.RED + "❗ Zostałeś ukarany kwotą -" + amount + " " + tag + ". Powód: " + reason);
                    }
                }
                default -> sender.sendMessage(ChatColor.RED + "❓ Nieznana akcja. Użyj: dodaj | usun | ustaw | kara | info");
            }

            return true;
        }

        // ✋ Odmowa innym graczom wpisującym /coins <nick>
        sender.sendMessage(ChatColor.RED + "⛔ Ta komenda nie istnieje. Użyj /coins lub /coins manager <nick>");
        return true;
    }

    private double parseDouble(String input, CommandSender sender) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "⛔ Podana kwota nie jest liczbą.");
            throw e;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("manager".startsWith(args[0].toLowerCase())) {
                completions.add("manager");
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("manager")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("manager")) {
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(args[2].toLowerCase())) {
                    completions.add(sub);
                }
            }
        }

        return completions;
    }
}
