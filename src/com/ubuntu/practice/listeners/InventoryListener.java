package com.ubuntu.practice.listeners;

import com.ubuntu.practice.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.kit.*;
import com.ubuntu.practice.events.*;
import com.ubuntu.practice.duel.*;
import org.bukkit.*;
import com.ubuntu.practice.commands.*;
import com.ubuntu.practice.lang.*;
import org.bukkit.command.*;
import com.ubuntu.practice.tournament.*;
import com.ubuntu.practice.runnables.other.*;
import org.bukkit.plugin.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.party.*;
import com.ubuntu.practice.manager.managers.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import java.util.*;

public class InventoryListener implements Listener
{
    private uPractice plugin;
    
    public InventoryListener(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInvClick(final InventoryClickEvent e) {
        final Player player = (Player)e.getWhoClicked();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final String invTitle = e.getInventory().getTitle().toLowerCase();
        if (invTitle.contains("inventory")) {
            e.setCancelled(true);
            return;
        }
        final ItemStack itemClicked = e.getCurrentItem();
        final Inventory inventory = e.getInventory();
        if (practicePlayer.getCurrentState() == PlayerState.LOBBY || practicePlayer.getCurrentState() == PlayerState.QUEUE) {
            e.setCancelled(true);
            if (itemClicked == null || !itemClicked.hasItemMeta() || !itemClicked.getItemMeta().hasDisplayName()) {
                e.setCancelled(true);
                return;
            }
            if (invTitle.contains("party events")) {
                e.setCancelled(true);
                final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                if (party == null) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You must be in a party in order for this feature to work.");
                    return;
                }
                switch (itemClicked.getType()) {
                    case GOLD_SWORD: {
                        if (!party.getLeader().equals(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You are not the leader of this party!");
                            break;
                        }
                        if (party.getPartyState() == PartyState.DUELING) {
                            player.sendMessage(ChatColor.RED + "Your party is currently busy and cannot fight");
                            break;
                        }
                        if (party.getMembers().size() == 0) {
                            player.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
                            break;
                        }
                        player.closeInventory();
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getSplitFightInventory());
                        break;
                    }
                    case GOLD_AXE: {
                        if (!party.getLeader().equals(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You are not the leader of this party!");
                            break;
                        }
                        if (party.getPartyState() == PartyState.DUELING) {
                            player.sendMessage(ChatColor.RED + "Your party is currently busy and cannot fight");
                            break;
                        }
                        if (party.getMembers().size() == 0) {
                            player.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
                            break;
                        }
                        player.closeInventory();
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getFfaPartyInventory());
                        break;
                    }
                }
            }
            if (invTitle.contains("unranked queue")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final String kitName = kit.getName();
                    final QueueManager queueManager = this.plugin.getManagerHandler().getQueueManager();
                    final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    if (party2 != null) {
                        if (party2.getSize() != 2) {
                            player.closeInventory();
                            player.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
                            return;
                        }
                        if (queueManager.isUnrankedPartyQueueEmpty(kitName)) {
                            player.closeInventory();
                            queueManager.addToPartyUnrankedQueue(kitName, player.getUniqueId());
                            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, ChatColor.YELLOW + "Your party has been added to the " + "§d" + "Unranked 2v2 " + kitName + ChatColor.YELLOW + " queue!");
                            practicePlayer.setCurrentState(PlayerState.QUEUE);
                            final PracticePlayer partyMember = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(party2.getMembers().get(0));
                            partyMember.setCurrentState(PlayerState.QUEUE);
                            final Player partyPlayer = this.plugin.getServer().getPlayer(partyMember.getUUID());
                            partyPlayer.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            partyPlayer.updateInventory();
                            player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            player.updateInventory();
                        }
                        else {
                            player.closeInventory();
                            final UUID targetLeaderUUID = queueManager.getQueuedForPartyUnrankedQueue(kitName);
                            queueManager.removePartyFromPartyUnrankedQueue(kitName);
                            final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
                            final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
                            final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(targetLeaderUUID);
                            firstTeam.add(player.getUniqueId());
                            firstTeam.add(party2.getMembers().get(0));
                            secondTeam.add(targetLeaderUUID);
                            secondTeam.add(targetParty.getMembers().get(0));
                            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, player.getUniqueId(), targetLeaderUUID, firstTeam, secondTeam, false));
                        }
                    }
                    else if (queueManager.isUnrankedQueueEmpty(kitName)) {
                        player.closeInventory();
                        queueManager.addToUnrankedQueue(kitName, player.getUniqueId());
                        player.sendMessage(ChatColor.YELLOW + "You have been added to the " + "§d" + "Unranked " + kitName + ChatColor.YELLOW + " queue.");
                        practicePlayer.setCurrentState(PlayerState.QUEUE);
                        player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                        player.updateInventory();
                    }
                    else {
                        player.closeInventory();
                        final UUID queuedUuid = queueManager.getQueuedForUnrankedQueue(kitName);
                        queueManager.removePlayerFromUnrankedQueue(kitName);
                        final ArrayList<UUID> firstTeam = new ArrayList<UUID>();
                        final ArrayList<UUID> secondTeam = new ArrayList<UUID>();
                        firstTeam.add(queuedUuid);
                        secondTeam.add(player.getUniqueId());
                        this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, null, null, firstTeam, secondTeam, false));
                    }
                }
            }
            else if (invTitle.contains("ranked queue")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final String kitName = kit.getName();
                    final QueueManager queueManager = this.plugin.getManagerHandler().getQueueManager();
                    final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    if (party2 != null) {
                        if (party2.getSize() != 2) {
                            player.closeInventory();
                            player.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
                            return;
                        }
                        Map<String, Integer> partyEloMap = practicePlayer.getPartyEloMap().get(party2.getMembers().get(0));
                        final PracticePlayer memberPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(party2.getMembers().get(0));
                        if (partyEloMap == null) {
                            practicePlayer.addPartyElo(party2.getMembers().get(0), kitName, 1000);
                            memberPracPlayer.addPartyElo(practicePlayer.getUUID(), kitName, 1000);
                            partyEloMap = practicePlayer.getPartyEloMap().get(party2.getMembers().get(0));
                        }
                        final int partyElo = partyEloMap.get(kitName);
                        if (queueManager.isRankedPartyQueueEmpty(kitName)) {
                            player.closeInventory();
                            queueManager.addToPartyRankedQueue(kitName, player.getUniqueId());
                            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, ChatColor.YELLOW + "Your party has been added to the " + "§d" + "Ranked 2v2 " + kitName + ChatColor.YELLOW + " queue with " + ChatColor.GREEN + partyElo + " elo" + ChatColor.YELLOW + "!");
                            practicePlayer.setCurrentState(PlayerState.QUEUE);
                            final PracticePlayer partyMember2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(party2.getMembers().get(0));
                            partyMember2.setCurrentState(PlayerState.QUEUE);
                            final Player partyPlayer2 = this.plugin.getServer().getPlayer(partyMember2.getUUID());
                            partyPlayer2.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            partyPlayer2.updateInventory();
                            player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            player.updateInventory();
                        }
                        else {
                            player.closeInventory();
                            for (final UUID queuedUuid2 : queueManager.getQueuedForPartyRankedQueue(kitName)) {
                                final PracticePlayer queuedPracticePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(queuedUuid2);
                                final Party queuedParty = this.plugin.getManagerHandler().getPartyManager().getParty(queuedUuid2);
                                final Map<String, Integer> queuedPartyEloMap = queuedPracticePlayer.getPartyEloMap().get(queuedParty.getMembers().get(0));
                                final int queuedPartyElo = queuedPartyEloMap.get(kitName);
                                final int diff = Math.abs(queuedPartyElo - partyElo);
                                if (diff >= 325) {
                                    continue;
                                }
                                this.plugin.getManagerHandler().getQueueManager().removePlayerFromPartyRankedQueue(kitName, queuedUuid2);
                                final ArrayList<UUID> firstTeam2 = new ArrayList<UUID>();
                                final ArrayList<UUID> secondTeam2 = new ArrayList<UUID>();
                                firstTeam2.add(player.getUniqueId());
                                firstTeam2.add(party2.getMembers().get(0));
                                secondTeam2.add(queuedUuid2);
                                secondTeam2.add(queuedParty.getMembers().get(0));
                                this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, true, player.getUniqueId(), queuedUuid2, firstTeam2, secondTeam2, false));
                                return;
                            }
                            queueManager.addToPartyRankedQueue(kitName, player.getUniqueId());
                            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, ChatColor.YELLOW + "Party has been added to the " + "§d" + "Ranked " + kitName + ChatColor.YELLOW + " queue. " + "§d" + "[" + partyElo + "]");
                            practicePlayer.setCurrentState(PlayerState.QUEUE);
                            final PracticePlayer partyMember2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(party2.getMembers().get(0));
                            partyMember2.setCurrentState(PlayerState.QUEUE);
                            final Player partyPlayer2 = this.plugin.getServer().getPlayer(partyMember2.getUUID());
                            partyPlayer2.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            partyPlayer2.updateInventory();
                            player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            player.updateInventory();
                        }
                    }
                    else {
                        final int playerElo = practicePlayer.getEloMap().get(kitName);
                        if (queueManager.isRankedQueueEmpty(kitName)) {
                            player.closeInventory();
                            queueManager.addToRankedQueue(kitName, player.getUniqueId());
                            player.sendMessage(ChatColor.YELLOW + "You have been added to the " + "§d" + "Ranked " + kitName + ChatColor.YELLOW + " queue. " + "§d" + "[" + playerElo + "]");
                            practicePlayer.setCurrentState(PlayerState.QUEUE);
                            player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            player.updateInventory();
                        }
                        else {
                            player.closeInventory();
                            for (final UUID queuedUuid3 : queueManager.getQueuedForRankedQueue(kitName)) {
                                final PracticePlayer queuedPracticePlayer2 = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(queuedUuid3);
                                final int queuedElo = queuedPracticePlayer2.getEloMap().get(kitName);
                                final int diff2 = Math.abs(queuedElo - playerElo);
                                if (diff2 >= 325) {
                                    continue;
                                }
                                this.plugin.getManagerHandler().getQueueManager().removePlayerFromRankedQueue(kitName, queuedUuid3);
                                final ArrayList<UUID> firstTeam3 = new ArrayList<UUID>();
                                final ArrayList<UUID> secondTeam3 = new ArrayList<UUID>();
                                firstTeam3.add(queuedUuid3);
                                secondTeam3.add(player.getUniqueId());
                                this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, true, null, null, firstTeam3, secondTeam3, false));
                                return;
                            }
                            queueManager.addToRankedQueue(kitName, player.getUniqueId());
                            player.sendMessage(ChatColor.YELLOW + "You have been added to the " + "§d" + "Ranked " + kitName + ChatColor.YELLOW + " queue. " + "§d" + "[" + playerElo + "]");
                            practicePlayer.setCurrentState(PlayerState.QUEUE);
                            player.getInventory().setContents(this.plugin.getManagerHandler().getItemManager().getQueueItems());
                            player.updateInventory();
                        }
                    }
                }
            }
            else if (invTitle.contains("kit editor")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    this.plugin.getManagerHandler().getEditorManager().addEditingKit(player.getUniqueId(), kit);
                    player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(player.getUniqueId()));
                }
            }
            else if (invTitle.contains("send request")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final String kitName2 = kit.getName();
                    final Player target = this.plugin.getServer().getPlayer(this.plugin.getManagerHandler().getInventoryManager().getSelectingDuelPlayerUUID(player.getUniqueId()));
                    if (target == null) {
                        player.sendMessage(Messages.PLAYER_NOT_FOUND);
                        player.closeInventory();
                        return;
                    }
                    final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
                    player.closeInventory();
                    this.plugin.getManagerHandler().getInventoryManager().removeSelectingDuel(player.getUniqueId());
                    if (practiceTarget.getCurrentState() != PlayerState.LOBBY) {
                        player.sendMessage(Messages.PLAYER_BUSY);
                        return;
                    }
                    final Party party3 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    final Party targetParty2;
                    player.sendMessage(ChatColor.YELLOW + "Sent a duel request to " + "§d" + target.getName() + (((targetParty2 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId())) != null) ? (ChatColor.YELLOW + "'s party " + "§d" + "(" + (targetParty2.getMembers().size() + 1) + ")") : "") + ChatColor.YELLOW + " with the kit " + "§d" + kitName2 + ChatColor.YELLOW + "!");
                    this.plugin.getManagerHandler().getRequestManager().addDuelRequest(target, player, new DuelRequest(kitName2));
                    final UtilActionMessage actionMessage = new UtilActionMessage();
                    actionMessage.addText("§d" + player.getName() + ((party3 != null) ? (ChatColor.YELLOW + "'s party " + "§d" + "(" + (party3.getMembers().size() + 1) + ")") : "") + ChatColor.YELLOW + " has requested a duel with the kit " + "§d" + kitName2 + ChatColor.YELLOW + ".");
                    actionMessage.addText("               " + ChatColor.RED + "[Click here to accept]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/accept " + player.getName());
                    actionMessage.sendToPlayer(target);
                }
            }
            else if (invTitle.contains("split fights")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    final Party party4 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    player.closeInventory();
                    final ArrayList<UUID> firstTeam5 = new ArrayList<UUID>();
                    final ArrayList<UUID> secondTeam5 = new ArrayList<UUID>();
                    firstTeam5.add(party4.getLeader());
                    for (final UUID member : party4.getMembers()) {
                        if (firstTeam5.size() == secondTeam5.size()) {
                            firstTeam5.add(member);
                        }
                        else {
                            if (firstTeam5.size() <= secondTeam5.size()) {
                                continue;
                            }
                            secondTeam5.add(member);
                        }
                    }
                    this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, party4.getLeader(), party4.getLeader(), firstTeam5, secondTeam5, false));
                }
            }
            else if (invTitle.contains("tournament size")) {
                e.setCancelled(true);
                if (itemClicked.getType() != Material.NETHER_STAR) {
                    player.closeInventory();
                    return;
                }
                final boolean isPlayerTournament = ChatColor.stripColor(itemClicked.getItemMeta().getLore().get(0).split(": ")[1]).equalsIgnoreCase("Player");
                final Kit kit2 = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getLore().get(1).split(": ")[1]));
                final int size = Integer.parseInt(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName().split("v")[0]));
                if (!player.hasPermission("host.event." + size)) {
                    player.sendMessage(ChatColor.RED + "You don't have permissions to host (" + size + "v" + size + ")");
                    player.closeInventory();
                    return;
                }
                if (isPlayerTournament && Tournament.getTournaments().size() >= 1) {
                    player.sendMessage(ChatColor.RED + "There is a tournament currently running.");
                    player.closeInventory();
                    return;
                }
                if (!isPlayerTournament) {
                    Tournament.forceEndPlayerTournaments();
                }
                final Tournament tournament = new Tournament(size, kit2, isPlayerTournament);
                if (isPlayerTournament) {
                    practicePlayer.setHostCooldown(System.currentTimeMillis() + 3600000L);
                    HostCommand.getRunningTournaments().put(player.getUniqueId(), tournament);
                }
                final String[] split;
                final String[] texts = split = Lang.getLang().getLocalized((CommandSender)Bukkit.getConsoleSender(), "TOURNAMENT_STARTING").replace("%max_per_team%", String.valueOf(tournament.getMaximumPerTeam())).split("\n");
                for (final String text : split) {
                    final UtilActionMessage actionMessage2 = new UtilActionMessage();
                    actionMessage2.addText(text).addHoverText(ChatColor.GRAY + "Click to join the tournament").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/join " + Tournament.getTournaments().size());
                    for (final Player online : PlayerUtility.getOnlinePlayers()) {
                        actionMessage2.sendToPlayer(online);
                    }
                }
                player.closeInventory();
            }
            else if (invTitle.contains("available tournaments")) {
                e.setCancelled(true);
                if (itemClicked.getType() != Material.IRON_SWORD) {
                    player.closeInventory();
                    return;
                }
                final Tournament tournament2 = Tournament.getTournaments().get(e.getSlot());
                if (tournament2 == null) {
                    player.sendMessage(ChatColor.RED + "That tournament doesn't exist.");
                    player.closeInventory();
                    return;
                }
                for (final Tournament tourney : Tournament.getTournaments()) {
                    if (!tourney.isInTournament(player)) {
                        continue;
                    }
                    player.sendMessage(ChatColor.RED + "You are currently in another tournament.");
                    player.closeInventory();
                    return;
                }
                if (tournament2.isStarted()) {
                    player.sendMessage(ChatColor.RED + "Sorry! The tournament already started.");
                    return;
                }
                if (tournament2.getTotalPlayersInTournament() == tournament2.getPlayersLimit()) {
                    player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
                    player.closeInventory();
                    return;
                }
                if (tournament2.isInTournament(player)) {
                    player.sendMessage(ChatColor.RED + "You are already in the tournament.");
                    player.closeInventory();
                    return;
                }
                if (tournament2.getMaximumPerTeam() == 1) {
                    final TournamentTeam tournamentTeam = new TournamentTeam();
                    tournamentTeam.setPlayers(Collections.singletonList(player.getUniqueId()));
                    tournament2.getTeams().add(tournamentTeam);
                    tournament2.sendMessage(ChatColor.YELLOW + player.getName() + " has joined the tournament. (" + tournament2.getTotalPlayersInTournament() + "/" + tournament2.getPlayersLimit() + ")");
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
                    player.closeInventory();
                }
                else if (tournament2.getMaximumPerTeam() >= 2) {
                    final Party party5 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    if (party5 == null) {
                        player.sendMessage(ChatColor.RED + "You must be in a party to join this tournament.");
                        player.closeInventory();
                        return;
                    }
                    if (party5.getLeader() != player.getUniqueId()) {
                        player.sendMessage(ChatColor.RED + "Only the leader can join the tournament.");
                        player.closeInventory();
                        return;
                    }
                    if (party5.getSize() != tournament2.getMaximumPerTeam()) {
                        player.sendMessage(ChatColor.RED + "The party must have only " + tournament2.getMaximumPerTeam() + " players.");
                        player.closeInventory();
                        return;
                    }
                    final TournamentTeam tournamentTeam2 = new TournamentTeam();
                    tournamentTeam2.setPlayers(party5.getAllMembersOnline());
                    tournament2.getTeams().add(tournamentTeam2);
                    tournament2.sendMessage(ChatColor.YELLOW + player.getName() + "'s Party has joined the tournament. (" + tournament2.getTotalPlayersInTournament() + "/" + tournament2.getPlayersLimit() + ")");
                    this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
                    player.closeInventory();
                }
            }
            else if (invTitle.contains("party ffa")) {
                e.setCancelled(true);
                final Kit kit = this.plugin.getManagerHandler().getKitManager().getKitMap().get(ChatColor.stripColor(itemClicked.getItemMeta().getDisplayName()));
                if (kit != null) {
                    player.closeInventory();
                    final Party party4 = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                    final ArrayList<UUID> members = new ArrayList<UUID>(party4.getMembers());
                    members.add(player.getUniqueId());
                    this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, party4.getLeader(), members));
                }
            }
            else if (invTitle.contains("fight other parties") && itemClicked.getType() == Material.SKULL_ITEM) {
                e.setCancelled(true);
                final String[] itemName = UtilString.splitString(itemClicked.getItemMeta().getDisplayName());
                itemName[0] = ChatColor.stripColor(itemName[0]);
                player.closeInventory();
                this.plugin.getServer().dispatchCommand((CommandSender)player, "duel " + itemName[0]);
            }
            else if (invTitle.toLowerCase().contains("kit layout")) {
                final String kitName3 = this.plugin.getManagerHandler().getEditorManager().getPlayerEditingKit(player.getUniqueId());
                final Map<Integer, PlayerKit> kitMap = practicePlayer.getKitMap().get(kitName3);
                e.setCancelled(true);
                if (!itemClicked.hasItemMeta() || !itemClicked.getItemMeta().hasDisplayName()) {
                    return;
                }
                if (itemClicked.getType() == Material.CHEST && e.getSlot() < 9) {
                    final int kitIndex = e.getSlot();
                    final ItemStack loadedKit = UtilItem.createItem(Material.ENDER_CHEST, 1, (short)0, ChatColor.YELLOW + "KIT: " + "§d" + kitName3 + " #" + kitIndex);
                    final ItemStack load = UtilItem.createItem(Material.BOOK, 1, (short)0, ChatColor.YELLOW + "Load/Edit Kit " + "§d" + kitName3 + " #" + kitIndex);
                    final ItemStack rename = UtilItem.createItem(Material.NAME_TAG, 1, (short)0, ChatColor.YELLOW + "Rename Kit " + "§d" + kitName3 + " #" + kitIndex);
                    final ItemStack delete = UtilItem.createItem(Material.FLINT, 1, (short)0, ChatColor.YELLOW + "Delete Kit " + "§d" + kitName3 + " #" + kitIndex);
                    final PlayerKit playerKit = new PlayerKit(kitName3, kitIndex, ChatColor.GOLD + "KIT: " + kitName3 + " #" + kitIndex, new ItemStack[0], new ItemStack[0]);
                    playerKit.setMainContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                    playerKit.setArmorContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getArmorContents());
                    practicePlayer.addKit(kitName3, kitIndex, playerKit);
                    player.sendMessage("§e" + "Successfully created Kit " + "§d" + "#" + kitIndex);
                    inventory.setItem(e.getSlot(), loadedKit);
                    inventory.setItem(e.getSlot() + 9, load);
                    inventory.setItem(e.getSlot() + 18, rename);
                    inventory.setItem(e.getSlot() + 27, delete);
                }
                else if (itemClicked.getType() == Material.BOOK && e.getSlot() > 9 && e.getSlot() < 18) {
                    final int kitIndex = e.getSlot() - 9;
                    if (kitMap != null && kitMap.containsKey(kitIndex)) {
                        practicePlayer.setCurrentState(PlayerState.EDITING);
                        UtilPlayer.clear(player);
                        if (kitMap.get(kitIndex).getMainContents().length > 0) {
                            player.getInventory().setContents(kitMap.get(kitIndex).getMainContents());
                            player.getInventory().setArmorContents(kitMap.get(kitIndex).getArmorContents());
                        }
                        else {
                            player.getInventory().setContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName3).getMainContents());
                            player.getInventory().setArmorContents(kitMap.get(kitIndex).getArmorContents());
                        }
                        player.updateInventory();
                        this.plugin.getManagerHandler().getKitManager().openEditiKitsInventory(player, this.plugin.getManagerHandler().getKitManager().getKit(kitName3), kitMap.get(kitIndex));
                    }
                }
                else if (itemClicked.getType() == Material.NAME_TAG && e.getSlot() > 18 && e.getSlot() < 27) {
                    final int kitIndex = e.getSlot() - 18;
                    if (kitMap != null && kitMap.containsKey(kitIndex)) {
                        this.plugin.getManagerHandler().getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));
                        player.closeInventory();
                        player.sendMessage("§d" + "Enter the name you want this kit to be.");
                    }
                }
                else if (itemClicked.getType() == Material.FLINT && e.getSlot() > 27 && e.getSlot() < 36) {
                    final int kitIndex = e.getSlot() - 27;
                    if (kitMap != null && kitMap.containsKey(kitIndex)) {
                        this.plugin.getManagerHandler().getEditorManager().removeRenamingKit(player.getUniqueId());
                        kitMap.remove(kitIndex);
                        player.sendMessage("§c" + "Successfully removed Kit " + "§d" + "#" + kitIndex);
                        final ItemStack save = UtilItem.createItem(Material.CHEST, 1, (short)0, ChatColor.YELLOW + "Create Kit " + "§d" + kitName3 + " #" + kitIndex);
                        inventory.setItem(kitIndex, save);
                        inventory.setItem(e.getSlot(), (ItemStack)null);
                        inventory.setItem(e.getSlot() - 9, (ItemStack)null);
                        inventory.setItem(e.getSlot() - 18, (ItemStack)null);
                    }
                }
                else if (itemClicked.getType() == Material.STAINED_GLASS_PANE) {
                    this.plugin.getManagerHandler().getEditorManager().removeEditingKit(player.getUniqueId());
                    player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditorInventory());
                }
            }
        }
        else if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
            if (invTitle.toLowerCase().contains("editing kit")) {
                if (e.getClickedInventory() == null) {
                    if (e.getCursor() != null) {
                        e.setCursor((ItemStack)null);
                        player.updateInventory();
                    }
                    return;
                }
                if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getView().getBottomInventory() != null && e.getView().getBottomInventory().equals(e.getClickedInventory())) {
                    e.setCancelled(true);
                    return;
                }
                if (e.getView().getTopInventory() == null || !e.getClickedInventory().equals(e.getView().getTopInventory())) {
                    return;
                }
                e.setCancelled(true);
                final String[] parse = ChatColor.stripColor(e.getView().getTopInventory().getItem(0).getItemMeta().getDisplayName()).split(" ");
                final String kitName = parse[3].replace("(", "");
                final int id = Integer.parseInt(parse[4].replace(")", "").replace("#", ""));
                final Map<Integer, PlayerKit> kitMap2 = practicePlayer.getKitMap().get(kitName);
                if (itemClicked.getType() == Material.STAINED_CLAY && itemClicked.getDurability() == 13) {
                    int allgapple = 0;
                    int allgoldenhead = 0;
                    for (final ItemStack iteminv : player.getInventory().getContents()) {
                        if (iteminv != null && iteminv.getType() != Material.AIR && iteminv.getType() == Material.GOLDEN_APPLE) {
                            if (iteminv.getItemMeta().getDisplayName() != null && iteminv.getItemMeta().getDisplayName().contains("Golden Head")) {
                                allgoldenhead += iteminv.getAmount();
                            }
                            else {
                                allgapple += iteminv.getAmount();
                            }
                        }
                    }
                    if (allgapple > 6 || allgoldenhead > 3) {
                        player.sendMessage(ChatColor.RED + "Error - Too many Golden Apples");
                        return;
                    }
                    if (kitMap2 != null && kitMap2.containsKey(id)) {
                        kitMap2.get(id).setMainContents(e.getView().getBottomInventory().getContents());
                        kitMap2.get(id).setArmorContents(reverse(new ItemStack[] { e.getView().getTopInventory().getItem(18), e.getView().getTopInventory().getItem(27), e.getView().getTopInventory().getItem(36), e.getView().getTopInventory().getItem(45) }));
                        player.sendMessage("§e" + "Successfully saved Kit " + "§d" + "#" + id);
                        player.closeInventory();
                    }
                }
                else if (itemClicked.getType() == Material.STAINED_CLAY && itemClicked.getDurability() == 14) {
                    player.closeInventory();
                }
                else if (itemClicked.getType() == Material.STAINED_CLAY && itemClicked.getDurability() == 4) {
                    e.getView().getBottomInventory().clear();
                }
                else if (itemClicked.getType() == Material.STAINED_CLAY && itemClicked.getDurability() == 0) {
                    player.getInventory().setContents(this.plugin.getManagerHandler().getKitManager().getKit(kitName).getMainContents());
                }
                else if (itemClicked.getType() == Material.BUCKET) {
                    if (e.getCursor() != null) {
                        e.setCursor((ItemStack)null);
                        player.updateInventory();
                    }
                }
                else if (itemClicked.getType() == Material.POTION && itemClicked.getDurability() == 16421 && itemClicked.hasItemMeta() && itemClicked.getItemMeta().hasDisplayName()) {
                    if (!player.getInventory().contains(Material.POTION) || !player.getInventory().contains(Material.ENDER_PEARL)) {
                        e.setCancelled(true);
                        e.setCursor((ItemStack)null);
                    }
                    else {
                        while (e.getView().getBottomInventory().firstEmpty() != -1) {
                            e.getView().getBottomInventory().addItem(new ItemStack[] { new ItemStack(Material.POTION, 1, (short)16421) });
                        }
                    }
                }
                else if (itemClicked.getType() == Material.POTION || player.getInventory().contains(Material.IRON_AXE)) {
                    e.setCancelled(true);
                    e.setCursor((ItemStack)null);
                }
                else if (itemClicked.getType() == Material.COBBLESTONE || itemClicked.getType() == Material.WOOD || itemClicked.getType() == Material.LAVA_BUCKET || itemClicked.getType() == Material.WATER_BUCKET || itemClicked.getType() == Material.ARROW || itemClicked.getType() == Material.GOLDEN_APPLE || itemClicked.getType() == Material.DIAMOND_HELMET || itemClicked.getType() == Material.DIAMOND_CHESTPLATE || itemClicked.getType() == Material.DIAMOND_LEGGINGS || itemClicked.getType() == Material.DIAMOND_BOOTS || itemClicked.getType() == Material.FLINT_AND_STEEL) {
                    e.setCancelled(true);
                    e.setCursor((ItemStack)null);
                }
                else {
                    if (itemClicked.getType() == Material.AIR || itemClicked.getType() == Material.BOOK || itemClicked.getType() == Material.STAINED_GLASS_PANE || (itemClicked.hasItemMeta() && itemClicked.getItemMeta().hasLore() && itemClicked.getItemMeta().getLore().get(1).contains("equipped"))) {
                        return;
                    }
                    if (e.getClick().isShiftClick()) {
                        if (e.getView().getBottomInventory().firstEmpty() != -1) {
                            e.getView().getBottomInventory().addItem(new ItemStack[] { itemClicked });
                        }
                    }
                    else if (e.getClick() == ClickType.NUMBER_KEY) {
                        if (e.getView().getBottomInventory().firstEmpty() != -1) {
                            e.getView().getBottomInventory().setItem(e.getHotbarButton(), itemClicked);
                        }
                    }
                    else {
                        e.setCursor(itemClicked);
                    }
                }
            }
        }
        else if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
            if (invTitle.toLowerCase().contains("spectator menu")) {
                e.setCancelled(true);
            }
        }
        else if (practicePlayer.getCurrentState() == PlayerState.WAITING || practicePlayer.getCurrentState() == PlayerState.FIGHTING) {
            if (itemClicked != null && itemClicked.getType() == Material.BOOK) {
                e.setCancelled(true);
            }
            if (e.getClick() == ClickType.NUMBER_KEY && player.getInventory().getItem(e.getHotbarButton()) != null && player.getInventory().getItem(e.getHotbarButton()).getType() == Material.BOOK) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (this.isTopInventory(event) && event.getView().getTopInventory().getTitle().toLowerCase().contains("editing kit")) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onInvClose(final InventoryCloseEvent e) {
        if (e.getInventory().getTitle().toLowerCase().contains("send request")) {
            this.plugin.getManagerHandler().getInventoryManager().removeSelectingDuel(e.getPlayer().getUniqueId());
        }
        if (e.getInventory().getTitle().toLowerCase().contains("kit layout") && !this.plugin.getManagerHandler().getEditorManager().getRenamingKit().containsKey(e.getPlayer().getUniqueId())) {
            this.plugin.getManagerHandler().getEditorManager().removeEditingKit(e.getPlayer().getUniqueId());
        }
        if (e.getInventory().getTitle().toLowerCase().contains("editing kit")) {
            this.plugin.getManagerHandler().getEditorManager().removeEditingKit(e.getPlayer().getUniqueId());
            this.plugin.getManagerHandler().getPracticePlayerManager().returnItems((Player)e.getPlayer());
        }
    }
    
    public static ItemStack[] reverse(final ItemStack[] x) {
        final ItemStack[] d = new ItemStack[x.length];
        for (int i = x.length - 1; i >= 0; --i) {
            d[x.length - i - 1] = x[i];
        }
        return d;
    }
    
    private boolean isTopInventory(final InventoryDragEvent event) {
        final InventoryView view = event.getView();
        if (event.getView().getTopInventory() == null) {
            return false;
        }
        final Set<Map.Entry<Integer, ItemStack>> items = event.getNewItems().entrySet();
        boolean isInventory = false;
        for (final Map.Entry<Integer, ItemStack> item : items) {
            if (item.getKey() < event.getView().getTopInventory().getSize()) {
                isInventory = true;
                break;
            }
        }
        return isInventory;
    }
}
