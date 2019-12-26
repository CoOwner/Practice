package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.apache.commons.lang.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

public class PingCommand implements CommandExecutor
{
    @SuppressWarnings("unused")
	private uPractice plugin;
    
    public PingCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        Player toCheck;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /ping <player>");
                return true;
            }
            toCheck = (Player)sender;
        }
        else {
            toCheck = Bukkit.getPlayer(StringUtils.join((Object[])args));
        }
        if (toCheck == null) {
            sender.sendMessage(ChatColor.RED + "No player named '" + StringUtils.join((Object[])args) + "' found online.");
            return true;
        }
        sender.sendMessage("§d" + toCheck.getName() + (toCheck.getName().endsWith("s") ? "'" : "'s") + ChatColor.YELLOW + " current ping is " + "§d" + this.getPing(toCheck) + "ms" + ChatColor.GRAY + ".");
        if (sender instanceof Player && !toCheck.getName().equals(sender.getName())) {
            final Player senderPlayer = (Player)sender;
            sender.sendMessage("§e" + "Ping difference: " + "§d" + (Math.max(this.getPing(senderPlayer), this.getPing(toCheck)) - Math.min(this.getPing(senderPlayer), this.getPing(toCheck))) + "ms" + ChatColor.GRAY + ".");
        }
        return true;
    }
    
    private int getPing(final Player player) {
        final int ping = ((CraftPlayer)player).getHandle().ping;
        if (ping >= 100) {
            return ping - 20;
        }
        if (ping >= 50) {
            return ping - 9;
        }
        if (ping >= 20) {
            return ping - 4;
        }
        return ping;
    }
}
