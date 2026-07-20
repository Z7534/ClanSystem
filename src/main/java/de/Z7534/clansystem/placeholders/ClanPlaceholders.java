package de.Z7534.clansystem.placeholders;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.managers.LevelManager;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ClanPlaceholders extends PlaceholderExpansion {

    private final Clansystem plugin;

    public ClanPlaceholders(Clansystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clansystem";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Z7534";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) {
            return "";
        }

        Clan clan = plugin.getClanManager().getClanByPlayer(offlinePlayer.getUniqueId());

        switch (params.toLowerCase()) {
            case "clan_name" -> {
                return clan != null ? clan.getName() : "";
            }
            case "clan_suffix" -> {
                return clan != null ? clan.getColoredSuffix() : "";
            }
            case "clan_level" -> {
                return clan != null ? String.valueOf(clan.getLevel()) : "0";
            }
            case "clan_points" -> {
                return clan != null ? String.valueOf(clan.getPoints()) : "0";
            }
            case "clan_rank" -> {
                if (clan == null) {
                    return "";
                }
                ClanMember member = clan.getMember(offlinePlayer.getUniqueId());
                return member != null ? member.getDisplayRankName() : "";
            }
            case "clan_membercount" -> {
                return clan != null ? String.valueOf(clan.getMemberCount()) : "0";
            }
            case "clan_maxmembers" -> {
                if (clan == null) {
                    return "0";
                }
                LevelManager levelManager = plugin.getLevelManager();
                return String.valueOf(levelManager.getMaxMembers(clan.getLevel()));
            }
            default -> {
                return null;
            }
        }
    }
}
