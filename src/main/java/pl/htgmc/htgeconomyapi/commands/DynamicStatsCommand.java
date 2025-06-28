package pl.htgmc.htgeconomyapi.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.htgmc.htgeconomyapi.analysis.WealthAnalyzer;

import java.util.Map;

public class DynamicStatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        double total = WealthAnalyzer.getTotalCoins();
        double avg = WealthAnalyzer.getAverageCoins();
        double multiplier = WealthAnalyzer.getCombinedDynamicMultiplier();
        Map<String, Integer> groups = WealthAnalyzer.getGroupDistribution();

        sender.sendMessage(ChatColor.GOLD + "📊 Statystyki ekonomii:");
        sender.sendMessage(ChatColor.YELLOW + "➤ Suma coins: " + ChatColor.WHITE + String.format("%.2f", total));
        sender.sendMessage(ChatColor.YELLOW + "➤ Średnia na gracza: " + ChatColor.WHITE + String.format("%.2f", avg));
        sender.sendMessage(ChatColor.YELLOW + "➤ Mnożnik dynamiczny: " + ChatColor.WHITE + String.format("%.2f", multiplier));

        if (!groups.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "➤ Rozkład rang:");
            for (Map.Entry<String, Integer> entry : groups.entrySet()) {
                sender.sendMessage(ChatColor.GRAY + "   ▸ " + entry.getKey() + ": " + entry.getValue());
            }
        } else {
            sender.sendMessage(ChatColor.GRAY + "Brak danych o rangach.");
        }

        return true;
    }
}
