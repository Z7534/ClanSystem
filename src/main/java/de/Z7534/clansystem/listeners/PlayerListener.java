package de.Z7534.clansystem.listeners;

import de.Z7534.clansystem.Clansystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final Clansystem plugin;

    public PlayerListener(Clansystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getDatabaseManager().clearExpiredCooldowns();

        plugin.getGlowManager().applyToPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ChatListener.clearToggles(player.getUniqueId());
        plugin.getChatInputManager().cancel(player.getUniqueId());

        de.Z7534.clansystem.gui.AbstractGUI.removeOpenGui(player);
        de.Z7534.clansystem.gui.ClanChestGUI.removeOpenChest(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        plugin.getGlowManager().applyToPlayer(player);
    }
}
