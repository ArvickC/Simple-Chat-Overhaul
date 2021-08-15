package me.mystc.simplechatoverhaul.Commands.TabCompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessageTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {
            ArrayList<String> tab = new ArrayList<>();
            for(Player p : Bukkit.getOnlinePlayers()) {
                tab.add(p.getName());
            }
            return tab;
        }

        return null;
    }
}
