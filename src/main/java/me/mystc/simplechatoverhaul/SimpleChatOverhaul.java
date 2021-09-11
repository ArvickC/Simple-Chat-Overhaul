package me.mystc.simplechatoverhaul;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.mystc.simplechatoverhaul.Commands.*;
import me.mystc.simplechatoverhaul.Commands.TabCompleters.MessageTabComplete;
import me.mystc.simplechatoverhaul.Files.GroupsFile;
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
    public static HashMap<Player, Boolean> staffChatToggle = new HashMap<>();

    Reload reload = new Reload();
    Message message = new Message();
    MessageTabComplete messageTab = new MessageTabComplete();
    Reply reply = new Reply();
    Announce bc = new Announce();
    Staffchat staffchat = new Staffchat();

    public static Boolean groupEnabled = false;

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
    public static String announceCommandInputError = "";

    @Override
    public void onEnable() {
        instance = this;

        // Setup Configs
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        setupMessagesFile();
        getMessagesFile();
        groupSetup();

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
        String format = "";
        String staffTrigger = getConfig().getString("staffchat.staffchat-trigger");
        int triggerLength = staffTrigger.length();
        String staffFormat = getConfig().getString("staffchat.staffchat-format");

        if(e.getPlayer().hasPermission("chat.staff")) {
            if(staffChatToggle.containsKey(e.getPlayer())) {
                if(staffChatToggle.get(e.getPlayer())) {
                    message = message.replaceFirst(staffTrigger, "");
                    staffFormat = staffFormat.replaceAll("<player>", name);
                    staffFormat = staffFormat.replaceAll("<message>", message);
                    e.setCancelled(true);
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.hasPermission("chat.staff")) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', staffFormat));
                        }
                    }
                    System.out.println(ChatColor.translateAlternateColorCodes('&', staffFormat));
                    return;
                }
            }

            if(message.substring(0, triggerLength).equalsIgnoreCase(staffTrigger)) {
                message = message.replaceFirst(staffTrigger, "");
                staffFormat = staffFormat.replaceAll("<player>", name);
                staffFormat = staffFormat.replaceAll("<message>", message);
                e.setCancelled(true);
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.hasPermission("chat.staff")) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', staffFormat));
                    }
                }
                System.out.println(ChatColor.translateAlternateColorCodes('&', staffFormat));
                return;
            }
        }

        if(groupEnabled) {
            String[] groups = getConfig().getStringList("groups.group-names").toArray(new String[0]);
            for(String group : groups) {
                if(e.getPlayer().hasPermission("chat.group." + group)) {
                    format = GroupsFile.get().getString(group);
                    format = format.replaceAll("<player>", name);
                    format = format.replaceAll("<message>", message);
                    e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
                    return;
                }
            }
        }
        format = getConfig().getString("message-format");
        format = format.replaceAll("<player>", name);
        format = format.replaceAll("<message>", message);

        e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getDisplayName();
        String format = "";
        if(groupEnabled) {
            String[] groups = getConfig().getStringList("groups.group-names").toArray(new String[0]);
            for(String group : groups) {
                if(e.getPlayer().hasPermission("chat.group." + group)) {
                    format = GroupsFile.get().getString(group+"-join");
                    format = format.replaceAll("<player>", name);
                    e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', format));
                    return;
                }
            }
        }
        format = getConfig().getString("join-format");
        format = format.replaceAll("<player>", name);

        e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', format));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        String name = e.getPlayer().getDisplayName();
        String format = "";
        if(groupEnabled) {
            String[] groups = getConfig().getStringList("groups.group-names").toArray(new String[0]);
            for(String group : groups) {
                if(e.getPlayer().hasPermission("chat.group." + group)) {
                    format = GroupsFile.get().getString(group+"-leave");
                    format = format.replaceAll("<player>", name);
                    e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', format));
                    return;
                }
            }
        }
        format = getConfig().getString("leave-format");
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
        MessagesFile.get().addDefault("announce-command-input-error", "&cIncorrect format&e: /bc <message>");

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
        announceCommandInputError = MessagesFile.get().getString("announce-command-input-error");
    }

    void setupCommands() {
        getCommand("coreload").setExecutor(reload);
        getCommand("message").setExecutor(message);
        getCommand("message").setTabCompleter(messageTab);
        getCommand("reply").setExecutor(reply);
        getCommand("announce").setExecutor(bc);
        getCommand("staffchat").setExecutor(staffchat);
    }

    void groupSetup() {
        if(getConfig().getBoolean("groups.enable-groups")) {
            groupEnabled = true;
        }

        GroupsFile.setup();

        GroupsFile.get().addDefault("admin", "&7[&cAdmin&7]&c <player>&7 >>&f <message>");
        GroupsFile.get().addDefault("admin-join", "&7[&a+&7]&c <player>");
        GroupsFile.get().addDefault("admin-leave", "&7[&c-&7]&c <player>");

        GroupsFile.get().addDefault("mod", "&7[&aMod&7]&a <player>&7 >>&f <message>");
        GroupsFile.get().addDefault("mod-join", "&7[&a+&7]&a <player>");
        GroupsFile.get().addDefault("mod-leave", "&7[&c-&7]&a <player>");

        GroupsFile.get().options().copyDefaults(true);
        GroupsFile.save();
    }

    public static SimpleChatOverhaul getInstance() {
        return instance;
    }
}
