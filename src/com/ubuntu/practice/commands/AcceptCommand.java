package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.tournament.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.events.*;
import org.bukkit.event.*;
import com.ubuntu.practice.player.*;
import java.util.*;
import com.ubuntu.practice.duel.*;
import com.ubuntu.practice.kit.*;
import com.ubuntu.practice.party.*;

public class AcceptCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public AcceptCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "UNABLE_ACCEPT"));
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
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(player)) {
            player.sendMessage(Messages.NO_REQUESTS_FOUND);
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(player, target)) {
            player.sendMessage(Messages.NO_REQUESTS_FOUND);
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.PLAYER_BUSY);
            return true;
        }
        final DuelRequest request = this.plugin.getManagerHandler().getRequestManager().getDuelRequest(player, target);
        if (request == null) {
            player.sendMessage(Messages.NO_REQUESTS_FOUND);
            return true;
        }
        this.plugin.getManagerHandler().getRequestManager().removeDuelRequest(player, target);
        player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "ACCEPTED_DUEL").replace("%player%", target.getName()));
        final String kitName = request.getKitName();
        final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
        final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
        final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
        firstTeam.add(player.getUniqueId());
        secondTeam.add(target.getUniqueId());
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null && targetParty != null) {
            for (final UUID member : party.getMembers()) {
                firstTeam.add(member);
            }
            for (final UUID member : targetParty.getMembers()) {
                secondTeam.add(member);
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, party.getLeader(), targetParty.getLeader(), firstTeam, secondTeam, false));
        }
        else {
            if ((party != null && targetParty == null) || (targetParty != null && party == null)) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PLAYER_IN_PARTY"));
                return true;
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, null, null, firstTeam, secondTeam, false));
        }
        return true;
    }
}
