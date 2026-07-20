package de.Z7534.clansystem.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GlowPacketHook {

    private static final int FLAGS_METADATA_INDEX = 0;

    private static final byte GLOWING_BIT = 0x40;

    private final Clansystem plugin;

    public GlowPacketHook(Clansystem plugin) {
        this.plugin = plugin;
        register();
    }

    private void register() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL,
                PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    handle(event);
                } catch (Throwable t) {

                    plugin.getLogger().warning("Fehler im Glow-Paket-Filter (wird ignoriert, Paket bleibt unveraendert): " + t);
                }
            }
        });
    }

    private void handle(PacketEvent event) {
        Player viewer = event.getPlayer();

        Entity entity = event.getPacket().getEntityModifier(event).read(0);
        if (!(entity instanceof Player target)) {

            return;
        }

        if (viewer == null || target.getUniqueId().equals(viewer.getUniqueId())) {
            return;
        }

        Clan targetClan = plugin.getClanManager().getClanByPlayer(target);
        if (targetClan == null || !targetClan.isGlowEnabled()) {

            return;
        }

        Clan viewerClan = plugin.getClanManager().getClanByPlayer(viewer);
        boolean sameClan = viewerClan != null && viewerClan.getId() == targetClan.getId();
        if (sameClan) {

            return;
        }

        List<WrappedDataValue> values = event.getPacket().getDataValueCollectionModifier().read(0);
        if (values == null || values.isEmpty()) {
            return;
        }

        boolean changed = false;
        List<WrappedDataValue> newValues = new ArrayList<>(values.size());

        for (WrappedDataValue value : values) {
            if (value.getIndex() == FLAGS_METADATA_INDEX && value.getValue() instanceof Byte flagsValue) {
                byte flags = flagsValue;
                byte stripped = (byte) (flags & ~GLOWING_BIT);
                if (stripped != flags) {
                    newValues.add(new WrappedDataValue(value.getIndex(), value.getSerializer(), stripped));
                    changed = true;
                    continue;
                }
            }
            newValues.add(value);
        }

        if (changed) {
            event.getPacket().getDataValueCollectionModifier().write(0, newValues);
        }
    }
}
