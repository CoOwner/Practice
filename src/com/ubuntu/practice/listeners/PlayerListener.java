package com.ubuntu.practice.listeners;

import com.ubuntu.practice.*;
import java.text.*;
import org.bukkit.event.*;
import org.bukkit.potion.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.duel.*;
import org.bukkit.*;
import com.ubuntu.practice.runnables.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.*;
import org.bukkit.event.block.*;
import com.ubuntu.practice.tournament.*;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import com.ubuntu.practice.party.*;
import com.ubuntu.practice.player.*;
import org.bukkit.block.*;
import com.ubuntu.practice.kit.*;
import org.bukkit.event.entity.*;
import com.ubuntu.practice.util.*;

import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedSoundEffect;

import org.bukkit.event.player.*;
import java.lang.reflect.*;

public class PlayerListener implements Listener
{
    private uPractice plugin;
    private static Map<UUID, Long> lastPearl;
    private DecimalFormat formatter;
    
    public PlayerListener(final uPractice plugin) {
        this.formatter = new DecimalFormat("0.0");
        PlayerListener.lastPearl = new HashMap<UUID, Long>();
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent e) {
        this.plugin.getManagerHandler().getPracticePlayerManager().createPracticePlayer(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerItemConsume(final PlayerItemConsumeEvent event) {
        final ItemStack itemStack = event.getItem();
        if (itemStack.getType() == Material.GOLDEN_APPLE && ChatColor.stripColor(UtilItem.getTitle(itemStack)).equalsIgnoreCase("Golden Head")) {
            final PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 200, 1);
            UtilPlayer.addConcideringLevel(event.getPlayer(), effect);
        }
    }
    

    
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setDamage(100.0);
        }
    }
    
    @EventHandler
    public void onPlayerRegainHealth(final EntityRegainHealthEvent event) {
        final Duel duel;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && event.getEntity() instanceof Player && (duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(event.getEntity().getUniqueId())) != null && this.plugin.getManagerHandler().getKitManager().getKit(duel.getKitName()).isBuilduhc()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer != null) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party != null) {
                if (party.getLeader().equals(player.getUniqueId())) {
                    this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                    for (final UUID member : party.getMembers()) {
                        final Player pLayer = this.plugin.getServer().getPlayer(member);
                        pLayer.sendMessage(ChatColor.YELLOW + "Your party leader has left, so the party t disbanded!");
                        final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                        if (ppLayer.getCurrentState() != PlayerState.LOBBY) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(pLayer);
                    }
                    this.plugin.getManagerHandler().getInventoryManager().delParty(party);
                }
                else {
                    this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                    this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
                }
            }
            switch (practicePlayer.getCurrentState()) {
                case WAITING:
                case FIGHTING: {
                    this.plugin.getManagerHandler().getDuelManager().removePlayerFromDuel(e.getPlayer());
                    break;
                }
                case QUEUE: {
                    if (party == null) {
                        this.plugin.getManagerHandler().getQueueManager().unqueueSingleQueue(e.getPlayer().getUniqueId());
                        break;
                    }
                    for (final UUID uuid : party.getMembers()) {
                        final Player memberPlayer = this.plugin.getServer().getPlayer(uuid);
                        if (memberPlayer == null) {
                            continue;
                        }
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(memberPlayer);
                    }
                    this.plugin.getManagerHandler().getQueueManager().unqueuePartyQueue(party.getLeader());
                    this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.YELLOW + "Your party has left the queue");
                    final Player leaderPlayer = this.plugin.getServer().getPlayer(party.getLeader());
                    if (leaderPlayer == null) {
                        break;
                    }
                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(leaderPlayer);
                    break;
                }
                case EDITING: {
                    this.plugin.getManagerHandler().getEditorManager().removeEditingKit(e.getPlayer().getUniqueId());
                    break;
                }
                case SPECTATING: {
                    this.plugin.getManagerHandler().getSpectatorManager().removeSpectatorMode(player);
                    break;
                }
            }
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously((Plugin)uPractice.getInstance(), (Runnable)new RemovePlayerTask(e.getPlayer()));
        this.plugin.getManagerHandler().getPracticePlayerManager().removePracticePlayer(e.getPlayer());
    }

    
    @EventHandler
    public void onPlayerInteractEntityEvent(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player && e.getPlayer().getItemInHand().getType() == Material.COMPASS) {
            e.getPlayer().openInventory((Inventory)((Player)e.getRightClicked()).getInventory());
        }
    }
    
    @EventHandler
    public void onPlayerPreCommand(final PlayerCommandPreprocessEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.LOADING) {
            e.getPlayer().sendMessage(ChatColor.RED + "§l(LOADING) §7Your data is currently loading...");
            e.setCancelled(true);
        }
        else if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
            e.getPlayer().sendMessage(ChatColor.RED + "You can't execute commands while editing a kit.");
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerJoinEvent e) {
        e.setJoinMessage((String)null);
        new BukkitRunnable() {
            public void run() {
                final PracticePlayer practicePlayer = PlayerListener.this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
                PlayerListener.this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(e.getPlayer());
            }
        }.runTaskLater((Plugin)this.plugin, 20L);
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerQuitEvent e) {
        e.setQuitMessage((String)null);
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.DIAMOND_SWORD) {
            e.setCancelled(true);
        }
    }

    
    @EventHandler
    public void onPlayerInteract(final ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            final Player player = (Player)e.getEntity().getShooter();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            practicePlayer.getProjectileQueue().add(e.getEntity());
        }
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            final Player player = e.getPlayer();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            final ItemStack interactedItem = e.getItem();
            final Block interactedBlock = e.getClickedBlock();
            if (practicePlayer.getCurrentState() == PlayerState.LOADING) {
                e.getPlayer().sendMessage(ChatColor.RED + "§l(LOADING) §7Your data is currently loading...");
                return;
            }
            if (interactedItem != null) {
                if (practicePlayer.getCurrentState() == PlayerState.LOBBY) {
                    if (Tournament.getTournaments().size() > 0) {
                        for (final Tournament tournament : Tournament.getTournaments()) {
                            if (!tournament.isInTournament(player)) {
                                continue;
                            }
                            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
                            return;
                        }
                    }
                    if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                        switch (interactedItem.getType()) {
                            case REDSTONE: {
                                this.plugin.getServer().dispatchCommand((CommandSender)player, "party leave");
                                break;
                            }
                            case SKULL_ITEM: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + "You are not the leader of this party!");
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.RED + "Your party is currently busy and cannot fight");
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getPartiesInventory());
                                break;
                            }
                            case IRON_AXE: {
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
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getSplitFightInventory());
                                break;
                            }
                            case GOLD_SWORD: {
                                if (!party.getLeader().equals(player.getUniqueId())) {
                                    player.sendMessage(ChatColor.RED + "You are not the leader of this party!");
                                    break;
                                }
                                if (party.getPartyState() == PartyState.DUELING) {
                                    player.sendMessage(ChatColor.RED + "Your party is currently busy and cannot fight");
                                    break;
                                }
                                if (party.getSize() != 2) {
                                    player.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
                                    break;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
                                break;
                            }
                            case NAME_TAG: {
                                player.chat("/party info");
                                break;
                            }
                        }
                    }
                    else {
                        switch (interactedItem.getType()) {
                            case REDSTONE_COMPARATOR: {
                                player.chat("/settings");
                                break;
                            }
                            case REDSTONE_TORCH_ON: {
                                player.chat("/mod");
                                break;
                            }
                            case CHEST: {
                                player.chat("/buy");
                                break;
                            }
                            case BLAZE_POWDER: {
                                player.chat("/duel " + practicePlayer.getLastDuelPlayer());
                                break;
                            }
                            case IRON_SWORD: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getUnrankedInventory());
                                break;
                            }
                            case DIAMOND_SWORD: {
                                if (practicePlayer.getUnrankedWins() < 20 && !player.isOp()) {
                                    player.sendMessage(ChatColor.RED + "You must win 20 Unranked Matches to play in Ranked Matches.");
                                    player.sendMessage(ChatColor.RED + "You need (" + (20 - practicePlayer.getUnrankedWins()) + ") more win" + ((practicePlayer.getUnrankedWins() == 19) ? "" : "s") + " to play.");
                                    return;
                                }
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getRankedInventory());
                                break;
                            }
                            case BOOK: {
                                player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditorInventory());
                                break;
                            }
                            case NAME_TAG: {
                            	this.plugin.getServer().dispatchCommand((CommandSender)player, "party create");
                                break;
                            }
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.QUEUE) {
                    switch (interactedItem.getType()) {
                        case REDSTONE: {
                            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
                            if (party != null) {
                                for (final UUID uuid : party.getMembers()) {
                                    final Player memberPlayer = this.plugin.getServer().getPlayer(uuid);
                                    if (memberPlayer == null) {
                                        continue;
                                    }
                                    this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(memberPlayer);
                                }
                                this.plugin.getManagerHandler().getQueueManager().unqueuePartyQueue(party.getLeader());
                                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.RED + "Your party has left the queue");
                                final Player leaderPlayer = this.plugin.getServer().getPlayer(party.getLeader());
                                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(leaderPlayer);
                                break;
                            }
                            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobbyQueueing(player);
                            this.plugin.getManagerHandler().getQueueManager().unqueueSingleQueue(player.getUniqueId());
                            player.sendMessage(ChatColor.RED + "You have left the queue.");
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) {
                    switch (interactedItem.getType()) {
                        case BOOK: {
                            final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId()).getKitName());
                            final Map<Integer, PlayerKit> playerKitMap = practicePlayer.getKitMap().get(kit.getName());
                            for (int i = 0; i < 9; ++i) {
                                final ItemStack item = player.getInventory().getItem(i);
                                if (item != null) {
                                    if (item.equals((Object)interactedItem) && i == 0) {
                                        player.getInventory().setContents(kit.getMainContents());
                                        player.getInventory().setArmorContents(kit.getArmorContents());
                                        player.updateInventory();
                                        break;
                                    }
                                    if (item.equals((Object)interactedItem)) {
                                        final PlayerKit playerKit = playerKitMap.get(i - 1);
                                        if (playerKit != null) {
                                            player.getInventory().setContents(playerKit.getMainContents());
                                            player.getInventory().setArmorContents(playerKit.getArmorContents());
                                            player.updateInventory();
                                            break;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case ENDER_PEARL: {
                            if (practicePlayer.getCurrentState() != PlayerState.FIGHTING) {
                                player.sendMessage(ChatColor.RED + "You can't throw enderpearls in your current state!");
                                e.setCancelled(true);
                                break;
                            }
                            final long now = System.currentTimeMillis();
                            final double d;
                            final double diff = d = (PlayerListener.lastPearl.containsKey(player.getUniqueId()) ? (now - PlayerListener.lastPearl.get(player.getUniqueId())) : ((double)now));
                            if (diff < 15000.0) {
                                player.sendMessage(ChatColor.RED + "Pearl cooldown: " + new DecimalFormat(".#").format(15.0 - diff / 1000.0) + " seconds");
                                e.setCancelled(true);
                                player.updateInventory();
                                break;
                            }
                            PlayerListener.lastPearl.put(player.getUniqueId(), now);
                            break;
                        }
                        case POTION: {
                            if (interactedItem.getAmount() <= 1) {
                                break;
                            }
                            e.setCancelled(true);
                            interactedItem.setAmount(1);
                            player.sendMessage("§cYou can't use stacked potions.");
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.SPECTATING) {
                    switch (interactedItem.getType()) {
                        case REDSTONE: {
                            this.plugin.getManagerHandler().getSpectatorManager().removeSpectator(player, true);
                            break;
                        }
                        case NETHER_STAR: {
                            this.plugin.getManagerHandler().getSpectatorManager().removeSpectatorMode(player);
                            break;
                        }
                        case PAPER: {
                            if (this.plugin.getManagerHandler().getDuelManager().getUuidIdentifierToDuel().values().size() == 0) {
                                player.sendMessage(ChatColor.RED + "There are currently no matches to spectate.");
                                return;
                            }
                            final Duel duel = this.plugin.getManagerHandler().getDuelManager().getRandomDuel();
                            if (duel == null) {
                                player.sendMessage(ChatColor.RED + "Please try again, an error occured.");
                                return;
                            }
                            final Player spectator = Bukkit.getPlayer((UUID)duel.getFirstTeam().get(0));
                            if (spectator == null) {
                                player.sendMessage(ChatColor.RED + "Please try again, an error occured.");
                                return;
                            }
                            this.plugin.getManagerHandler().getSpectatorManager().addSpectator(player, spectator);
                            break;
                        }
                    }
                }
                else if (practicePlayer.getCurrentState() == PlayerState.EDITING) {
                    switch (interactedItem.getType()) {
                        case POTION: {
                            e.setCancelled(true);
                            break;
                        }
                        case ENDER_PEARL: {
                            e.setCancelled(true);
                            break;
                        }
                    }
                }
            }
            if (interactedBlock != null && practicePlayer.getCurrentState() == PlayerState.EDITING) {
                switch (interactedBlock.getType()) {
                    case SIGN_POST:
                    case WALL_SIGN:
                    case SIGN: {
                        this.plugin.getManagerHandler().getEditorManager().removeEditingKit(player.getUniqueId());
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                        break;
                    }
                    case CHEST: {
                        e.setCancelled(true);
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitItemsInventory(player.getUniqueId()));
                        break;
                    }
                    case ANVIL: {
                        e.setCancelled(true);
                        player.openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(player.getUniqueId()));
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        final Iterator recipents = e.getRecipients().iterator();
        while (recipents.hasNext()) {
            final PracticePlayer practicePlayer = uPractice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer((Player) recipents.next());
            if (practicePlayer.getSettings().isPublicChat()) {
                continue;
            }
            recipents.remove();
        }
        final PlayerKit kitRenaming = this.plugin.getManagerHandler().getEditorManager().getKitRenaming(e.getPlayer().getUniqueId());
        if (kitRenaming != null) {
            kitRenaming.setDisplayName(e.getMessage().replaceAll("&", "§"));
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.YELLOW + "Successfully set kit " + "§d" + kitRenaming.getKitIndex() + ChatColor.YELLOW + "'s name to " + "§d" + kitRenaming.getDisplayName());
            this.plugin.getManagerHandler().getEditorManager().removeRenamingKit(e.getPlayer().getUniqueId());
            e.getPlayer().openInventory(this.plugin.getManagerHandler().getInventoryManager().getEditKitKitsInventory(e.getPlayer().getUniqueId()));
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() != PlayerState.FIGHTING && practicePlayer.getCurrentState() != PlayerState.WAITING && practicePlayer.getCurrentState() != PlayerState.BUILDER) {
            e.setCancelled(true);
        }
        if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && e.getItemDrop().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() != PlayerState.FIGHTING && practicePlayer.getCurrentState() != PlayerState.WAITING && practicePlayer.getCurrentState() != PlayerState.BUILDER) {
            e.setCancelled(true);
        }
        if ((practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) && e.getItem().getItemStack().getType() == Material.BOOK) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player)e.getEntity();
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
            if (practicePlayer != null && (practicePlayer.getCurrentState() != PlayerState.FIGHTING || player.getInventory().contains(Material.MUSHROOM_SOUP))) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        PlayerListener.lastPearl.remove(player.getUniqueId());
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() == PlayerState.FIGHTING || practicePlayer.getCurrentState() == PlayerState.WAITING) {
            this.plugin.getManagerHandler().getDuelManager().removePlayerFromDuel(player);
        }
        if (e.getEntity() instanceof CraftPlayer) {
            final PacketPlayOutNamedSoundEffect soundEffect = new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 10000.0f, 63.0f);
            final Packet packet = null;
            PlayerUtility.getOnlinePlayers().stream().filter(p -> p.canSee(player)).forEach(p -> {
                if (p instanceof CraftPlayer) {
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                }
                return;
            });
            this.autoRespawn(e);
        }
        e.setDeathMessage((String)null);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                PlayerListener.this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(e.getPlayer());
            }
        }, 2L);
    }
    
    private void autoRespawn(final PlayerDeathEvent e) {
        new BukkitRunnable() {
            public void run() {
                try {
                    final Object nmsPlayer = e.getEntity().getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(e.getEntity(), new Object[0]);
                    final Object con = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
                    final Class EntityPlayer2 = Class.forName(nmsPlayer.getClass().getPackage().getName() + ".EntityPlayer");
                    final Field minecraftServer = con.getClass().getDeclaredField("minecraftServer");
                    minecraftServer.setAccessible(true);
                    final Object mcserver = minecraftServer.get(con);
                    final Object playerlist = mcserver.getClass().getDeclaredMethod("getPlayerList", (Class<?>[])new Class[0]).invoke(mcserver, new Object[0]);
                    final Method moveToWorld = playerlist.getClass().getMethod("moveToWorld", EntityPlayer2, Integer.TYPE, Boolean.TYPE);
                    moveToWorld.invoke(playerlist, nmsPlayer, 0, false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskLater((Plugin)this.plugin, 20L);
    }
    
    public static Map<UUID, Long> getLastPearl() {
        return PlayerListener.lastPearl;
    }
}
