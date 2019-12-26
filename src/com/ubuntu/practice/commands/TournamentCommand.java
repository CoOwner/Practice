package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import org.bukkit.*;
import com.ubuntu.practice.tournament.*;
import org.apache.commons.lang.*;
import com.ubuntu.practice.kit.*;

public class TournamentCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public TournamentCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.donator.tournament")) {
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PERMISSION"));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /tournament start <kit>");
            player.sendMessage(ChatColor.RED + "Usage: /tournament stop <id>");
            player.sendMessage(ChatColor.RED + "Usage: /tournament forcestart <id>");
            player.sendMessage(ChatColor.RED + "Usage: /tournament active");
            return true;
        }
        if (args[0].toLowerCase().equalsIgnoreCase("start")) {
            if (!sender.hasPermission("practice.donator.tournament")) {
                ((Player)sender).sendMessage(ChatColor.RED + "No permissions.");
                return true;
            }
            final String kitName = args[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist.");
                return true;
            }
            this.plugin.getManagerHandler().getInventoryManager().setTournamentInventory(kit, false);
            player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getTournamentInventory());
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("practice.commands.tournament.stop")) {
                ((Player)sender).sendMessage(ChatColor.RED + "No permissions.");
                return true;
            }
            final int id = Integer.parseInt(args[1]);
            if (Tournament.getTournaments().size() > id) {
                sender.sendMessage(ChatColor.RED + "Available tournaments to stop:");
                int count = 1;
                for (final Tournament tournament : Tournament.getTournaments()) {
                    sender.sendMessage(ChatColor.RED + "ID: " + count + ChatColor.GRAY + " (" + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam() + ")");
                    ++count;
                }
                return true;
            }
            final Tournament tournament2 = Tournament.getTournaments().get(id - 1);
            if (tournament2 == null) {
                sender.sendMessage(ChatColor.RED + "That tournament id doesn't exist.");
                return true;
            }
            tournament2.stopTournament();
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("forcestart")) {
            if (!sender.hasPermission("practice.commands.tournament.forcestart")) {
                ((Player)sender).sendMessage(ChatColor.RED + "No permissions.");
                return true;
            }
            final int id = Integer.parseInt(args[1]);
            final Tournament tournament2 = Tournament.getTournaments().get(id - 1);
            if (tournament2 == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "WRONG_ID_TOURNAMENT"));
                return true;
            }
            tournament2.setForceStarted(true);
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("active") || args[0].toLowerCase().equalsIgnoreCase("list")) {
            if (!sender.hasPermission("practice.commands.tournament.active")) {
                ((Player)sender).sendMessage(ChatColor.RED + "No permissions.");
                return true;
            }
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_TOURNAMENT"));
                return true;
            }
            for (final Tournament tournament2 : Tournament.getTournaments()) {
                player.sendMessage(ChatColor.GOLD + "(*) Tournament (" + tournament2.getMaximumPerTeam() + "v" + tournament2.getMaximumPerTeam() + ")");
                player.sendMessage(ChatColor.GRAY + "-> Kit: " + tournament2.getDefaultKit().getName());
                player.sendMessage(ChatColor.GRAY + "-> Stage: " + ((tournament2.getTournamentStage() == null) ? "Waiting for players" : StringUtils.capitalize(tournament2.getTournamentStage().name().replace("_", " "))));
                player.sendMessage(ChatColor.GRAY + "-> Current Matches: " + tournament2.getCurrentMatches().size());
                player.sendMessage(ChatColor.GRAY + "-> Total Teams: " + tournament2.getTeams().size());
                player.sendMessage(ChatColor.GRAY + "-> Players Limit: " + tournament2.getPlayersLimit());
            }
        }
        return true;
    }
}
