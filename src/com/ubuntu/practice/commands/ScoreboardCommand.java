package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import com.ubuntu.practice.scoreboard.provider.sidebar.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.scoreboard.*;

public class ScoreboardCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public ScoreboardCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final boolean toggle = practicePlayer.isScoreboard();
        final PlayerBoard playerBoard = this.plugin.getManagerHandler().getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        if (toggle) {
            if (playerBoard != null) {
                playerBoard.setSidebarVisible(false);
            }
            practicePlayer.setScoreboard(false);
        }
        else {
            if (playerBoard != null) {
                playerBoard.setSidebarVisible(true);
            }
            practicePlayer.setScoreboard(true);
        }
        player.sendMessage(ChatColor.YELLOW + "You are now " + (practicePlayer.isScoreboard() ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable")) + ChatColor.YELLOW + " to see the scoreboard.");
        if (practicePlayer.getCurrentState() == PlayerState.LOBBY || practicePlayer.getCurrentState() == PlayerState.QUEUE) {
            if (playerBoard != null) {
                playerBoard.setDefaultSidebar(new LobbyScoreboardProvider(this.plugin), 2L);
            }
        }
        else if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && playerBoard != null) {
            playerBoard.setDefaultSidebar(new DuelScoreboardProvider(this.plugin), 2L);
        }
        return true;
    }
}
