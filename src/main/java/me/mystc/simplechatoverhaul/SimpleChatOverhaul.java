package me.mystc.simplechatoverhaul;

import me.mystc.simplechatoverhaul.Commands.Message;
import me.mystc.simplechatoverhaul.Commands.Reload;
import me.mystc.simplechatoverhaul.Commands.Reply;
import me.mystc.simplechatoverhaul.Commands.TabCompleters.MessageTabComplete;
import me.mystc.simplechatoverhaul.Files.MessagesFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class SimpleChatOverhaul extends JavaPlugin implements Listener {
    // Var
    static SimpleChatOverhaul instance;
    public static HashMap<Player, Player> replyPlayers = new HashMap<>();

    Reload reload = new Reload();
    Message message = new Message();
    MessageTabComplete messageTab = new MessageTabComplete();
    Reply reply = new Reply();

    public static String prefix = "";
    public static String pluginActivate = "";
    public static String pluginDeactivate = "";
    public static String noPermissionMessage = "";
    public static String pluginReloaded = "";
    public static String playerError = "";
    public static String messageCommandInputError = "";
    public static String messageCommandNoOnlineError = "";
    public static String replyCommandInputError = "";
    public static String replyCommandPlayerError = "";

    @Override
    public void onEnable() {
        instance = this;

        // Setup Configs
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        setupMessagesFile();
        getMessagesFile();

        prefix = getConfig().getString("plugin-prefix");

        setupCommands();

        Bukkit.getPluginManager().registerEvents(this, this);
        System.out.println(ChatColor.translateAlternateColorCodes('&', prefix + pluginActivate));
    }

    @Override
    public void onDisable() {
        System.out.println(ChatColor.translateAlternateColorCodes('&', prefix + pluginDeactivate));
    }

    // Events
    @EventHandler
    public void onSendMessage(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        String name = e.getPlayer().getDisplayName();
        String format = getConfig().getString("message-format");
        format = format.replaceAll("<player>", name);
        format = format.replaceAll("<message>", message);

        e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getDisplayName();
        String format = getConfig().getString("join-format");
        format = format.replaceAll("<player>", name);

        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', format));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getDisplayName();
        String format = getConfig().getString("leave-format");
        format = format.replaceAll("<player>", name);

        e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', format));
    }

    // Functions
    void setupMessagesFile() {
        MessagesFile.setup();

        MessagesFile.get().addDefault("plugin-startup", "&ePlugin&a activated.");
        MessagesFile.get().addDefault("plugin-shutdown", "&ePlugin&c deactivated.");
        MessagesFile.get().addDefault("no-permission", "&cNo permission.");
        MessagesFile.get().addDefault("plugin-reloaded", "&ePlugin&a reloaded.");
        MessagesFile.get().addDefault("player-error", "&cYou must be a player to run that command");
        MessagesFile.get().addDefault("message-command-input-error", "&cIncorrect format&e: /msg <player> <message>");
        MessagesFile.get().addDefault("message-command-noOnline-error", "&e That player isn't&c online&e.");
        MessagesFile.get().addDefault("reply-command-input-error", "&cIncorrect format&e: /r <message>.");
        MessagesFile.get().addDefault("reply-command-player-error", "&eYou have no one to&c reply&e to.");

        MessagesFile.get().options().copyDefaults(true);
        MessagesFile.save();
    }

    static void getMessagesFile() {
        pluginActivate = MessagesFile.get().getString("plugin-startup");
        pluginDeactivate = MessagesFile.get().getString("plugin-shutdown");
        noPermissionMessage = MessagesFile.get().getString("no-permission");
        pluginReloaded = MessagesFile.get().getString("plugin-reloaded");
        playerError = MessagesFile.get().getString("player-error");
        messageCommandInputError = MessagesFile.get().getString("message-command-input-error");
        messageCommandNoOnlineError = MessagesFile.get().getString("message-command-noOnline-error");
        replyCommandInputError = MessagesFile.get().getString("reply-command-input-error");
        replyCommandPlayerError = MessagesFile.get().getString("reply-command-player-error");
    }

    void setupCommands() {
        getCommand("coreload").setExecutor(reload);
        getCommand("message").setExecutor(message);
        getCommand("message").setTabCompleter(messageTab);
        getCommand("reply").setExecutor(reply);
    }

    public static SimpleChatOverhaul getInstance() {
        return instance;
    }
}
