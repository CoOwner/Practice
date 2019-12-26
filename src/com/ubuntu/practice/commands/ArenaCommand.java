package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.util.*;
import net.md_5.bungee.api.*;
import com.ubuntu.practice.arena.*;

public class ArenaCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public ArenaCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] commandArgs) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (commandArgs.length == 0) {
            player.sendMessage("Unknown Command.");
            return true;
        }
        if (!player.hasPermission("practice.commands.arena")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }
        if (commandArgs[0].equalsIgnoreCase("create")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena create <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            if (this.plugin.getManagerHandler().getArenaManager().getArena(arenaName) != null) {
                player.sendMessage(ChatColor.RED + "This arena already exists!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().createArena(arenaName);
            player.sendMessage(ChatColor.YELLOW + "Successfully created the arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("set1location")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena set1location <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setFirstTeamLocation(player.getLocation());
            player.sendMessage(ChatColor.YELLOW + "Successfully set the first team's location in arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("set2location")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena set2location <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setSecondTeamLocation(player.getLocation());
            player.sendMessage(ChatColor.YELLOW + "Successfully set the second team's location in arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("enable") || commandArgs[1].equalsIgnoreCase("disable")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena enable <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(arenaName);
            arena.setEnabled(!arena.isEnabled());
            player.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (arena.isEnabled() ? "enabled" : "disabled") + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("remove")) {
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /arena remove <arena name>");
                return true;
            }
            final String arenaName = commandArgs[1];
            if (this.plugin.getManagerHandler().getArenaManager().getArena(arenaName) == null) {
                player.sendMessage(ChatColor.RED + "This arena does not exist!");
                return true;
            }
            this.plugin.getManagerHandler().getArenaManager().destroyArena(arenaName);
            player.sendMessage(ChatColor.YELLOW + "Arena " + ChatColor.GREEN + arenaName + ChatColor.YELLOW + " has been removed!");
        }
        else {
            player.sendMessage("Unknown Command.");
        }
        return true;
    }
}
