package de.Z7534.clansystem.managers;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputManager {

    private final Clansystem plugin;
    private final Map<UUID, Consumer<String>> pending = new ConcurrentHashMap<>();

    public ChatInputManager(Clansystem plugin) {
        this.plugin = plugin;
    }

    public void request(Player player, String prompt, Consumer<String> onInput) {
        player.closeInventory();
        pending.put(player.getUniqueId(), onInput);

        player.sendMessage(ColorUtils.colorize("&8&m--------------------------------"));
        player.sendMessage(ColorUtils.colorize("&e" + prompt));
        player.sendMessage(ColorUtils.colorize("&7Schreibe &c'abbrechen' &7in den Chat, um abzubrechen."));
        player.sendMessage(ColorUtils.colorize("&8&m--------------------------------"));
    }

    public boolean hasPending(UUID uuid) {
        return pending.containsKey(uuid);
    }

    public boolean handleInput(Player player, String message) {
        Consumer<String> callback = pending.remove(player.getUniqueId());
        if (callback == null) {
            return false;
        }

        if (message.equalsIgnoreCase("abbrechen") || message.equalsIgnoreCase("cancel")) {
            player.sendMessage(ColorUtils.colorize("&cEingabe abgebrochen."));
            return true;
        }

        Bukkit.getScheduler().runTask(plugin, () -> callback.accept(message));
        return true;
    }

    public void cancel(UUID uuid) {
        pending.remove(uuid);
    }
}
