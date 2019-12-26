package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import java.text.*;
import java.util.*;
import com.ubuntu.practice.tournament.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import org.bukkit.*;
import org.apache.commons.lang.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.kit.*;
import com.ubuntu.practice.util.*;

public class HostCommand implements CommandExecutor
{
    private uPractice plugin;
    private DecimalFormat SECONDS_FORMATTER;
    private static HashMap<UUID, Tournament> runningTournaments;
    
    public HostCommand(final uPractice plugin) {
        this.plugin = plugin;
        this.SECONDS_FORMATTER = new DecimalFormat("#0.0");
        HostCommand.runningTournaments = new HashMap<UUID, Tournament>();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return true;
        }
        if (!player.hasPermission("practice.commands.host")) {
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PERMISSION"));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /host start <kit>");
            player.sendMessage(ChatColor.RED + "Usage: /host list");
            return true;
        }
        if (args[0].toLowerCase().equalsIgnoreCase("start")) {
            final String kitName = args[1];
            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
            if (kit == null) {
                sender.sendMessage(ChatColor.RED + "This kit doesn't exist.");
                return true;
            }
            if (HostCommand.runningTournaments.containsKey(player.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are already running a tournament.");
                return true;
            }
            if (practicePlayer.getHostCooldown() != 0L && practicePlayer.getHostCooldown() > System.currentTimeMillis()) {
                final String timeLeft = this.getTimeLeft(practicePlayer);
                sender.sendMessage(ChatColor.RED + "You must wait " + timeLeft + " to start another tournament.");
                return true;
            }
            if (Tournament.getTournaments().size() >= 1) {
                sender.sendMessage(ChatColor.RED + "There is a tournament currently running.");
                return true;
            }
            this.plugin.getManagerHandler().getInventoryManager().setTournamentInventory(kit, true);
            player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getTournamentInventory());
        }
        else if (args[0].toLowerCase().equalsIgnoreCase("active") || args[0].toLowerCase().equalsIgnoreCase("list")) {
            if (!HostCommand.runningTournaments.containsKey(player.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not running a tournament.");
                return true;
            }
            final Tournament tournament = HostCommand.runningTournaments.get(player.getUniqueId());
            if (tournament != null) {
                player.sendMessage(ChatColor.AQUA + "(*) Tournament (" + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam() + ")");
                player.sendMessage(ChatColor.GRAY + "-> Kit: " + tournament.getDefaultKit().getName());
                player.sendMessage(ChatColor.GRAY + "-> Stage: " + ((tournament.getTournamentStage() == null) ? "Waiting for players" : StringUtils.capitalize(tournament.getTournamentStage().name().replace("_", " "))));
                player.sendMessage(ChatColor.GRAY + "-> Current Matches: " + tournament.getCurrentMatches().size());
                player.sendMessage(ChatColor.GRAY + "-> Total Teams: " + tournament.getTeams().size());
                player.sendMessage(ChatColor.GRAY + "-> Players Limit: " + tournament.getPlayersLimit());
            }
        }
        return true;
    }
    
    private String getTimeLeft(final PracticePlayer player) {
        if (player.getHostCooldown() - System.currentTimeMillis() >= 60000L) {
            return DateUtil.readableTime(player.getHostCooldown() - System.currentTimeMillis()).trim();
        }
        return this.SECONDS_FORMATTER.format((player.getHostCooldown() - System.currentTimeMillis()) / 1000.0f);
    }
    
    public static HashMap<UUID, Tournament> getRunningTournaments() {
        return HostCommand.runningTournaments;
    }
}
