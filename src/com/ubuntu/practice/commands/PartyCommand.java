package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.tournament.*;
import com.ubuntu.practice.util.*;
import org.bukkit.*;
import com.ubuntu.practice.player.*;
import java.util.*;
import com.ubuntu.practice.party.*;

public class PartyCommand implements CommandExecutor
{
    private String[] HELP_COMMAND;
    private uPractice plugin;
    private HashMap<UUID, Long> delay;
    
    public PartyCommand(final uPractice plugin) {
        this.HELP_COMMAND = new String[] { ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Commands:", ChatColor.GOLD + "(*) /party help " + ChatColor.GRAY + "- Displays the help menu", ChatColor.GOLD + "(*) /party create " + ChatColor.GRAY + "- Creates a party instance", ChatColor.GOLD + "(*) /party leave " + ChatColor.GRAY + "- Leave your current party", ChatColor.GOLD + "(*) /party info " + ChatColor.GRAY + "- Displays your party information", ChatColor.GOLD + "(*) /party join (player) " + ChatColor.GRAY + "- Join a party (invited or unlocked)", "", ChatColor.RED + "Leader Commands:", ChatColor.GOLD + "(*) /party open " + ChatColor.GRAY + "- Open your party for others to join", ChatColor.GOLD + "(*) /party lock " + ChatColor.GRAY + "- Lock your party for others to join", ChatColor.GOLD + "(*) /party invite (player) " + ChatColor.GRAY + "- Invites a player to your party", ChatColor.GOLD + "(*) /party kick (player) " + ChatColor.GRAY + "- Kicks a player from your party", ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
        this.delay = new HashMap<UUID, Long>();
        this.plugin = plugin;
    }
    
    @SuppressWarnings("unused")
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length == 0) {
        	player.sendMessage(this.HELP_COMMAND);
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
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
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(this.HELP_COMMAND);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "ALREADY_IN_PARTY"));
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().createParty(player.getUniqueId(), player.getName());
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_CREATE"));
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(player);
            this.plugin.getManagerHandler().getInventoryManager().addParty(party);
        }
        else if (args[0].equalsIgnoreCase("info")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PARTY"));
                return true;
            }
            final Player leader = this.plugin.getServer().getPlayer(party.getLeader());
            final StringJoiner members = new StringJoiner(", ");
            for (final UUID memberUUID : party.getMembers()) {
                final Player member = this.plugin.getServer().getPlayer(memberUUID);
                members.add(member.getName());
            }
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_INFO_COMMAND").replace("%party_leader%", leader.getName()).replace("%party_size%", String.valueOf(party.getMembers().size())).replace("%party_members%", members.toString()).replace("%party_state%", party.isOpen() ? "Open" : "Locked"));
        }
        else if (args[0].equalsIgnoreCase("leave")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PARTY"));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "DISBANDED_PARTY"));
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                for (final UUID member2 : party.getMembers()) {
                    final Player pLayer = this.plugin.getServer().getPlayer(member2);
                    player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "LEADER_DISBANDED_PARTY"));
                    final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                    if (ppLayer.getCurrentState() != PlayerState.LOBBY) {
                        continue;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(pLayer);
                }
                this.plugin.getManagerHandler().getInventoryManager().delParty(party);
            }
            else {
                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, Lang.getLang().getLocalized((CommandSender)player, "PLAYER_LEAVE_PARTY").replace("%player%", player.getName()));
                this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
            }
        }
        else if (args[0].equalsIgnoreCase("open")) {
            final Player p = (Player)sender;
            if (this.check(p.getPlayer().getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You will be able to use this command again in 2 minutes");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PARTY"));
                return true;
            }
            if (!sender.hasPermission("practice.donator.party")) {
                party.setOpen(false);
                sender.sendMessage(ChatColor.RED + "Buy a rank from /buy to use this command.");
            }
            else {
                final UtilActionMessage actionMessage = new UtilActionMessage();
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d" + sender.getName() + " &ehas just opened his party! Type &d/party join " + sender.getName()));
                this.delay.put(p.getPlayer().getUniqueId(), System.currentTimeMillis() + 120000L);
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (party.isOpen()) {
                    player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_OPENED_ALREADY"));
                    return true;
                }
                party.setOpen(true);
            }
            else {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_LEADER"));
            }
        }
        else if (args[0].equalsIgnoreCase("lock")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PARTY"));
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (!party.isOpen()) {
                    player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_LOCKED_ALREADY"));
                    return true;
                }
                party.setOpen(false);
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_LOCKED"));
            }
            else {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_LEADER"));
            }
        }
        else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party join <player>");
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "ALREADY_IN_PARTY"));
                return true;
            }
            final Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
            if (party2 == null || !party2.getLeader().equals(target.getUniqueId())) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_EXISTS"));
                return true;
            }
            if (!party2.isOpen()) {
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(player)) {
                    player.sendMessage(Messages.NO_REQUESTS_FOUND);
                    return true;
                }
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(player, target)) {
                    player.sendMessage(Messages.NO_REQUESTS_FOUND);
                    return true;
                }
                this.plugin.getManagerHandler().getRequestManager().removePartyRequest(player, target);
            }
            if (party2.getMembers().size() >= 15) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_FULL"));
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().joinParty(party2.getLeader(), player.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, Lang.getLang().getLocalized((CommandSender)player, "PLAYER_JOIN_PARTY").replace("%player%", player.getName()));
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_JOIN"));
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party2);
        }
        else if (args[0].equalsIgnoreCase("kick")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party kick <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PARTY"));
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_LEADER"));
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if (party.getLeader() == target2.getUniqueId()) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "CANNOT_KICK_LEADER"));
                return true;
            }
            if (!party.getMembers().contains(target2.getUniqueId())) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PLAYER_NOT_IN_PARTY"));
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().leaveParty(target2.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party, Lang.getLang().getLocalized((CommandSender)player, "PLAYER_KICK_PARTY").replace("%player%", target2.getName()));
            target2.sendMessage(ChatColor.YELLOW + "You were kicked from the party.");
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_KICKED"));
            final PracticePlayer ptarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target2);
            if (ptarget.getCurrentState() == PlayerState.LOBBY) {
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(target2);
            }
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PLAYER_KICK_PARTY_TWO").replace("%player%", target2.getName()));
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
        }
        else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party invite <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NO_PARTY"));
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "NOT_LEADER"));
                return true;
            }
            if (party.isOpen()) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_IS_OPENED"));
                return true;
            }
            if (party.getMembers().size() >= 15) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_FULL"));
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(target2.getUniqueId()) != null) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PLAYER_IN_PARTY"));
                return true;
            }
            if (this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(target2) && this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(target2, player)) {
                player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "PARTY_INVITATION"));
                return true;
            }
            player.sendMessage(ChatColor.YELLOW + "Sent a party request to " + "§d" + target2.getName() + ChatColor.YELLOW + "!");
            this.plugin.getManagerHandler().getRequestManager().addPartyRequest(target2, player);
            final UtilActionMessage actionMessage2 = new UtilActionMessage();
            actionMessage2.addText("§d" + player.getName() + ChatColor.YELLOW + " has invited you to their party! ");
            actionMessage2.addText("                                 " + ChatColor.RED + "[Click here to accept]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/party join " + player.getName());
            actionMessage2.sendToPlayer(target2);
        }
        else {
            player.sendMessage(this.HELP_COMMAND);
        }
        return true;
    }
    
    private boolean check(final UUID uuid) {
        return this.delay.containsKey(uuid) && this.delay.get(uuid) >= System.currentTimeMillis();
    }
}
