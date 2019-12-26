package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.lang.*;
import org.bukkit.*;
import com.ubuntu.practice.runnables.other.*;
import org.bukkit.plugin.*;
import com.ubuntu.practice.player.*;
import java.util.*;
import com.ubuntu.practice.tournament.*;

public class LeaveCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public LeaveCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return true;
        }
        if (args.length == 0) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_TOURNAMENT"));
                return true;
            }
            Tournament tournament = Tournament.getTournaments().get(0);
            if (tournament.getTotalPlayersInTournament() == tournament.getPlayersLimit()) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "FULL_TOURNAMENT"));
                return true;
            }
            final List<Tournament> check = new ArrayList<Tournament>();
            for (final Tournament tournaments1 : Tournament.getTournaments()) {
                check.add(tournaments1);
            }
            for (final Tournament tournaments2 : Tournament.getTournaments()) {
                if (!tournaments2.isInTournament(player)) {
                    if (check.size() == 0) {
                        player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_IN_TOURNAMENT"));
                        return true;
                    }
                    check.remove(tournaments2);
                }
                else {
                    tournament = tournaments2;
                }
            }
            TournamentTeam tournamentTeam = tournament.getTournamentTeam(player);
            tournamentTeam = tournament.getTournamentTeam(player);
            if (tournamentTeam == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_IN_TOURNAMENT"));
                return true;
            }
            if (tournament.getTournamentMatch(player) != null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "CANNOT_LEAVE_IN_MATCH"));
                player.sendMessage(ChatColor.RED + "You can't leave during a match.");
                return true;
            }
            final String reason = (tournamentTeam.getPlayers().size() > 1) ? "Someone in your party left the tournament" : "You left the tournament";
            tournamentTeam.sendMessage(ChatColor.RED + "You have been removed from the tournament.");
            tournamentTeam.sendMessage(ChatColor.RED + "Reason: " + ChatColor.GRAY + reason);
            tournament.getCurrentQueue().remove(tournamentTeam);
            tournament.getTeams().remove(tournamentTeam);
            this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
        }
        return true;
    }
}
