package me.mystc.simplechatoverhaul.Commands;

import me.mystc.simplechatoverhaul.SimpleChatOverhaul;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reply implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Var
        String prefix = SimpleChatOverhaul.prefix;
        String noPerms = SimpleChatOverhaul.noPermissionMessage;
        String playerError = SimpleChatOverhaul.playerError;
        String noReplyPlayer = SimpleChatOverhaul.replyCommandPlayerError;
        String replyInputError = SimpleChatOverhaul.replyCommandInputError;
        String mCNOE = SimpleChatOverhaul.messageCommandNoOnlineError;

        String senderFormat = SimpleChatOverhaul.getInstance().getConfig().getString("message-command-format-sender");
        String receiverFormat = SimpleChatOverhaul.getInstance().getConfig().getString("message-command-format-receiver");

        // Checks
        if(!sender.hasPermission("chat.reply")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + noPerms));
            return false;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + playerError));
            return false;
        }

        Player p = (Player)sender;
        if(!SimpleChatOverhaul.replyPlayers.containsKey(p)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + noReplyPlayer));
            return false;
        }
        if(SimpleChatOverhaul.replyPlayers.get(p) == null || !SimpleChatOverhaul.replyPlayers.get(p).isOnline()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + mCNOE));
            return false;
        }
        if(args == null || args.length <= 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + replyInputError));
            return false;
        }

        Player pReceiver = SimpleChatOverhaul.replyPlayers.get(p);

        String message;
        StringBuffer sb = new StringBuffer();

        for(int i=0;i<args.length;i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        message = sb.toString();

        senderFormat = senderFormat.replaceAll("<player>", pReceiver.getName());
        senderFormat = senderFormat.replaceAll("<sender>", p.getName());
        senderFormat = senderFormat.replaceAll("<message>", message);

        receiverFormat = receiverFormat.replaceAll("<player>", pReceiver.getName());
        receiverFormat = receiverFormat.replaceAll("<sender>", p.getName());
        receiverFormat = receiverFormat.replaceAll("<message>", message);

        pReceiver.sendMessage(ChatColor.translateAlternateColorCodes('&', receiverFormat));
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', senderFormat));

        if(SimpleChatOverhaul.replyPlayers.containsKey(p)) {
            SimpleChatOverhaul.replyPlayers.replace(p, pReceiver);
        } else {
            SimpleChatOverhaul.replyPlayers.put(p, pReceiver);
        }
        if(SimpleChatOverhaul.replyPlayers.containsKey(pReceiver)) {
            SimpleChatOverhaul.replyPlayers.replace(pReceiver, p);
        } else {
            SimpleChatOverhaul.replyPlayers.put(pReceiver, p);
        }

        return false;
    }
}
