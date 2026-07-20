package de.Z7534.clansystem.commands;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.gui.ClanChestGUI;
import de.Z7534.clansystem.managers.MessageManager;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import de.Z7534.clansystem.models.ClanRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ClanAdminCommand implements CommandExecutor, TabCompleter {

    private final Clansystem plugin;

    public ClanAdminCommand(Clansystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("clansystem.admin")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload" -> handleReload(sender);
            case "delete" -> handleDelete(sender, args);
            case "setlevel" -> handleSetLevel(sender, args);
            case "setleader" -> handleSetLeader(sender, args);
            case "chest" -> handleChest(sender, args);
            case "addpoints" -> handleAddPoints(sender, args);
            case "info" -> handleInfo(sender, args);
            case "forcejoin" -> handleForceJoin(sender, args);
            case "forceleave" -> handleForceLeave(sender, args);
            case "list" -> handleList(sender);
            case "help", "?" -> sendHelp(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("clansystem.admin.reload")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        plugin.reload();
        plugin.getMessageManager().send(sender, "general.reload-success");
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.delete")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin delete <clanname>"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[1]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[1]));
            return;
        }

        String clanName = clan.getColoredName();

        if (plugin.getConfigManager().isBroadcastClanDisbanded()) {
            plugin.getMessageManager().broadcast("broadcast.clan-disbanded",
                    MessageManager.of("clan", clanName));
        }

        plugin.getClanManager().disbandClan(clan);
        plugin.getMessageManager().send(sender, "admin.clan-deleted",
                MessageManager.of("clan", clanName));
    }

    private void handleSetLevel(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.setlevel")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin setlevel <clanname> <level>"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[1]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[1]));
            return;
        }

        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin setlevel <clanname> <level>"));
            return;
        }

        if (level < 1 || level > plugin.getLevelManager().getMaxLevel()) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin setlevel <clanname> <1-" + plugin.getLevelManager().getMaxLevel() + ">"));
            return;
        }

        clan.setLevel(level);

        int newChestSize = plugin.getLevelManager().getChestSize(level);
        if (clan.getChestSize() < newChestSize) {
            clan.resizeChest(newChestSize);
        }

        plugin.getClanManager().updateClanInDatabase(clan);

        plugin.getMessageManager().send(sender, "admin.level-set",
                MessageManager.of("clan", clan.getColoredName(), "level", String.valueOf(level)));
    }

    private void handleSetLeader(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.setleader")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin setleader <clanname> <spieler>"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[1]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[1]));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        UUID targetUuid;
        String targetName;

        if (target != null) {
            targetUuid = target.getUniqueId();
            targetName = target.getName();
        } else {
            targetUuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
            targetName = args[2];
        }

        ClanMember member = clan.getMember(targetUuid);
        if (member == null) {
            plugin.getMessageManager().send(sender, "clan.target-not-in-your-clan",
                    MessageManager.of("player", targetName));
            return;
        }

        boolean success = plugin.getClanManager().transferLeadership(clan, targetUuid);
        if (!success) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        plugin.getMessageManager().send(sender, "admin.leader-set",
                MessageManager.of("player", targetName, "clan", clan.getColoredName()));

        if (target != null) {
            plugin.getMessageManager().send(target, "rank.rank-changed",
                    MessageManager.of("rank", "Leader"));
        }
    }

    private void handleChest(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.chest")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().sendRaw(sender, "general.player-only");
            return;
        }

        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin chest <clanname>"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[1]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[1]));
            return;
        }

        new ClanChestGUI(plugin, player, clan).open();
        plugin.getMessageManager().send(player, "admin.chest-opened",
                MessageManager.of("clan", clan.getColoredName()));
    }

    private void handleAddPoints(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.addpoints")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin addpoints <clanname> <punkte>"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[1]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[1]));
            return;
        }

        int points;
        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin addpoints <clanname> <punkte>"));
            return;
        }

        plugin.getClanManager().addPointsToClan(clan, points);

        plugin.getMessageManager().send(sender, "admin.points-added",
                MessageManager.of("points", String.valueOf(points), "clan", clan.getColoredName()));
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin info <clanname>"));
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[1]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[1]));
            return;
        }

        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&8&m----------&8[ &6Clan-Info: " + clan.getColoredName() + " &8]&m----------"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7ID: &e" + clan.getId()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Name: " + clan.getColoredName()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Suffix: &r" + (clan.getSuffix().isEmpty() ? "&8Keiner" : clan.getColoredSuffix())));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Level: &e" + clan.getLevel()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Punkte: &e" + clan.getPoints()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Mitglieder: &e" + clan.getMemberCount()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Online: &a" + clan.getOnlineMembers().size()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Beitrittsart: &e" + clan.getJoinType().name()));

        ClanMember leader = clan.getMember(clan.getLeaderUuid());
        String leaderName = leader != null ? leader.getName() : "Unbekannt";
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Leader: &e" + leaderName));

        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Allianzen: &e" + clan.getAllies().size()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Aktive Kriege: &c" + clan.getActiveWars().size()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Homes: &e" + clan.getHomeCount()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Warps: &e" + clan.getWarpCount()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&7Ränge: &e" + clan.getRanks().size()));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&8&m-----------------------------------------"));
    }

    private void handleForceJoin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.bypass")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin forcejoin <spieler> <clanname>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageManager().send(sender, "general.player-not-found",
                    MessageManager.of("player", args[1]));
            return;
        }

        if (plugin.getClanManager().isInClan(target)) {
            plugin.getMessageManager().send(sender, "clan.target-already-in-clan");
            return;
        }

        Clan clan = plugin.getClanManager().getClan(args[2]);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-found",
                    MessageManager.of("clan", args[2]));
            return;
        }

        ClanRank lowestRank = clan.getLowestRank();
        plugin.getClanManager().addMember(clan, target, lowestRank);

        plugin.getMessageManager().send(target, "clan.joined",
                MessageManager.of("clan", clan.getColoredName()));

        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize(
                "&a" + target.getName() + " wurde dem Clan " + clan.getColoredName() + " hinzugefügt."));
    }

    private void handleForceLeave(CommandSender sender, String[] args) {
        if (!sender.hasPermission("clansystem.admin.bypass")) {
            plugin.getMessageManager().send(sender, "general.no-permission");
            return;
        }

        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "general.invalid-usage",
                    MessageManager.of("usage", "/clanadmin forceleave <spieler>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        UUID targetUuid;
        String targetName;

        if (target != null) {
            targetUuid = target.getUniqueId();
            targetName = target.getName();
        } else {
            targetUuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
            targetName = args[1];
        }

        Clan clan = plugin.getClanManager().getClanByPlayer(targetUuid);
        if (clan == null) {
            plugin.getMessageManager().send(sender, "clan.not-in-clan");
            return;
        }

        String clanName = clan.getColoredName();
        plugin.getClanManager().removeMember(clan, targetUuid);

        if (target != null) {
            plugin.getMessageManager().send(target, "clan.left",
                    MessageManager.of("clan", clanName));
        }

        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize(
                "&a" + targetName + " wurde aus dem Clan " + clanName + " entfernt."));
    }

    private void handleList(CommandSender sender) {
        Collection<Clan> clans = plugin.getClanManager().getAllClans();

        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&8&m----------&8[ &6Alle Clans (" + clans.size() + ") &8]&m----------"));

        for (Clan clan : clans) {
            sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize(
                    "&8- " + clan.getColoredName() + " &8| &7Level: &e" + clan.getLevel() +
                            " &8| &7Mitglieder: &e" + clan.getMemberCount() +
                            " &8| &7Punkte: &e" + clan.getPoints()));
        }

        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&8&m-----------------------------------------"));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&8&m----------&8[ &6Clan-Admin &8]&m----------"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin reload &8- &7Konfiguration neu laden"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin delete <clan> &8- &7Clan löschen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin setlevel <clan> <level> &8- &7Level setzen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin setleader <clan> <spieler> &8- &7Leader setzen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin chest <clan> &8- &7Truhe öffnen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin addpoints <clan> <punkte> &8- &7Punkte hinzufügen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin info <clan> &8- &7Clan-Info anzeigen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin forcejoin <spieler> <clan> &8- &7Spieler in Clan zwingen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin forceleave <spieler> &8- &7Spieler aus Clan entfernen"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&e/clanadmin list &8- &7Alle Clans auflisten"));
        sender.sendMessage(de.Z7534.clansystem.utils.ColorUtils.colorize("&8&m---------------------------------"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("clansystem.admin")) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList(
                    "reload", "delete", "setlevel", "setleader", "chest", "addpoints",
                    "info", "forcejoin", "forceleave", "list", "help"
            );
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                    .filter(s -> s.startsWith(input))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            switch (subCommand) {
                case "delete", "setlevel", "setleader", "chest", "addpoints", "info" -> {
                    completions = plugin.getClanManager().getAllClans().stream()
                            .map(Clan::getName)
                            .filter(n -> n.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                }
                case "forcejoin", "forceleave" -> {

                    completions = Arrays.stream(Bukkit.getOfflinePlayers())
                            .map(OfflinePlayer::getName)
                            .filter(java.util.Objects::nonNull)
                            .filter(n -> n.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                }
            }
        } else if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            String input = args[2].toLowerCase();

            switch (subCommand) {
                case "setlevel" -> {
                    for (int i = 1; i <= plugin.getLevelManager().getMaxLevel(); i++) {
                        String level = String.valueOf(i);
                        if (level.startsWith(input)) {
                            completions.add(level);
                        }
                    }
                }
                case "setleader" -> {
                    Clan clan = plugin.getClanManager().getClan(args[1]);
                    if (clan != null) {
                        completions = clan.getMembers().values().stream()
                                .map(ClanMember::getName)
                                .filter(n -> n.toLowerCase().startsWith(input))
                                .collect(Collectors.toList());
                    }
                }
                case "forcejoin" -> {
                    completions = plugin.getClanManager().getAllClans().stream()
                            .map(Clan::getName)
                            .filter(n -> n.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                }
            }
        }

        return completions;
    }
}
