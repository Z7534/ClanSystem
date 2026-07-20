package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanRank;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ClanRankGUI extends AbstractGUI {

    private final Clan clan;

    public ClanRankGUI(Clansystem plugin, Player player, Clan clan) {
        super(plugin, player, plugin.getMessageManager().getRaw("gui.ranks"), 54);
        this.clan = clan;
    }

    @Override
    public void setup() {
        fillFancyBorder();

        List<ClanRank> ranks = clan.getSortedRanks();
        int slot = 10;

        for (ClanRank rank : ranks) {

            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 44) break;

            Material material;
            if (rank.getPriority() >= 100) {
                material = Material.DIAMOND;
            } else if (rank.getPriority() >= 50) {
                material = Material.GOLD_INGOT;
            } else {
                material = Material.IRON_INGOT;
            }

            int memberCount = (int) clan.getMembers().values().stream()
                    .filter(m -> m.getRankId() == rank.getId())
                    .count();

            final int rankId = rank.getId();
            setItem(slot, new ItemBuilder(material)
                    .name("&e" + rank.getName())
                    .lore(
                            "&7Priorität: &e" + rank.getPriority(),
                            "&7Mitglieder: &e" + memberCount,
                            "&7Berechtigungen: &e" + rank.getPermissions().size(),
                            "",
                            "&eLinksklick: Bearbeiten",
                            "&cRechtsklick: Löschen"
                    )
                    .build(), () -> {
                ClanRank clickedRank = clan.getRank(rankId);
                if (clickedRank != null) {
                    new ClanRankEditGUI(plugin, player, clan, clickedRank).open();
                }
            });

            slot++;
        }

        setItem(49, new ItemBuilder(Material.EMERALD)
                .name("&a&l➤ Neuen Rang erstellen")
                .lore(
                        "&7Erstelle einen neuen Rang",
                        "&7für deinen Clan.",
                        "",
                        "&a▶ &7Klicke zum Erstellen"
                )
                .build(), () -> plugin.getChatInputManager().request(player,
                "Bitte gib den Namen für den neuen Rang im Chat ein:",
                input -> {
                    player.performCommand("clan rank create " + input.trim().replace(" ", "_"));
                    new ClanRankGUI(plugin, player, clan).open();
                }));

        addBackButton(45, () -> new ClanSettingsGUI(plugin, player, clan).open());

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }
}
