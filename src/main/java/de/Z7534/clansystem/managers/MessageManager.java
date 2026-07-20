package de.Z7534.clansystem.managers;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final Clansystem plugin;
    private FileConfiguration messagesConfig;
    private String prefix;

    public MessageManager(Clansystem plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = messagesConfig.getString("prefix", "&8[&6Clan&8] &7");
    }

    public String getPrefix() {
        return prefix;
    }

    public String getRaw(String path) {
        return messagesConfig.getString(path, "&cNachricht nicht gefunden: " + path);
    }

    public String get(String path) {
        return prefix + getRaw(path);
    }

    public String get(String path, Map<String, String> placeholders) {
        String message = get(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    public String getRawWithPlaceholders(String path, Map<String, String> placeholders) {
        String message = getRaw(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return message;
    }

    public Component getComponent(String path) {
        return ColorUtils.colorize(get(path));
    }

    public Component getComponent(String path, Map<String, String> placeholders) {
        return ColorUtils.colorize(get(path, placeholders));
    }

    public void send(CommandSender sender, String path) {
        sender.sendMessage(getComponent(path));
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        sender.sendMessage(getComponent(path, placeholders));
    }

    public void sendRaw(CommandSender sender, String path) {
        sender.sendMessage(ColorUtils.colorize(getRaw(path)));
    }

    public void sendRaw(CommandSender sender, String path, Map<String, String> placeholders) {
        sender.sendMessage(ColorUtils.colorize(getRawWithPlaceholders(path, placeholders)));
    }

    public void broadcast(String path) {
        Component message = getComponent(path);
        Bukkit.broadcast(message);
    }

    public void broadcast(String path, Map<String, String> placeholders) {
        Component message = getComponent(path, placeholders);
        Bukkit.broadcast(message);
    }

    public void broadcastExcept(String path, org.bukkit.entity.Player exclude, Map<String, String> placeholders) {
        broadcastExcept(path, placeholders, exclude);
    }

    public void broadcastExcept(String path, Map<String, String> placeholders, org.bukkit.entity.Player... exclude) {
        Component message = getComponent(path, placeholders);
        java.util.Set<java.util.UUID> excludedIds = new java.util.HashSet<>();
        if (exclude != null) {
            for (org.bukkit.entity.Player p : exclude) {
                if (p != null) {
                    excludedIds.add(p.getUniqueId());
                }
            }
        }
        for (org.bukkit.entity.Player online : Bukkit.getOnlinePlayers()) {
            if (excludedIds.contains(online.getUniqueId())) {
                continue;
            }
            online.sendMessage(message);
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static Map<String, String> of(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static Map<String, String> of(String k1, String v1, String k2, String v2) {
        Map<String, String> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    public static Map<String, String> of(String k1, String v1, String k2, String v2, String k3, String v3) {
        Map<String, String> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    public static Map<String, String> of(String k1, String v1, String k2, String v2,
                                         String k3, String v3, String k4, String v4) {
        Map<String, String> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    public static String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + " Sekunden";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long secs = seconds % 60;
            return minutes + " Minuten" + (secs > 0 ? " " + secs + " Sekunden" : "");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + " Stunden" + (minutes > 0 ? " " + minutes + " Minuten" : "");
        } else {
            long days = seconds / 86400;
            long hours = (seconds % 86400) / 3600;
            return days + " Tage" + (hours > 0 ? " " + hours + " Stunden" : "");
        }
    }

    public static String formatTimeShort(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m " + (seconds % 60) + "s";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "h " + ((seconds % 3600) / 60) + "m";
        } else {
            return (seconds / 86400) + "d " + ((seconds % 86400) / 3600) + "h";
        }
    }
}
