package de.Z7534.clansystem.managers;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GlowManager {

    private final Clansystem plugin;
    private final boolean packetFilteringAvailable;

    public GlowManager(Clansystem plugin) {
        this.plugin = plugin;

        boolean protocolLibPresent = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null
                && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");

        boolean hookRegistered = false;
        if (protocolLibPresent) {
            try {
                new GlowPacketHook(plugin);
                hookRegistered = true;
                plugin.getLogger().info("ProtocolLib gefunden - Clan-Glow ist nur fuer das eigene Team sichtbar.");
            } catch (Throwable t) {
                plugin.getLogger().severe("Konnte den ProtocolLib-Hook fuer teambasierten Glow nicht laden: " + t);
            }
        } else {
            plugin.getLogger().warning("ProtocolLib wurde NICHT gefunden (Bukkit.getPluginManager().getPlugin(\"ProtocolLib\") == null " +
                    "oder nicht aktiviert). Der Clan-Glow-Effekt bleibt deaktiviert, da er sonst fuer ALLE Spieler " +
                    "sichtbar waere. Pruefe: 1) Liegt ProtocolLib.jar im plugins-Ordner? 2) Steht in der Konsole beim " +
                    "Start eine Fehlermeldung von ProtocolLib? 3) Ist die ProtocolLib-Version mit dieser Server-Version kompatibel?");
        }
        this.packetFilteringAvailable = hookRegistered;

        Bukkit.getScheduler().runTaskTimer(plugin, this::enforceAll, 100L, 100L);
    }

    public boolean isPacketFilteringAvailable() {
        return packetFilteringAvailable;
    }

    public void setupAll() {
        for (Clan clan : plugin.getClanManager().getAllClans()) {
            refresh(clan);
        }
    }

    private void enforceAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Clan clan = plugin.getClanManager().getClanByPlayer(player);
            boolean shouldGlow = clan != null && clan.isGlowEnabled() && packetFilteringAvailable;
            if (player.isGlowing() != shouldGlow) {
                player.setGlowing(shouldGlow);
            }
        }
    }

    public void refresh(Clan clan) {

        boolean shouldGlow = clan.isGlowEnabled() && packetFilteringAvailable;

        for (ClanMember member : clan.getMembers().values()) {
            Player player = member.getPlayer();
            if (player != null) {
                player.setGlowing(shouldGlow);
            }
        }
    }

    public void applyToPlayer(Player player) {
        Clan clan = plugin.getClanManager().getClanByPlayer(player);
        boolean shouldGlow = clan != null && clan.isGlowEnabled() && packetFilteringAvailable;
        player.setGlowing(shouldGlow);
    }

    public void removeFromPlayer(Player player) {
        player.setGlowing(false);
    }

    public void disband(Clan clan) {
        for (ClanMember member : clan.getMembers().values()) {
            Player player = member.getPlayer();
            if (player != null) {
                removeFromPlayer(player);
            }
        }
    }

    public void refreshVisibilityForNewMember(Clan clan, Player joined) {
        if (!clan.isGlowEnabled() || !packetFilteringAvailable) {
            return;
        }
        for (ClanMember member : clan.getMembers().values()) {
            Player mate = member.getPlayer();
            if (mate == null || mate.getUniqueId().equals(joined.getUniqueId())) {
                continue;
            }
            resyncPair(joined, mate);
        }
    }

    public void refreshVisibilityForLeavingMember(Clan formerClan, Player left) {
        if (!formerClan.isGlowEnabled() || !packetFilteringAvailable) {
            return;
        }
        for (ClanMember member : formerClan.getMembers().values()) {
            Player mate = member.getPlayer();
            if (mate == null || mate.getUniqueId().equals(left.getUniqueId())) {
                continue;
            }
            resyncPair(left, mate);
        }
    }

    private void resyncPair(Player a, Player b) {
        if (!a.isOnline() || !b.isOnline()) {
            return;
        }
        a.hidePlayer(plugin, b);
        a.showPlayer(plugin, b);
        b.hidePlayer(plugin, a);
        b.showPlayer(plugin, a);
    }
}
