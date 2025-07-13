package pl.htgmc.htgeconomyapi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.htgmc.htgeconomyapi.gui.BankGui;

public class BankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Ta komenda jest dostępna tylko dla graczy.");
            return true;
        }

        BankGui.open(player);
        return true;
    }
}
