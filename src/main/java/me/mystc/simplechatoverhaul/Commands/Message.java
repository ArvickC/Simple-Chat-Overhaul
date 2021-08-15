package me.mystc.simplechatoverhaul.Commands;

import me.mystc.simplechatoverhaul.SimpleChatOverhaul;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Vars
        String prefix = SimpleChatOverhaul.prefix;
        String noPerms = SimpleChatOverhaul.noPermissionMessage;
        String playerError = SimpleChatOverhaul.playerError;
        String mCE = SimpleChatOverhaul.messageCommandInputError;
        String mCNOE = SimpleChatOverhaul.messageCommandNoOnlineError;

        String senderFormat = SimpleChatOverhaul.getInstance().getConfig().getString("message-command-format-sender");
        String receiverFormat = SimpleChatOverhaul.getInstance().getConfig().getString("message-command-format-receiver");

        // Checks
        if(!sender.hasPermission("chat.message")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + noPerms));
            return false;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + playerError));
            return false;
        }
        if(args == null || args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + mCE));
            return false;
        }

        // NO GUI
        // Checks
        if(!(args.length >= 2)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + mCE));
            return false;
        }
        if(Bukkit.getPlayer(args[0]).equals(null) || !Bukkit.getPlayer(args[0]).isOnline()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + mCNOE));
            return false;
        }

        // Var
        Player playerSender = (Player)sender;
        Player playerReceiver = Bukkit.getPlayer(args[0]);
        String message;
        StringBuffer sb = new StringBuffer();

        for(int i=1;i<args.length;i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        message = sb.toString();

        senderFormat = senderFormat.replaceAll("<player>", playerReceiver.getName());
        senderFormat = senderFormat.replaceAll("<sender>", playerSender.getName());
        senderFormat = senderFormat.replaceAll("<message>", message);

        receiverFormat = receiverFormat.replaceAll("<player>", playerReceiver.getName());
        receiverFormat = receiverFormat.replaceAll("<sender>", playerSender.getName());
        receiverFormat = receiverFormat.replaceAll("<message>", message);

        playerReceiver.sendMessage(ChatColor.translateAlternateColorCodes('&', receiverFormat));
        playerSender.sendMessage(ChatColor.translateAlternateColorCodes('&', senderFormat));

        if(SimpleChatOverhaul.replyPlayers.containsKey(playerSender)) {
            SimpleChatOverhaul.replyPlayers.replace(playerSender, playerReceiver);
        } else {
            SimpleChatOverhaul.replyPlayers.put(playerSender, playerReceiver);
        }
        if(SimpleChatOverhaul.replyPlayers.containsKey(playerReceiver)) {
            SimpleChatOverhaul.replyPlayers.replace(playerReceiver, playerSender);
        } else {
            SimpleChatOverhaul.replyPlayers.put(playerReceiver, playerSender);
        }

        return false;
    }
}
