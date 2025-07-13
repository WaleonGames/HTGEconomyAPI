package pl.htgmc.htgeconomyapi.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class HistorieGui {

    private static final String TITLE = ChatColor.DARK_GREEN + "Historia Transakcji";
    private static final int PAGE_SIZE = 28;
    private static final Map<UUID, Integer> currentPage = new HashMap<>();

    public static void open(Player player, int page) {
        currentPage.put(player.getUniqueId(), page);
        Inventory gui = Bukkit.createInventory(null, 54, TITLE + " #" + (page + 1));

        // Dodaj szare szyby dookoła GUI
        ItemStack glass = createSimpleItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        List<Integer> borderSlots = Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44,
                45, 46, 47, 48, 50, 51, 52, 53
        );
        for (int slot : borderSlots) gui.setItem(slot, glass);

        List<String> allLogs = readLogs();
        List<String> pageLogs = allLogs.stream()
                .skip((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());

        int slot = 10;
        for (String log : pageLogs) {
            gui.setItem(slot, createLogItem(log));
            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }

        if (page > 0) {
            gui.setItem(45, createSimpleItem(Material.ARROW, ChatColor.GRAY + "Poprzednia strona"));
        }

        if ((page + 1) * PAGE_SIZE < allLogs.size()) {
            gui.setItem(53, createSimpleItem(Material.ARROW, ChatColor.GRAY + "Następna strona"));
        }

        gui.setItem(49, createSimpleItem(Material.BARRIER, ChatColor.RED + "Zamknij"));

        player.openInventory(gui);
    }

    public static void openFirstPage(Player player) {
        open(player, 0);
    }

    public static void nextPage(Player player) {
        int page = currentPage.getOrDefault(player.getUniqueId(), 0);
        open(player, page + 1);
    }

    public static void previousPage(Player player) {
        int page = currentPage.getOrDefault(player.getUniqueId(), 0);
        if (page > 0) open(player, page - 1);
    }

    private static ItemStack createSimpleItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createLogItem(String line) {
        String type = "INNE";
        ChatColor color = ChatColor.GRAY;

        if (line.contains("[ADD]")) {
            type = "Dodano";
            color = ChatColor.GREEN;
        } else if (line.contains("[REMOVE]")) {
            type = "Wydano";
            color = ChatColor.RED;
        } else if (line.contains("[PUNISH]")) {
            type = "Kara";
            color = ChatColor.YELLOW;
        }

        String[] parts = line.split(" ");
        String time = parts.length > 1 ? parts[0] + " " + parts[1] : "Brak daty";
        String amount = Arrays.stream(parts)
                .filter(p -> p.startsWith("AMOUNT="))
                .findFirst()
                .orElse("AMOUNT=?")
                .replace("AMOUNT=", "");

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(color + type + " " + ChatColor.DARK_GRAY + "(" + amount + ")");
        meta.setLore(List.of(
                ChatColor.GRAY + "Data: " + time,
                ChatColor.DARK_GRAY + line
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static List<String> readLogs() {
        File logFile = new File("plugins/HTGEconomyAPI/logs/economy.log");
        if (!logFile.exists()) return List.of();

        try {
            List<String> lines = Files.readAllLines(logFile.toPath());
            Collections.reverse(lines);
            return lines;
        } catch (IOException e) {
            Bukkit.getLogger().warning("[EconomyGUI] Błąd odczytu economy.log: " + e.getMessage());
            return List.of();
        }
    }
}
