package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import de.Z7534.clansystem.models.Permission;
import de.Z7534.clansystem.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanChestGUI implements InventoryHolder {

    private static final Map<UUID, ClanChestGUI> openChests = new HashMap<>();

    private final Clansystem plugin;
    private final Player player;
    private final Clan clan;
    private final ClanMember member;
    private final Inventory inventory;
    private final boolean canWithdraw;
    private ItemStack[] originalContents;

    public ClanChestGUI(Clansystem plugin, Player player, Clan clan) {
        this.plugin = plugin;
        this.player = player;
        this.clan = clan;
        this.member = clan.getMember(player.getUniqueId());
        this.canWithdraw = member.hasPermission(Permission.CHEST_WITHDRAW);

        String title = plugin.getMessageManager().getRaw("gui.chest");
        this.inventory = Bukkit.createInventory(this, clan.getChestSize(), ColorUtils.colorize(title));
    }

    public void open() {

        ItemStack[] contents = clan.getChestContents();
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.length, inventory.getSize()); i++) {
                inventory.setItem(i, contents[i]);
            }
        }

        ItemStack[] liveContents = inventory.getContents();
        this.originalContents = new ItemStack[liveContents.length];
        for (int i = 0; i < liveContents.length; i++) {
            this.originalContents[i] = liveContents[i] != null ? liveContents[i].clone() : null;
        }

        player.openInventory(inventory);
        openChests.put(player.getUniqueId(), this);
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();

        if (slot >= 0 && slot < inventory.getSize()) {
            ItemStack clickedItem = inventory.getItem(slot);

            if (clickedItem != null && !clickedItem.getType().isAir()) {
                if (!canWithdraw) {
                    event.setCancelled(true);
                    plugin.getMessageManager().send(player, "chest.no-withdraw-access");
                    return;
                }

                if (plugin.getConfigManager().isChestLogging()) {
                    plugin.getDatabaseManager().logChestAction(clan.getId(), player.getUniqueId(),
                            "WITHDRAW", clickedItem.clone());
                }
            }
        }

    }

    public void handleClose(InventoryCloseEvent event) {

        ItemStack[] contents = new ItemStack[inventory.getSize()];
        for (int i = 0; i < inventory.getSize(); i++) {
            contents[i] = inventory.getItem(i);
        }
        clan.setChestContents(contents);
        plugin.getClanManager().saveClanChest(clan);

        long itemsBefore = countItems(originalContents);
        long itemsAfter = countItems(contents);

        if (itemsAfter > itemsBefore) {
            clan.broadcastMessage(plugin.getMessageManager().get("chest.deposit",
                    de.Z7534.clansystem.managers.MessageManager.of("player", player.getName())));
        } else if (itemsAfter < itemsBefore) {
            clan.broadcastMessage(plugin.getMessageManager().get("chest.withdraw",
                    de.Z7534.clansystem.managers.MessageManager.of("player", player.getName())));
        }

        openChests.remove(player.getUniqueId());
    }

    private long countItems(ItemStack[] items) {
        long total = 0;
        if (items == null) {
            return 0;
        }
        for (ItemStack item : items) {
            if (item != null && !item.getType().isAir()) {
                total += item.getAmount();
            }
        }
        return total;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Clan getClan() {
        return clan;
    }

    public Player getPlayer() {
        return player;
    }

    public static ClanChestGUI getOpenChest(Player player) {
        return openChests.get(player.getUniqueId());
    }

    public static boolean hasOpenChest(Player player) {
        return openChests.containsKey(player.getUniqueId());
    }

    public static void removeOpenChest(Player player) {
        openChests.remove(player.getUniqueId());
    }
}
