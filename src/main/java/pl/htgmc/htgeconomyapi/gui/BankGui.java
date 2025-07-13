package pl.htgmc.htgeconomyapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.htgmc.htgeconomyapi.analysis.MultiEconomyAnalyzer;
import pl.htgmc.htgeconomyapi.analysis.WealthAnalyzer;
import pl.htgmc.htgeconomyapi.data.CoinStorage;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BankGui {

    private static final String TITLE = ChatColor.DARK_GREEN + "Twój Bank";

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, TITLE);
        UUID uuid = player.getUniqueId();

        String formattedCoins = CoinStorage.getFormattedCoins(uuid);
        double vault = MultiEconomyAnalyzer.getVaultBalance(uuid);
        double multiplier = WealthAnalyzer.getCombinedDynamicMultiplier();

        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        // Tylko wokół (góra, dół, lewa, prawa)
        for (int i = 0; i < 9; i++) gui.setItem(i, blackGlass);             // góra
        for (int i = 45; i < 54; i++) gui.setItem(i, blackGlass);          // dół
        for (int i = 0; i < 54; i += 9) gui.setItem(i, blackGlass);        // lewa
        for (int i = 8; i < 54; i += 9) gui.setItem(i, blackGlass);        // prawa

        // Środkowe przyciski
        gui.setItem(20, createItem(Material.SUNFLOWER, "&eSaldo Coins", "&7" + formattedCoins));
        gui.setItem(22, createItem(Material.GOLD_INGOT, "&6Saldo Vault", "&7" + String.format("%.2f $", vault)));
        gui.setItem(24, createItem(Material.EXPERIENCE_BOTTLE, "&bMnożnik globalny", "&7x" + String.format("%.3f", multiplier)));

        // Dolny rząd – Historia i Zamknij na tym samym poziomie
        gui.setItem(48, createItem(Material.BOOK, "&9Historia", "&7Kliknij, aby zobaczyć historię"));
        gui.setItem(50, createItem(Material.BARRIER, "&cZamknij", ""));

        player.openInventory(gui);
    }

    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.setLore(Arrays.stream(lore).map(s -> ChatColor.translateAlternateColorCodes('&', s)).toList());
            item.setItemMeta(meta);
        }
        return item;
    }
}
