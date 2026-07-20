package de.Z7534.clansystem.models;

public enum Permission {

    INVITE("Mitglieder einladen"),
    KICK("Mitglieder kicken"),
    PROMOTE("Mitglieder befördern"),
    DEMOTE("Mitglieder degradieren"),

    SET_HOME("Homes setzen"),
    DEL_HOME("Homes löschen"),
    SET_WARP("Warps setzen"),
    DEL_WARP("Warps löschen"),

    CHEST_ACCESS("Truhen-Zugriff"),
    CHEST_WITHDRAW("Aus Truhe entnehmen"),

    ALLY_MANAGE("Allianzen verwalten"),
    WAR_MANAGE("Kriege verwalten"),

    SETTINGS("Einstellungen ändern"),
    RANK_MANAGE("Ränge verwalten"),
    SET_SUFFIX("Suffix ändern"),
    DISBAND("Clan auflösen");

    private final String displayName;

    Permission(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Permission fromString(String name) {
        try {
            return Permission.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
