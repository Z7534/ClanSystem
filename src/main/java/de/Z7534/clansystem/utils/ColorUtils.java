package de.Z7534.clansystem.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fk-or])");

    public static Component colorize(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }

        message = translateHexColorCodes(message);

        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public static String translateColors(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = translateHexColorCodes(message);

        char[] chars = message.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
                chars[i] = '\u00A7';
                chars[i + 1] = Character.toLowerCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

    private static String translateHexColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("&x");
            for (char c : hex.toCharArray()) {
                replacement.append('&').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    public static String stripColors(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = HEX_PATTERN.matcher(message).replaceAll("");

        message = COLOR_CODE_PATTERN.matcher(message).replaceAll("");
        message = message.replaceAll("\u00A7[0-9a-fk-or]", "");

        return message;
    }

    public static boolean isColorAllowed(String colorCode, java.util.List<String> allowedColors) {
        if (allowedColors == null || allowedColors.isEmpty()) {
            return true;
        }
        return allowedColors.contains(colorCode.toLowerCase());
    }

    public static java.util.List<String> extractColorCodes(String message) {
        java.util.List<String> codes = new java.util.ArrayList<>();

        if (message == null || message.isEmpty()) {
            return codes;
        }

        Matcher matcher = COLOR_CODE_PATTERN.matcher(message);
        while (matcher.find()) {
            codes.add("&" + matcher.group(1).toLowerCase());
        }

        return codes;
    }

    public static boolean containsBold(String message) {
        return message != null && (message.contains("&l") || message.contains("&L"));
    }

    public static boolean containsItalic(String message) {
        return message != null && (message.contains("&o") || message.contains("&O"));
    }

    public static boolean containsUnderline(String message) {
        return message != null && (message.contains("&n") || message.contains("&N"));
    }

    public static boolean containsStrikethrough(String message) {
        return message != null && (message.contains("&m") || message.contains("&M"));
    }

    public static boolean containsMagic(String message) {
        return message != null && (message.contains("&k") || message.contains("&K"));
    }
}
