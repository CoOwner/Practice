package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.tournament.*;
import org.bukkit.*;
import com.ubuntu.practice.player.*;

public class SpectateCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public SpectateCommand(final uPractice plugin) {
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
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (!tournament.isInTournament(player)) {
                    continue;
                }
                player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
                return true;
            }
        }
        if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
            this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player, true);
        }
        if (args.length == 0) {
            this.plugin.getManagerHandler().getSpectatorManager().toggleSpectatorMode(player);
            player.sendMessage(ChatColor.YELLOW + "You are now in spectator mode.");
        }
        else if (args.length == 1) {
            final Player target = this.plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
            if (practiceTarget.getCurrentState() != PlayerState.FIGHTING) {
                player.sendMessage(Messages.REQUESTED_PLAYER_NOT_IN_FIGHT);
                return true;
            }
            if (!this.plugin.getManagerHandler().getSpectatorManager().isSpectatorMode(player)) {
                this.plugin.getManagerHandler().getSpectatorManager().toggleSpectatorMode(player);
            }
            this.plugin.getManagerHandler().getSpectatorManager().addSpectator(player, target);
        }
        else {
            player.sendMessage(ChatColor.RED + "Usage: /spectate (player)");
        }
        return true;
    }
}
