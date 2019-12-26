package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.tournament.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.party.*;
import java.util.*;

public class DuelCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public DuelCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /duel <player>");
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return false;
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
        final Player target;
        if ((target = this.plugin.getServer().getPlayer(args[0])) == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }
        if (target.getName().equals(player.getName())) {
            player.sendMessage(Messages.CANNOT_DUEL_YOURSELF);
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY || !practiceTarget.getSettings().isDuelRequests()) {
            player.sendMessage(Messages.PLAYER_BUSY);
            return true;
        }
        if (this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(target) && this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(target, player)) {
            player.sendMessage(Messages.WAIT_SENDING_DUEL);
            return true;
        }
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null) {
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_LEADER"));
                return true;
            }
            if (targetParty == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_IN_PARTY"));
                return true;
            }
            if (!targetParty.getLeader().equals(target.getUniqueId())) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PLAYER_NOT_LEADER"));
                return true;
            }
            if (targetParty.getPartyState() == PartyState.DUELING) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_BUSY"));
                return true;
            }
        }
        else if (targetParty != null) {
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PLAYER_IN_PARTY"));
            return true;
        }
        final UUID uuid = player.getUniqueId();
        this.plugin.getManagerHandler().getInventoryManager().setSelectingDuel(uuid, target.getUniqueId());
        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRequestInventory());
        return true;
    }
}
