package pl.htgmc.htgeconomyapi.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import pl.htgmc.htgeconomyapi.gui.BankGui;
import pl.htgmc.htgeconomyapi.gui.HistorieGui;

public class HistorieGuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (!view.getTitle().startsWith(ChatColor.DARK_GREEN + "Historia Transakcji")) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) return;

        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        switch (name) {
            case "Poprzednia strona" -> HistorieGui.previousPage(player);
            case "Następna strona" -> HistorieGui.nextPage(player);
            case "Zamknij" -> BankGui.open(player);
        }
    }
}
