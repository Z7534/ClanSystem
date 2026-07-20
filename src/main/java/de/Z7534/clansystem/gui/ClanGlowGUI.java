package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ClanGlowGUI extends AbstractGUI {

    private final Clan clan;

    public ClanGlowGUI(Clansystem plugin, Player player, Clan clan) {
        super(plugin, player, "&8&l✦ &dGlow-Einstellungen", 27);
        this.clan = clan;
    }

    @Override
    public void setup() {
        fillFancyBorder();

        boolean enabled = clan.isGlowEnabled();
        boolean available = plugin.getGlowManager().isPacketFilteringAvailable();

        setItem(13, new ItemBuilder(enabled ? Material.GLOWSTONE_DUST : Material.GUNPOWDER)
                .name(enabled ? "&a&l✔ Glow aktiviert" : "&c&l✘ Glow deaktiviert")
                .lore(available ? List.of(
                        "&7Lässt alle Clan-Mitglieder",
                        "&7durch Wände hindurch leuchten.",
                        "&7Nur dein eigenes Team sieht das!",
                        "&7Namen bleiben dabei normal.",
                        "",
                        "&a▶ &7Klicke zum Umschalten"
                ) : List.of(
                        "&7Lässt alle Clan-Mitglieder",
                        "&7durch Wände hindurch leuchten.",
                        "",
                        "&c✘ Auf diesem Server nicht verfügbar",
                        "&c(ProtocolLib fehlt)"
                ))
                .glow(enabled)
                .build(), () -> {
            if (!available) {
                plugin.getMessageManager().send(player, "glow.protocollib-missing");
                return;
            }
            clan.setGlowEnabled(!clan.isGlowEnabled());
            plugin.getClanManager().updateClanInDatabase(clan);
            plugin.getGlowManager().refresh(clan);
            refresh();
            player.updateInventory();
        });

        addBackButton(22, () -> new ClanSettingsGUI(plugin, player, clan).open());

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }
}
