package de.Z7534.clansystem;

import de.Z7534.clansystem.commands.ClanAdminCommand;
import de.Z7534.clansystem.commands.ClanCommand;
import de.Z7534.clansystem.listeners.ChatListener;
import de.Z7534.clansystem.listeners.GUIListener;
import de.Z7534.clansystem.listeners.PlayerListener;
import de.Z7534.clansystem.listeners.PvPListener;
import de.Z7534.clansystem.managers.*;
import de.Z7534.clansystem.placeholders.ClanPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Clansystem extends JavaPlugin {

    private static Clansystem instance;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private LevelManager levelManager;
    private ClanManager clanManager;
    private WarManager warManager;
    private AllianceManager allianceManager;
    private ChatInputManager chatInputManager;
    private GlowManager glowManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("========================================");
        getLogger().info("  CLANSYSTEM v" + getDescription().getVersion());
        getLogger().info("  Autor: Z7534");
        getLogger().info("========================================");

        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("level.yml", false);

        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.levelManager = new LevelManager(this);

        this.databaseManager = new DatabaseManager(this);
        if (!databaseManager.connect()) {
            getLogger().severe("Konnte keine Verbindung zur Datenbank herstellen!");
            getLogger().severe("Plugin wird deaktiviert...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        databaseManager.createTables();

        this.clanManager = new ClanManager(this);
        this.warManager = new WarManager(this);
        this.allianceManager = new AllianceManager(this);
        this.chatInputManager = new ChatInputManager(this);
        this.glowManager = new GlowManager(this);
        this.teleportManager = new TeleportManager(this);

        clanManager.loadAllClans();

        glowManager.setupAll();

        ClanCommand clanCommand = new ClanCommand(this);
        getCommand("clan").setExecutor(clanCommand);
        getCommand("clan").setTabCompleter(clanCommand);

        ClanAdminCommand clanAdminCommand = new ClanAdminCommand(this);
        getCommand("clanadmin").setExecutor(clanAdminCommand);
        getCommand("clanadmin").setTabCompleter(clanAdminCommand);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListener(this), this);
        Bukkit.getPluginManager().registerEvents(teleportManager, this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ClanPlaceholders(this).register();
            getLogger().info("PlaceholderAPI gefunden - Platzhalter registriert!");
        }

        getLogger().info("Plugin erfolgreich aktiviert!");
    }

    @Override
    public void onDisable() {

        if (clanManager != null) {
            clanManager.saveAllClans();
        }

        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("Plugin deaktiviert!");
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();
        levelManager.reload();
        clanManager.loadAllClans();
    }

    public static Clansystem getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public WarManager getWarManager() {
        return warManager;
    }

    public AllianceManager getAllianceManager() {
        return allianceManager;
    }

    public ChatInputManager getChatInputManager() {
        return chatInputManager;
    }

    public GlowManager getGlowManager() {
        return glowManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }
}
