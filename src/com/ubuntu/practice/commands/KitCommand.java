package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import org.bukkit.*;
import com.ubuntu.practice.kit.*;

public class KitCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public KitCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] commandArgs) {
        if (commandArgs.length == 0) {
            final Player player = (Player)sender;
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "INCORRECT_USAGE"));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, only players can execute this command!");
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.commands.kit")) {
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PERMISSION"));
            return true;
        }
        if (commandArgs[0].equalsIgnoreCase("create")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit create <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            if (this.plugin.getManagerHandler().getKitManager().getKit(kitName) != null) {
                sender.sendMessage(ChatColor.RED + "This kit already exists!");
                return true;
            }
            this.plugin.getManagerHandler().getKitManager().createKit(kitName);
            player.sendMessage(ChatColor.YELLOW + "Successfully created a kit with the name " + ChatColor.GREEN + kitName + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("combo")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit combo <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setCombo(!kit.isCombo());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (kit.isCombo() ? "combo mode" : "not combo mode") + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("ranked")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit ranked <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setRanked(!kit.isRanked());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (kit.isRanked() ? "ranked mode" : "not ranked mode") + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("editable")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit editable <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setEditable(!kit.isEditable());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (kit.isEditable() ? "editable" : "not editable") + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("builduhc")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit builduhc <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setBuilduhc(!kit.isBuilduhc());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (kit.isBuilduhc() ? "builduhc mode" : "not builduhcmode") + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("enable")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit enable <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setEnabled(!kit.isEnabled());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " is now " + ChatColor.GREEN + (kit.isEnabled() ? "enabled" : "disabled") + ChatColor.YELLOW + "!");
        }
        else if (commandArgs[0].equalsIgnoreCase("setinventory")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit setinventory <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setMainContents(player.getInventory().getContents());
            kit.setArmorContents(player.getInventory().getArmorContents());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " now has your inventory setup!");
        }
        else if (commandArgs[0].equalsIgnoreCase("retrieve")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit retrieve <kit name>");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            player.getInventory().setContents(kit.getMainContents());
            player.getInventory().setArmorContents(kit.getArmorContents());
            player.updateInventory();
            player.sendMessage(ChatColor.YELLOW + "You have retrieved kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + "'s inventory setup!");
        }
        else if (commandArgs[0].equalsIgnoreCase("seticon")) {
            if (commandArgs.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /kit seticon <kit name>");
                return true;
            }
            if (player.getItemInHand() == null) {
                sender.sendMessage(ChatColor.RED + "You need something in your hand!");
                return true;
            }
            final String kitName = commandArgs[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist!");
                return true;
            }
            kit.setIcon(player.getItemInHand());
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " now has an icon as the item in your hand!");
        }
        else {
            if (!commandArgs[0].equalsIgnoreCase("remove")) {
                player.sendMessage("Unknown Command.");
                return true;
            }
            if (commandArgs.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /kit remove <arena name>");
                return true;
            }
            final String kitName = commandArgs[1];
            if (this.plugin.getManagerHandler().getKitManager().getKit(kitName) == null) {
                player.sendMessage(ChatColor.RED + "This kit does not exist!");
                return true;
            }
            this.plugin.getManagerHandler().getKitManager().destroyKit(kitName);
            player.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.GREEN + kitName + ChatColor.YELLOW + " has been removed!");
        }
        this.plugin.getManagerHandler().getInventoryManager().setUnrankedInventory();
        this.plugin.getManagerHandler().getInventoryManager().setRankedInventory();
        this.plugin.getManagerHandler().getInventoryManager().setEditorInventory();
        this.plugin.getManagerHandler().getInventoryManager().setRequestInventory();
        this.plugin.getManagerHandler().getInventoryManager().setSplitFightInventory();
        this.plugin.getManagerHandler().getInventoryManager().setFfaPartyInventory();
        return true;
    }
}
