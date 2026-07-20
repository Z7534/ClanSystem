package de.Z7534.clansystem.managers;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanRank;
import de.Z7534.clansystem.models.Permission;
import de.Z7534.clansystem.storage.StorageType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigManager {

    private final Clansystem plugin;
    private FileConfiguration config;

    private StorageType storageType;
    private String dbFile;
    private String dbTablePrefix;
    private String mariaDbHost;
    private int mariaDbPort;
    private String mariaDbDatabase;
    private String mariaDbUsername;
    private String mariaDbPassword;
    private boolean mariaDbUseSsl;
    private int mariaDbPoolSize;

    private int clanNameMinLength;
    private int clanNameMaxLength;
    private Pattern clanNamePattern;
    private int leaveCooldown;
    private Clan.JoinType defaultJoinType;

    private int suffixMaxLength;
    private boolean allowUnderline;
    private boolean allowStrikethrough;
    private boolean allowMagic;
    private List<String> suffixBlacklist;

    private int teleportWarmup;
    private int teleportCooldown;
    private boolean cancelOnMove;
    private boolean cancelOnDamage;

    private int warKillPoints;
    private int normalKillPoints;
    private boolean countNormalKills;
    private boolean disableFriendlyFire;
    private boolean disableAllyFire;

    private boolean broadcastClanCreated;
    private boolean broadcastClanDisbanded;
    private boolean broadcastMemberJoined;
    private boolean broadcastMemberLeft;

    private List<ClanRank> defaultRanks;

    private boolean chestLogging;

    public ConfigManager(Clansystem plugin) {
        this.plugin = plugin;
        this.defaultRanks = new ArrayList<>();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadDatabaseSettings();
        loadClanSettings();
        loadSuffixSettings();
        loadTeleportSettings();
        loadPvPSettings();
        loadBroadcastSettings();
        loadDefaultRanks();
        chestLogging = config.getBoolean("chest-logging", true);
    }

    private void loadDatabaseSettings() {
        ConfigurationSection db = config.getConfigurationSection("database");
        if (db != null) {
            storageType = parseStorageType(db.getString("type", "sqlite"));
            dbFile = db.getString("file", "clans.db");
            dbTablePrefix = db.getString("table-prefix", "clan_");

            ConfigurationSection mariadb = db.getConfigurationSection("mariadb");
            if (mariadb != null) {
                mariaDbHost = mariadb.getString("host", "localhost");
                mariaDbPort = mariadb.getInt("port", 3306);
                mariaDbDatabase = mariadb.getString("database", "clansystem");
                mariaDbUsername = mariadb.getString("username", "root");
                mariaDbPassword = mariadb.getString("password", "");
                mariaDbUseSsl = mariadb.getBoolean("use-ssl", false);
                mariaDbPoolSize = mariadb.getInt("pool-size", 6);
            }
        }
    }

    private StorageType parseStorageType(String value) {
        try {
            return StorageType.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            plugin.getLogger().warning("Unbekannter Datenbank-Typ '" + value + "' - falle zurück auf SQLITE.");
            return StorageType.SQLITE;
        }
    }

    private void loadClanSettings() {
        ConfigurationSection clan = config.getConfigurationSection("clan");
        if (clan != null) {
            ConfigurationSection name = clan.getConfigurationSection("name");
            if (name != null) {
                clanNameMinLength = name.getInt("min-length", 3);
                clanNameMaxLength = name.getInt("max-length", 16);
                String patternStr = name.getString("allowed-characters", "^[a-zA-Z0-9_]+$");
                clanNamePattern = Pattern.compile(patternStr);
            }
            leaveCooldown = clan.getInt("leave-cooldown", 86400);
            String joinTypeStr = clan.getString("default-join-type", "INVITE");
            try {
                defaultJoinType = Clan.JoinType.valueOf(joinTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                defaultJoinType = Clan.JoinType.INVITE;
            }
        }
    }

    private void loadSuffixSettings() {
        ConfigurationSection suffix = config.getConfigurationSection("suffix");
        if (suffix != null) {
            suffixMaxLength = suffix.getInt("max-length", 8);
            allowUnderline = suffix.getBoolean("allow-underline", false);
            allowStrikethrough = suffix.getBoolean("allow-strikethrough", false);
            allowMagic = suffix.getBoolean("allow-magic", false);
            suffixBlacklist = suffix.getStringList("blacklist");
        }
    }

    private void loadTeleportSettings() {
        ConfigurationSection teleport = config.getConfigurationSection("teleport");
        if (teleport != null) {
            teleportWarmup = teleport.getInt("warmup", 3);
            teleportCooldown = teleport.getInt("cooldown", 60);
            cancelOnMove = teleport.getBoolean("cancel-on-move", true);
            cancelOnDamage = teleport.getBoolean("cancel-on-damage", true);
        }
    }

    private void loadPvPSettings() {
        ConfigurationSection pvp = config.getConfigurationSection("pvp");
        if (pvp != null) {
            warKillPoints = pvp.getInt("war-kill-points", 10);
            normalKillPoints = pvp.getInt("normal-kill-points", 2);
            countNormalKills = pvp.getBoolean("count-normal-kills", true);
            disableFriendlyFire = pvp.getBoolean("disable-friendly-fire", true);
            disableAllyFire = pvp.getBoolean("disable-ally-fire", true);
        }
    }

    private void loadBroadcastSettings() {
        ConfigurationSection broadcasts = config.getConfigurationSection("broadcasts");
        if (broadcasts != null) {
            broadcastClanCreated = broadcasts.getBoolean("clan-created", true);
            broadcastClanDisbanded = broadcasts.getBoolean("clan-disbanded", true);
            broadcastMemberJoined = broadcasts.getBoolean("member-joined", true);
            broadcastMemberLeft = broadcasts.getBoolean("member-left", true);
        }
    }

    private void loadDefaultRanks() {
        defaultRanks.clear();
        List<?> ranksList = config.getList("default-ranks");
        if (ranksList != null) {
            for (Object obj : ranksList) {
                if (obj instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> rankMap = (java.util.Map<String, Object>) obj;

                    String name = (String) rankMap.get("name");
                    int priority = (int) rankMap.get("priority");

                    ClanRank rank = new ClanRank(name, priority);

                    @SuppressWarnings("unchecked")
                    List<String> permList = (List<String>) rankMap.get("permissions");
                    if (permList != null) {
                        for (String permStr : permList) {
                            Permission perm = Permission.fromString(permStr);
                            if (perm != null) {
                                rank.addPermission(perm);
                            }
                        }
                    }

                    defaultRanks.add(rank);
                }
            }
        }

        if (defaultRanks.isEmpty()) {
            ClanRank admin = new ClanRank("Admin", 100);
            for (Permission perm : Permission.values()) {
                if (perm != Permission.DISBAND) {
                    admin.addPermission(perm);
                }
            }
            defaultRanks.add(admin);

            ClanRank member = new ClanRank("Mitglied", 10);
            member.addPermission(Permission.CHEST_ACCESS);
            defaultRanks.add(member);
        }
    }

    public StorageType getStorageType() { return storageType; }
    public String getDbFile() { return dbFile; }
    public String getDbTablePrefix() { return dbTablePrefix; }
    public String getMariaDbHost() { return mariaDbHost; }
    public int getMariaDbPort() { return mariaDbPort; }
    public String getMariaDbDatabase() { return mariaDbDatabase; }
    public String getMariaDbUsername() { return mariaDbUsername; }
    public String getMariaDbPassword() { return mariaDbPassword; }
    public boolean isMariaDbUseSsl() { return mariaDbUseSsl; }
    public int getMariaDbPoolSize() { return mariaDbPoolSize; }

    public int getClanNameMinLength() { return clanNameMinLength; }
    public int getClanNameMaxLength() { return clanNameMaxLength; }
    public Pattern getClanNamePattern() { return clanNamePattern; }
    public int getLeaveCooldown() { return leaveCooldown; }
    public Clan.JoinType getDefaultJoinType() { return defaultJoinType; }

    public int getSuffixMaxLength() { return suffixMaxLength; }
    public boolean isAllowUnderline() { return allowUnderline; }
    public boolean isAllowStrikethrough() { return allowStrikethrough; }
    public boolean isAllowMagic() { return allowMagic; }
    public List<String> getSuffixBlacklist() { return suffixBlacklist; }

    public int getTeleportWarmup() { return teleportWarmup; }
    public int getTeleportCooldown() { return teleportCooldown; }
    public boolean isCancelOnMove() { return cancelOnMove; }
    public boolean isCancelOnDamage() { return cancelOnDamage; }

    public int getWarKillPoints() { return warKillPoints; }
    public int getNormalKillPoints() { return normalKillPoints; }
    public boolean isCountNormalKills() { return countNormalKills; }
    public boolean isDisableFriendlyFire() { return disableFriendlyFire; }
    public boolean isDisableAllyFire() { return disableAllyFire; }

    public boolean isBroadcastClanCreated() { return broadcastClanCreated; }
    public boolean isBroadcastClanDisbanded() { return broadcastClanDisbanded; }
    public boolean isBroadcastMemberJoined() { return broadcastMemberJoined; }
    public boolean isBroadcastMemberLeft() { return broadcastMemberLeft; }

    public List<ClanRank> getDefaultRanks() { return new ArrayList<>(defaultRanks); }

    public boolean isChestLogging() { return chestLogging; }
}
