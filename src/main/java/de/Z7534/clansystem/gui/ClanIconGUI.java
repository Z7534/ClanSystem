package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.utils.ClanIcons;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClanIconGUI extends AbstractGUI {

    private final Clan clan;

    public ClanIconGUI(Clansystem plugin, Player player, Clan clan) {
        super(plugin, player, plugin.getMessageManager().getRaw("gui.icon-picker"), 45);
        this.clan = clan;
    }

    @Override
    public void setup() {
        fillFancyBorder();

        int slot = 10;
        for (String iconName : plugin.getLevelManager().getAllConfiguredIcons()) {
            Material material = ClanIcons.fromName(iconName);

            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 44) break;

            int requiredLevel = plugin.getLevelManager().getRequiredLevelForIcon(iconName);
            boolean unlocked = clan.getLevel() >= requiredLevel;
            boolean selected = material.name().equalsIgnoreCase(clan.getIcon());

            if (!unlocked) {
                setItem(slot, new ItemBuilder(Material.BARRIER)
                        .name("&7&l✘ " + formatName(material))
                        .lore(
                                "&cNoch nicht freigeschaltet!",
                                "&7Erfordert Clan-Level &e" + requiredLevel,
                                "&7(aktuell: Level " + clan.getLevel() + ")"
                        )
                        .build());
            } else {
                setItem(slot, new ItemBuilder(material)
                        .name((selected ? "&a&l✔ " : "&f") + formatName(material))
                        .lore(selected ? "&7Aktuell ausgewählt" : "&a▶ &7Klicke zum Auswählen")
                        .glow(selected)
                        .build(), () -> {
                    clan.setIcon(material.name());
                    plugin.getClanManager().updateClanInDatabase(clan);
                    refresh();
                    player.updateInventory();
                });
            }

            slot++;
        }

        addBackButton(40, () -> new ClanSettingsGUI(plugin, player, clan).open());

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }

    private String formatName(Material material) {
        String[] parts = material.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase()).append(" ");
        }
        return sb.toString().trim();
    }
}
