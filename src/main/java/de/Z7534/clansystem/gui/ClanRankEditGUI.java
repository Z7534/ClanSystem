package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanRank;
import de.Z7534.clansystem.models.Permission;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanRankEditGUI extends AbstractGUI {

    private final Clan clan;
    private final ClanRank rank;

    public ClanRankEditGUI(Clansystem plugin, Player player, Clan clan, ClanRank rank) {
        super(plugin, player,
                plugin.getMessageManager().getRawWithPlaceholders("gui.rank-edit",
                        de.Z7534.clansystem.managers.MessageManager.of("rank", rank.getName())), 54);
        this.clan = clan;
        this.rank = rank;
    }

    @Override
    public void setup() {
        fillFancyBorder();

        setItem(4, new ItemBuilder(Material.NAME_TAG)
                .name("&6&l✦ " + rank.getName())
                .lore(
                        "&7Priorität: &e" + rank.getPriority(),
                        "",
                        "&a▶ &7Klicke, um den Namen zu ändern"
                )
                .glow()
                .build(), () -> {
            String oldName = rank.getName();
            plugin.getChatInputManager().request(player,
                    "Bitte gib den neuen Namen für den Rang '" + oldName + "' im Chat ein:",
                    input -> {
                        player.performCommand("clan rank rename " + oldName.replace(" ", "_") + " " + input.trim().replace(" ", "_"));
                        ClanRank updatedRank = clan.getRankByName(input.trim().replace(" ", "_"));
                        new ClanRankEditGUI(plugin, player, clan, updatedRank != null ? updatedRank : rank).open();
                    });
        });

        int slot = 10;
        for (Permission perm : Permission.values()) {

            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 44) break;

            boolean hasPerm = rank.hasPermission(perm);
            Material material = hasPerm ? Material.LIME_DYE : Material.GRAY_DYE;

            final Permission permission = perm;
            setItem(slot, new ItemBuilder(material)
                    .name((hasPerm ? "&a" : "&c") + perm.getDisplayName())
                    .lore(
                            "&7Status: " + (hasPerm ? "&aAktiv" : "&cInaktiv"),
                            "",
                            "&eKlicke zum Umschalten"
                    )
                    .build(), () -> {
                rank.togglePermission(permission);
                plugin.getDatabaseManager().updateRank(rank);
                refresh();
                player.updateInventory();
            });

            slot++;
        }

        setItem(47, new ItemBuilder(Material.ARROW)
                .name("&aPriorität erhöhen")
                .lore("&7Aktuelle Priorität: &e" + rank.getPriority())
                .build(), () -> {
            rank.setPriority(rank.getPriority() + 10);
            plugin.getDatabaseManager().updateRank(rank);
            refresh();
            player.updateInventory();
        });

        setItem(51, new ItemBuilder(Material.ARROW)
                .name("&cPriorität verringern")
                .lore("&7Aktuelle Priorität: &e" + rank.getPriority())
                .build(), () -> {
            if (rank.getPriority() > 0) {
                rank.setPriority(rank.getPriority() - 10);
                plugin.getDatabaseManager().updateRank(rank);
                refresh();
                player.updateInventory();
            }
        });

        addBackButton(49, () -> new ClanRankGUI(plugin, player, clan).open());

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }
}
