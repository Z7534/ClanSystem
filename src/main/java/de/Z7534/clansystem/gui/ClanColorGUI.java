package de.Z7534.clansystem.gui;

import de.Z7534.clansystem.Clansystem;
import de.Z7534.clansystem.models.Clan;
import de.Z7534.clansystem.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClanColorGUI extends AbstractGUI {

    private static final Map<String, String> COLORS = new LinkedHashMap<>();

    static {
        COLORS.put("&b", "Aqua");
        COLORS.put("&0", "Black");
        COLORS.put("&9", "Blue");
        COLORS.put("&2", "Dark Green");
        COLORS.put("&5", "Dark Purple");
        COLORS.put("&4", "Dark Red");
        COLORS.put("&c", "Red");
        COLORS.put("&3", "Dark Aqua");
        COLORS.put("&1", "Dark Blue");
        COLORS.put("&8", "Dark Gray");
        COLORS.put("&6", "Gold");
        COLORS.put("&7", "Gray");
        COLORS.put("&a", "Green");
        COLORS.put("&e", "Yellow");
        COLORS.put("&f", "White");
    }

    private final Clan clan;

    public ClanColorGUI(Clansystem plugin, Player player, Clan clan) {
        super(plugin, player, "&8&l✦ &6Clan-Farbe", 45);
        this.clan = clan;
    }

    @Override
    public void setup() {
        fillFancyBorder();

        String preview = clan.getColoredName();
        setItem(4, new ItemBuilder(Material.NAME_TAG)
                .name("&6&l✦ Vorschau")
                .lore(
                        "",
                        preview,
                        "",
                        "&7Diese Farbe gilt für deinen",
                        "&7Clan-Namen in Nachrichten",
                        "&7UND für deinen Clan-Suffix."
                )
                .glow()
                .build());

        int slot = 10;
        for (Map.Entry<String, String> entry : COLORS.entrySet()) {
            String code = entry.getKey();
            String colorName = entry.getValue();

            while (slot % 9 == 0 || slot % 9 == 8) {
                slot++;
            }
            if (slot >= 35) break;

            boolean selected = code.equalsIgnoreCase(clan.getNameColor());

            setItem(slot, new ItemBuilder(materialFor(code))
                    .name((selected ? "&a&l✔ " : "&f") + colorName)
                    .lore(selected ? "&7Aktuell ausgewählt" : "&a▶ &7Klicke zum Auswählen")
                    .glow(selected)
                    .build(), () -> {
                clan.setNameColor(code);
                plugin.getClanManager().updateClanInDatabase(clan);
                refresh();
                player.updateInventory();
            });

            slot++;
        }

        boolean strikethrough = clan.isNameStrikethrough();
        setItem(40, new ItemBuilder(Material.TRIPWIRE_HOOK)
                .name((strikethrough ? "&a&l✔ " : "&7") + "Durchgestrichen")
                .lore(
                        "&7Status: " + (strikethrough ? "&aAktiviert" : "&cDeaktiviert"),
                        "&7Gilt für Clan-Name und Suffix.",
                        "",
                        "&a▶ &7Klicke zum Umschalten"
                )
                .glow(strikethrough)
                .build(), () -> {
            clan.setNameStrikethrough(!clan.isNameStrikethrough());
            plugin.getClanManager().updateClanInDatabase(clan);
            refresh();
            player.updateInventory();
        });

        addBackButton(36, () -> new ClanSettingsGUI(plugin, player, clan).open());
        addCloseButton(44);

        fillEmpty(Material.BLACK_STAINED_GLASS_PANE);
    }

    private Material materialFor(String code) {
        return switch (code.toLowerCase()) {
            case "&0" -> Material.BLACK_DYE;
            case "&1" -> Material.LAPIS_LAZULI;
            case "&2" -> Material.GREEN_DYE;
            case "&3" -> Material.CYAN_DYE;
            case "&4" -> Material.BRICK;
            case "&5" -> Material.PURPLE_DYE;
            case "&6" -> Material.GOLD_INGOT;
            case "&7" -> Material.LIGHT_GRAY_DYE;
            case "&8" -> Material.GRAY_DYE;
            case "&9" -> Material.BLUE_DYE;
            case "&a" -> Material.LIME_DYE;
            case "&b" -> Material.LIGHT_BLUE_DYE;
            case "&c" -> Material.RED_DYE;
            case "&e" -> Material.YELLOW_DYE;
            case "&f" -> Material.WHITE_DYE;
            default -> Material.GRAY_DYE;
        };
    }
}
