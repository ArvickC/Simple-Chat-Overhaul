package me.mystc.simplechatoverhaul.Commands;

import me.mystc.simplechatoverhaul.Files.MessagesFile;
import me.mystc.simplechatoverhaul.SimpleChatOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Var
        String prefix = SimpleChatOverhaul.prefix;
        String noPerms = SimpleChatOverhaul.noPermissionMessage;
        String reloaded = SimpleChatOverhaul.pluginReloaded;

        // Checks
        if(!sender.hasPermission("chat.reload")) {
            sender.sendMessage(prefix + noPerms);
        }

        SimpleChatOverhaul.getInstance().reloadConfig();
        MessagesFile.reload();

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + reloaded));

        return false;
    }
}
