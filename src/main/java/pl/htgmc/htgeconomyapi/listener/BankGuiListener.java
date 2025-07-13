package pl.htgmc.htgeconomyapi.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import pl.htgmc.htgeconomyapi.gui.HistorieGui;

public class BankGuiListener implements Listener {

    private static final String TITLE = ChatColor.DARK_GREEN + "Twój Bank";

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        if (!view.getTitle().equals(TITLE)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) return;

        Material type = clicked.getType();
        String displayName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        switch (type) {
            case BARRIER -> {
                player.closeInventory();
                player.sendMessage(ChatColor.GRAY + "Zamknięto panel bankowy.");
            }
            case BOOK -> {
                HistorieGui.openFirstPage(player);
            }
        }
    }
}
