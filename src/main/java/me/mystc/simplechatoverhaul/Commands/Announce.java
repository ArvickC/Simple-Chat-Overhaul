package me.mystc.simplechatoverhaul.Commands;

import me.mystc.simplechatoverhaul.SimpleChatOverhaul;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Announce implements CommandExecutor {
    // Var
    String prefix = SimpleChatOverhaul.prefix;
    String noPerms = SimpleChatOverhaul.noPermissionMessage;
    String inputErr = SimpleChatOverhaul.announceCommandInputError;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!SimpleChatOverhaul.getInstance().getConfig().getBoolean("announce.enable-announce")) {
            ChatColor.translateAlternateColorCodes('&', prefix + "&cThat isn't enabled!");
            return false;
        }
        if(!sender.hasPermission("chat.announce")) {
            ChatColor.translateAlternateColorCodes('&', prefix + noPerms);
            return false;
        }
        if(args == null || args.length <=0) {
            ChatColor.translateAlternateColorCodes('&', prefix + inputErr);
            return false;
        }

        // Get message
        String message;
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<args.length;i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        message = sb.toString();

        // Set format
        String format = SimpleChatOverhaul.getInstance().getConfig().getString("announce.announce-format");
        format = format.replaceAll("<message>", message);

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(SimpleChatOverhaul.getInstance().getConfig().getBoolean("announce.play-sound")) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1f);
            }
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', format));
        }

        return false;
    }
}
