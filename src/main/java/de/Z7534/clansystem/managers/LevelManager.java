package de.Z7534.clansystem.managers;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LevelManager {

    private final Clansystem plugin;
    private FileConfiguration levelConfig;

    private int baseMaxMembers;
    private int baseMaxHomes;
    private int baseMaxWarps;
    private int baseChestRows;
    private String baseIcon;

    private int maxLevel;
    private Map<Integer, Integer> pointsRequired;
    private Map<Integer, LevelRewards> rewards;

    public LevelManager(Clansystem plugin) {
        this.plugin = plugin;
        this.pointsRequired = new HashMap<>();
        this.rewards = new HashMap<>();
        reload();
    }

    public void reload() {
        File levelFile = new File(plugin.getDataFolder(), "level.yml");
        if (!levelFile.exists()) {
            plugin.saveResource("level.yml", false);
        }
        levelConfig = YamlConfiguration.loadConfiguration(levelFile);

        loadBaseValues();
        loadLevels();
    }

    private void loadBaseValues() {
        ConfigurationSection base = levelConfig.getConfigurationSection("base-values");
        if (base != null) {
            baseMaxMembers = base.getInt("max-members", 10);
            baseMaxHomes = base.getInt("max-homes", 1);
            baseMaxWarps = base.getInt("max-warps", 1);
            baseChestRows = base.getInt("chest-rows", 1);
            baseIcon = base.getString("icon", "SHIELD");
        } else {
            baseIcon = "SHIELD";
        }
    }

    private void loadLevels() {
        pointsRequired.clear();
        rewards.clear();
        maxLevel = 0;

        ConfigurationSection levels = levelConfig.getConfigurationSection("levels");
        if (levels != null) {
            for (String key : levels.getKeys(false)) {
                try {
                    int level = Integer.parseInt(key);
                    ConfigurationSection levelSection = levels.getConfigurationSection(key);

                    if (levelSection != null) {
                        int points = levelSection.getInt("points-required", level * 100);
                        pointsRequired.put(level, points);

                        ConfigurationSection rewardsSection = levelSection.getConfigurationSection("rewards");
                        if (rewardsSection != null) {
                            LevelRewards levelRewards = new LevelRewards(
                                    rewardsSection.getInt("max-members", 0),
                                    rewardsSection.getInt("max-homes", 0),
                                    rewardsSection.getInt("max-warps", 0),
                                    rewardsSection.getInt("chest-rows", 0),
                                    rewardsSection.getString("icon", null)
                            );
                            rewards.put(level, levelRewards);
                        }

                        if (level > maxLevel) {
                            maxLevel = level;
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (maxLevel == 0) {
            maxLevel = 5;
            pointsRequired.put(1, 100);
            pointsRequired.put(2, 300);
            pointsRequired.put(3, 600);
            pointsRequired.put(4, 1000);
            pointsRequired.put(5, 1500);

            rewards.put(1, new LevelRewards(0, 0, 0, 0, "NETHER_STAR"));
            rewards.put(2, new LevelRewards(5, 1, 1, 1, "DIAMOND"));
            rewards.put(3, new LevelRewards(5, 1, 1, 1, "EMERALD"));
            rewards.put(4, new LevelRewards(5, 1, 2, 1, "TOTEM_OF_UNDYING"));
            rewards.put(5, new LevelRewards(10, 2, 3, 2, "NETHERITE_INGOT"));
        }
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getBaseIcon() {
        return baseIcon;
    }

    public boolean isMaxLevel(int level) {
        return level >= maxLevel;
    }

    public int getPointsRequired(int level) {
        return pointsRequired.getOrDefault(level, level * 100);
    }

    public LevelRewards getRewards(int level) {
        return rewards.getOrDefault(level, new LevelRewards(0, 0, 0, 0, null));
    }

    public int calculateLevel(int points) {
        int level = 1;
        for (int i = 1; i <= maxLevel; i++) {
            if (points >= getPointsRequired(i)) {
                level = i;
            } else {
                break;
            }
        }
        return level;
    }

    public int getMaxMembers(int level) {
        int total = baseMaxMembers;
        for (int i = 1; i <= level; i++) {
            LevelRewards r = getRewards(i);
            total += r.maxMembers();
        }
        return total;
    }

    public int getMaxHomes(int level) {

        return 1;
    }

    public int getMaxWarps(int level) {
        int total = baseMaxWarps;
        for (int i = 1; i <= level; i++) {
            LevelRewards r = getRewards(i);
            total += r.maxWarps();
        }
        return total;
    }

    public int getChestSize(int level) {
        int totalRows = baseChestRows;
        for (int i = 1; i <= level; i++) {
            LevelRewards r = getRewards(i);
            totalRows += r.chestRows();
        }

        totalRows = Math.min(totalRows, 6);
        return totalRows * 9;
    }

    public boolean checkLevelUp(Clan clan) {
        int currentLevel = clan.getLevel();
        if (currentLevel >= maxLevel) {
            return false;
        }

        int nextLevel = currentLevel + 1;
        int required = getPointsRequired(nextLevel);

        if (clan.getPoints() >= required) {
            clan.setLevel(nextLevel);

            int newChestSize = getChestSize(nextLevel);
            if (clan.getChestSize() < newChestSize) {
                clan.resizeChest(newChestSize);
            }

            return true;
        }

        return false;
    }

    public String getRewardsDescription(int level) {
        LevelRewards r = getRewards(level);
        StringBuilder sb = new StringBuilder();

        if (r.maxMembers() > 0) {
            sb.append("&a+").append(r.maxMembers()).append(" Mitglieder ");
        }
        if (r.maxHomes() > 0) {
            sb.append("&a+").append(r.maxHomes()).append(" Homes ");
        }
        if (r.maxWarps() > 0) {
            sb.append("&a+").append(r.maxWarps()).append(" Warps ");
        }
        if (r.chestRows() > 0) {
            sb.append("&a+").append(r.chestRows() * 9).append(" Truhen-Slots ");
        }
        if (r.icon() != null && !r.icon().isBlank()) {
            sb.append("&a+1 neues Clan-Icon ");
        }

        return sb.toString().trim();
    }

    public java.util.List<String> getUnlockedIcons(int level) {
        java.util.List<String> icons = new java.util.ArrayList<>();
        if (baseIcon != null) {
            icons.add(baseIcon);
        }
        for (int i = 1; i <= level; i++) {
            String icon = getRewards(i).icon();
            if (icon != null && !icon.isBlank() && !icons.contains(icon)) {
                icons.add(icon);
            }
        }
        return icons;
    }

    public java.util.List<String> getAllConfiguredIcons() {
        java.util.List<String> icons = new java.util.ArrayList<>();
        if (baseIcon != null) {
            icons.add(baseIcon);
        }
        for (int i = 1; i <= maxLevel; i++) {
            String icon = getRewards(i).icon();
            if (icon != null && !icon.isBlank() && !icons.contains(icon)) {
                icons.add(icon);
            }
        }
        return icons;
    }

    public int getRequiredLevelForIcon(String iconName) {
        if (iconName.equalsIgnoreCase(baseIcon)) {
            return 0;
        }
        for (int i = 1; i <= maxLevel; i++) {
            String icon = getRewards(i).icon();
            if (icon != null && icon.equalsIgnoreCase(iconName)) {
                return i;
            }
        }
        return -1;
    }

    public record LevelRewards(int maxMembers, int maxHomes, int maxWarps, int chestRows, String icon) {
    }
}
