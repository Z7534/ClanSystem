package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import de.Z7534.clansystem.models.ClanWar;
import de.Z7534.clansystem.models.Permission;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClanWarsGUI extends AbstractGUI {

    private final Clan clan;
    private final ClanMember member;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public ClanWarsGUI(Clansystem plugin, Player player, Clan clan) {
        super(plugin, player, plugin.getMessageManager().getRaw("gui.wars"), 54);
        this.clan = clan;
        this.member = clan.getMember(player.getUniqueId());
    }

    @Override
    public void setup() {
        fillFancyBorder();

        boolean canManage = member.hasPermission(Permission.WAR_MANAGE);

        setItem(4, new ItemBuilder(Material.IRON_SWORD)
                .name("&cKriege")
                .lore(
                        "&7Aktive Kriege: &e" + clan.getActiveWars().size(),
                        "",
                        "&7Kriege bringen Punkte für",
                        "&7deinen Clan bei Kills!"
                )
                .hideFlags()
                .build());

        List<ClanWar> activeWars = plugin.getWarManager().getActiveWarsForClan(clan.getId());
        int slot = 10;

        for (ClanWar war : activeWars) {
            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 26) break;

            int opponentId = war.getOpponent(clan.getId());
            Clan opponent = plugin.getClanManager().getClan(opponentId);
            if (opponent == null) continue;

            boolean isAttacker = war.isAttacker(clan.getId());
            int ourPoints = isAttacker ? war.getAttackerPoints() : war.getDefenderPoints();
            int theirPoints = isAttacker ? war.getDefenderPoints() : war.getAttackerPoints();

            List<String> lore = new ArrayList<>();
            lore.add("&7Rolle: " + (isAttacker ? "&cAngreifer" : "&eVerteidiger"));
            lore.add("&7Gestartet: &e" + dateFormat.format(new Date(war.getStartedAt())));
            lore.add("");
            lore.add("&7Punkte:");
            lore.add("&a" + clan.getName() + ": &e" + ourPoints);
            lore.add("&c" + opponent.getName() + ": &e" + theirPoints);

            if (canManage) {
                lore.add("");
                lore.add("&cKlicke zum Kapitulieren");
            }

            final Clan finalOpponent = opponent;
            setItem(slot, new ItemBuilder(Material.IRON_SWORD)
                    .name("&c⚔ " + opponent.getName())
                    .lore(lore)
                    .glow()
                    .hideFlags()
                    .build(), () -> {
                if (canManage) {
                    new ConfirmGUI(plugin, player,
                            "Vor " + finalOpponent.getName() + " kapitulieren?",
                            () -> {
                                plugin.getWarManager().surrender(clan, finalOpponent);
                                plugin.getMessageManager().send(player, "war.surrendered",
                                        de.Z7534.clansystem.managers.MessageManager.of(
                                                "clan", clan.getColoredName(),
                                                "enemy", finalOpponent.getColoredName()));
                                new ClanWarsGUI(plugin, player, clan).open();
                            },
                            () -> new ClanWarsGUI(plugin, player, clan).open()
                    ).open();
                }
            });

            slot++;
        }

        for (int i = 27; i < 36; i++) {
            setItem(i, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(" ").build());
        }

        setItem(31, new ItemBuilder(Material.BOOK)
                .name("&eKriegshistorie")
                .lore("&7Zeigt vergangene Kriege an.")
                .build());

        List<ClanWar> history = plugin.getWarManager().getWarHistory(clan.getId());
        slot = 37;

        for (ClanWar war : history) {
            if (!war.isActive()) {
                while (slot % 9 == 0 || slot % 9 == 8) {
                    slot++;
                }
                if (slot >= 44) break;

                int opponentId = war.getOpponent(clan.getId());
                Clan opponent = plugin.getClanManager().getClan(opponentId);
                String opponentName = opponent != null ? opponent.getName() : "Gelöscht";

                boolean won = war.getWinnerClanId() != null && war.getWinnerClanId() == clan.getId();
                Material material = won ? Material.LIME_WOOL : Material.RED_WOOL;

                setItem(slot, new ItemBuilder(material)
                        .name((won ? "&a" : "&c") + opponentName)
                        .lore(
                                "&7Status: " + (won ? "&aSieg" : "&cNiederlage"),
                                "&7Datum: &e" + dateFormat.format(new Date(war.getEndedAt()))
                        )
                        .build());

                slot++;
            }
        }

        addBackButton(49, () -> new ClanMainGUI(plugin, player, clan).open());

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }
}
