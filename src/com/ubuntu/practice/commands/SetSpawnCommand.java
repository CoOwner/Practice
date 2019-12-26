package com.ubuntu.practice.commands;

import org.bukkit.event.*;
import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.util.*;
import org.bukkit.*;

public class SetSpawnCommand implements CommandExecutor, Listener
{
    private uPractice plugin;
    
    public SetSpawnCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PERMISSION"));
            return true;
        }
        this.plugin.getConfig().set("spawn", (Object)LocationSerializer.serializeLocation(player.getLocation()));
        this.plugin.saveConfig();
        this.plugin.reloadConfig();
        this.plugin.setSpawn(player.getLocation());
        player.sendMessage(ChatColor.YELLOW + "Spawn is now set.");
        return true;
    }
}
