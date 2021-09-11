package me.mystc.simplechatoverhaul.Commands;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.mystc.simplechatoverhaul.SimpleChatOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Staffchat implements CommandExecutor {
    // Var
    String prefix = SimpleChatOverhaul.prefix;
    String noPerms = SimpleChatOverhaul.noPermissionMessage;
    String playerErr = SimpleChatOverhaul.playerError;
    HashMap<Player, Boolean> toggle = SimpleChatOverhaul.staffChatToggle;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chat.staff")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + noPerms));
            return false;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + playerErr));
            return false;
        }

        Player p = (Player)sender;

        if(!toggle.containsKey(p)) {
            SimpleChatOverhaul.staffChatToggle.put(p, true);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&eStaff Chat&a On"));
        } else if(!toggle.get(p)) {
            SimpleChatOverhaul.staffChatToggle.replace(p, true);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&eStaff Chat&a On"));
        } else {
            SimpleChatOverhaul.staffChatToggle.replace(p, false);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&eStaff Chat&c Off"));
        }

        return false;
    }
}
