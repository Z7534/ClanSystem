package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import de.Z7534.clansystem.models.Permission;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanSettingsGUI extends AbstractGUI {

    private final Clan clan;
    private final ClanMember member;

    public ClanSettingsGUI(Clansystem plugin, Player player, Clan clan) {
        super(plugin, player, plugin.getMessageManager().getRaw("gui.settings"), 45);
        this.clan = clan;
        this.member = clan.getMember(player.getUniqueId());
    }

    @Override
    public void setup() {
        fillFancyBorder();

        if (member.hasPermission(Permission.SETTINGS)) {
            Material joinMaterial;
            String joinTypeName;
            String joinTypeDesc;

            switch (clan.getJoinType()) {
                case OPEN:
                    joinMaterial = Material.OAK_DOOR;
                    joinTypeName = "&aOffen";
                    joinTypeDesc = "&7Jeder kann beitreten.";
                    break;
                case APPLY:
                    joinMaterial = Material.WRITABLE_BOOK;
                    joinTypeName = "&eBewerbung";
                    joinTypeDesc = "&7Spieler müssen sich bewerben.";
                    break;
                default:
                    joinMaterial = Material.IRON_DOOR;
                    joinTypeName = "&cNur Einladung";
                    joinTypeDesc = "&7Spieler müssen eingeladen werden.";
                    break;
            }

            setItem(11, new ItemBuilder(joinMaterial)
                    .name("&6Beitrittsart: " + joinTypeName)
                    .lore(
                            joinTypeDesc,
                            "",
                            "&eKlicke zum Ändern"
                    )
                    .build(), () -> {
                Clan.JoinType[] types = Clan.JoinType.values();
                int currentIndex = clan.getJoinType().ordinal();
                int nextIndex = (currentIndex + 1) % types.length;
                clan.setJoinType(types[nextIndex]);
                plugin.getClanManager().updateClanInDatabase(clan);
                refresh();
                player.updateInventory();
            });
        }

        if (member.hasPermission(Permission.SETTINGS)) {
            setItem(4, new ItemBuilder(Material.WHITE_DYE)
                    .name("&6&l➤ Clan-Farbe &8(" + clan.getNameColor() + clan.getName() + "&r&8)")
                    .lore(
                            "&7Färbt deinen Clan-Namen in",
                            "&7Nachrichten und deinen",
                            "&7Clan-Suffix ein.",
                            "",
                            "&a▶ &7Klicke zum Ändern"
                    )
                    .build(), () -> new ClanColorGUI(plugin, player, clan).open());
        }

        if (member.hasPermission(Permission.SET_SUFFIX)) {
            setItem(13, new ItemBuilder(Material.NAME_TAG)
                    .name("&6&l➤ Clan-Suffix")
                    .lore(
                            "&7Aktuell: &r" + (clan.getSuffix().isEmpty() ? "&8Keiner" : clan.getColoredSuffix()),
                            "",
                            "&a▶ &7Klicke zum Bearbeiten"
                    )
                    .build(), () -> new ClanSuffixGUI(plugin, player, clan).open());
        }

        if (member.hasPermission(Permission.SETTINGS)) {
            setItem(20, new ItemBuilder(de.Z7534.clansystem.utils.ClanIcons.fromName(clan.getIcon()))
                    .name("&b&l➤ Clan-Icon")
                    .lore(
                            "&7Wähle ein Icon, das den",
                            "&7Clan in Menüs repräsentiert.",
                            "",
                            "&a▶ &7Klicke zum Ändern"
                    )
                    .build(), () -> new ClanIconGUI(plugin, player, clan).open());
        }

        if (member.hasPermission(Permission.RANK_MANAGE)) {
            setItem(15, new ItemBuilder(Material.EMERALD)
                    .name("&aRänge verwalten")
                    .lore(
                            "&7Erstelle, bearbeite und",
                            "&7lösche Clan-Ränge.",
                            "",
                            "&eKlicke zum Öffnen"
                    )
                    .build(), () -> new ClanRankGUI(plugin, player, clan).open());
        }

        if (clan.getJoinType() == Clan.JoinType.APPLY && member.hasPermission(Permission.INVITE)) {
            int applicationCount = clan.getPendingApplications().size();
            setItem(19, new ItemBuilder(Material.PAPER)
                    .name("&eBewerbungen")
                    .lore(
                            "&7Ausstehende Bewerbungen: &e" + applicationCount,
                            "",
                            "&eKlicke zum Öffnen"
                    )
                    .glow(applicationCount > 0)
                    .build(), () -> new ClanApplicationsGUI(plugin, player, clan).open());
        }

        if (member.hasPermission(Permission.SETTINGS)) {
            int visibleWarps = (int) clan.getWarps().values().stream()
                    .filter(w -> w.isVisibleToAllies())
                    .count();

            setItem(22, new ItemBuilder(Material.ENDER_EYE)
                    .name("&5Warp-Sichtbarkeit")
                    .lore(
                            "&7Warps für Allianzen sichtbar:",
                            "&e" + visibleWarps + "&8/&e" + clan.getWarpCount(),
                            "",
                            "&7Bearbeite einzelne Warps",
                            "&7in den Warp-Einstellungen."
                    )
                    .build());
        }

        if (member.hasPermission(Permission.SETTINGS)) {
            setItem(24, new ItemBuilder(clan.isGlowEnabled() ? Material.GLOWSTONE_DUST : Material.GUNPOWDER)
                    .name("&d&l➤ Glow-Effekt")
                    .lore(
                            "&7Status: " + (clan.isGlowEnabled() ? "&aAktiviert" : "&cDeaktiviert"),
                            "&7Lässt Mitglieder durch",
                            "&7Wände hindurch leuchten.",
                            "",
                            "&a▶ &7Klicke zum Bearbeiten"
                    )
                    .glow(clan.isGlowEnabled())
                    .build(), () -> new ClanGlowGUI(plugin, player, clan).open());
        }

        if (member.hasPermission(Permission.DISBAND) && clan.isLeader(player.getUniqueId())) {
            setItem(31, new ItemBuilder(Material.TNT)
                    .name("&c&lClan auflösen")
                    .lore(
                            "&c&lACHTUNG!",
                            "&7Dies kann nicht rückgängig",
                            "&7gemacht werden!",
                            "",
                            "&cKlicke zum Auflösen"
                    )
                    .build(), () -> new ConfirmGUI(plugin, player,
                    "Clan auflösen?",
                    () -> {

                        if (plugin.getConfigManager().isBroadcastClanDisbanded()) {
                            plugin.getMessageManager().broadcastExcept("broadcast.clan-disbanded", player,
                                    de.Z7534.clansystem.managers.MessageManager.of("clan", clan.getColoredName()));
                        }

                        plugin.getClanManager().disbandClan(clan);
                        plugin.getMessageManager().send(player, "clan.disbanded",
                                de.Z7534.clansystem.managers.MessageManager.of("clan", clan.getColoredName()));
                        player.closeInventory();
                    },
                    () -> new ClanSettingsGUI(plugin, player, clan).open()
            ).open());
        }

        if (!clan.isLeader(player.getUniqueId())) {
            setItem(33, new ItemBuilder(Material.IRON_DOOR)
                    .name("&c&lClan verlassen")
                    .lore(
                            "&7Du verlässt den Clan &e" + clan.getName() + "&7.",
                            "&7Danach gilt ein kurzer",
                            "&7Cooldown, bevor du einen",
                            "&7neuen Clan gründen kannst.",
                            "",
                            "&cKlicke zum Verlassen"
                    )
                    .build(), () -> new ConfirmGUI(plugin, player,
                    "Clan wirklich verlassen?",
                    () -> {
                        player.closeInventory();
                        player.performCommand("clan leave");
                    },
                    () -> new ClanSettingsGUI(plugin, player, clan).open()
            ).open());
        }

        addBackButton(40, () -> new ClanMainGUI(plugin, player, clan).open());

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }
}
