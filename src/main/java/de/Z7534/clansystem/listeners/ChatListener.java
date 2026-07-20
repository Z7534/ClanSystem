package de.Z7534.clansystem.listeners;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.managers.MessageManager;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.models.ClanMember;
import de.Z7534.clansystem.utils.ColorUtils;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private static final Set<UUID> clanChatToggled = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> allyChatToggled = ConcurrentHashMap.newKeySet();

    private final Clansystem plugin;

    public ChatListener(Clansystem plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (plugin.getChatInputManager().hasPending(uuid)) {
            event.setCancelled(true);
            String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
            plugin.getChatInputManager().handleInput(player, plainMessage);
            return;
        }

        if (clanChatToggled.contains(uuid) || allyChatToggled.contains(uuid)) {
            Clan clan = plugin.getClanManager().getClanByPlayer(player);

            if (clan == null) {

                clearToggles(uuid);
                return;
            }

            event.setCancelled(true);
            String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

            if (clanChatToggled.contains(uuid)) {
                sendClanChat(player, clan, plainMessage);
            } else {
                sendAllyChat(player, clan, plainMessage);
            }
            return;
        }

        Clan clan = plugin.getClanManager().getClanByPlayer(player);
        String suffix = (clan != null && !clan.getSuffix().isEmpty()) ? " " + clan.getColoredSuffix() : "";

        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
            String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
            String formatted = plugin.getMessageManager().getRawWithPlaceholders("chat.global-format",
                    MessageManager.of("player", player.getName(), "suffix", suffix, "message", plainMessage));
            return ColorUtils.colorize(formatted);
        }));
    }

    public void sendClanChat(Player player, Clan clan, String message) {
        ClanMember member = clan.getMember(player.getUniqueId());
        String rankName = member != null ? member.getDisplayRankName() : "?";
        String suffix = clan.getSuffix().isEmpty() ? "" : " " + clan.getColoredSuffix();

        String formatted = plugin.getMessageManager().getRawWithPlaceholders("chat.format",
                MessageManager.of("rank", rankName, "suffix", suffix, "player", player.getName(), "message", message));

        clan.broadcastMessage(formatted);
    }

    public void sendAllyChat(Player player, Clan clan, String message) {
        String formatted = plugin.getMessageManager().getRawWithPlaceholders("chat.ally-format",
                MessageManager.of("clan", clan.getColoredName(), "player", player.getName(), "message", message));

        clan.broadcastMessage(formatted);
        for (Clan ally : plugin.getAllianceManager().getAllies(clan)) {
            ally.broadcastMessage(formatted);
        }
    }

    public enum ChatMode {
        GLOBAL, CLAN, ALLY
    }

    public static ChatMode getChatMode(UUID uuid) {
        if (clanChatToggled.contains(uuid)) {
            return ChatMode.CLAN;
        }
        if (allyChatToggled.contains(uuid)) {
            return ChatMode.ALLY;
        }
        return ChatMode.GLOBAL;
    }

    public static void setChatMode(Player player, ChatMode mode) {
        UUID uuid = player.getUniqueId();
        clanChatToggled.remove(uuid);
        allyChatToggled.remove(uuid);

        switch (mode) {
            case CLAN -> clanChatToggled.add(uuid);
            case ALLY -> allyChatToggled.add(uuid);
            default -> {
            }
        }
    }

    public static void toggleClanChat(Player player) {
        UUID uuid = player.getUniqueId();
        MessageManager messages = Clansystem.getInstance().getMessageManager();

        if (clanChatToggled.remove(uuid)) {
            messages.send(player, "chat.toggled-off");
        } else {
            clanChatToggled.add(uuid);
            allyChatToggled.remove(uuid);
            messages.send(player, "chat.toggled-on");
        }
    }

    public static void toggleAllyChat(Player player) {
        UUID uuid = player.getUniqueId();
        MessageManager messages = Clansystem.getInstance().getMessageManager();

        if (allyChatToggled.remove(uuid)) {
            messages.send(player, "chat.ally-toggled-off");
        } else {
            allyChatToggled.add(uuid);
            clanChatToggled.remove(uuid);
            messages.send(player, "chat.ally-toggled-on");
        }
    }

    public static boolean isClanChatToggled(UUID uuid) {
        return clanChatToggled.contains(uuid);
    }

    public static boolean isAllyChatToggled(UUID uuid) {
        return allyChatToggled.contains(uuid);
    }

    public static void clearToggles(UUID uuid) {
        clanChatToggled.remove(uuid);
        allyChatToggled.remove(uuid);
    }
}
