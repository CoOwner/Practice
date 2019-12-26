package com.ubuntu.practice.manager.managers;

import com.ubuntu.practice.manager.*;
import com.ubuntu.practice.kit.*;
import com.ubuntu.practice.util.*;
import org.bukkit.inventory.*;
import com.ubuntu.practice.duel.*;
import org.bukkit.*;
import com.ubuntu.practice.tournament.*;
import com.ubuntu.practice.party.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.inventory.meta.*;
import com.ubuntu.practice.player.*;

public class InventoryManager extends Manager
{
    private Inventory unrankedInventory;
    private Inventory rankedInventory;
    private Inventory unrankedPartyInventory;
    private Inventory editorInventory;
    private Inventory requestInventory;
    private Inventory splitFightInventory;
    private Inventory partyEventsInventory;
    private Inventory partiesInventory;
    private Inventory ffaPartyInventory;
    private Inventory tournamentInventory;
    private Inventory joinTournamentInventory;
    private Map<UUID, UUID> selectingDuel;
    private Map<UUID, Inventory> editingKitItems;
    private Map<UUID, Inventory> editingKitKits;
    
    public InventoryManager(final ManagerHandler handler) {
        super(handler);
        this.selectingDuel = new HashMap<UUID, UUID>();
        this.editingKitItems = new HashMap<UUID, Inventory>();
        this.editingKitKits = new HashMap<UUID, Inventory>();
        this.unrankedInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Unranked Queue");
        this.rankedInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Ranked Queue");
        this.unrankedPartyInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Unranked Queue");
        this.editorInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8"+ "Kit Editor");
        this.requestInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Send Request");
        this.splitFightInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Split Fights");
        this.partyEventsInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Party Events");
        this.partiesInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 54, "§8"+ "Fight other parties");
        this.ffaPartyInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Party FFA");
        this.tournamentInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Tournament Size");
        this.joinTournamentInventory = handler.getPlugin().getServer().createInventory((InventoryHolder)null, 9, "§8" + "Available Tournaments");
        this.setUnrankedInventory();
        this.setRankedInventory();
        this.setUnrankedPartyInventory();
        this.setEditorInventory();
        this.setRequestInventory();
        this.setPartyEventsInventory();
        this.setSplitFightInventory();
        this.setFfaPartyInventory();
        this.setJoinTournamentInventory();
    }
    
    public void setUnrankedInventory() {
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final int inGame = this.handler.getDuelManager().getUnRankedDuelsFromKit(kitName);
            final int inQueue = (this.handler.getQueueManager().getQueuedForUnrankedQueue(kitName) != null) ? 1 : 0;
            final List<String> lore = Arrays.asList(ChatColor.YELLOW + "In Game §7» " + ChatColor.WHITE + inGame, ChatColor.YELLOW + "In Queue §7» " + ChatColor.WHITE + inQueue);
            final ItemStack kitIcon = UtilItem.createItem(kit.getIcon().getType(), (inGame > 64) ? 64 : ((inGame == 0) ? 1 : inGame), kit.getIcon().getDurability(), "§d" + kitName, lore);
            this.unrankedInventory.setItem(count, kitIcon);
            ++count;
        }
    }

    
    public void setUnrankedPartyInventory() {
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final int inGame = this.handler.getDuelManager().getUnRankedPartyDuelsFromKit(kitName);
            final int inQueue = (this.handler.getQueueManager().getQueuedForPartyUnrankedQueue(kitName) != null) ? 1 : 0;
            final List<String> lore = Arrays.asList(ChatColor.YELLOW + "In Game §7» " + ChatColor.WHITE + inGame, ChatColor.YELLOW + "In Queue §7» " + ChatColor.WHITE + inQueue);
            final ItemStack kitIcon = UtilItem.createItem(kit.getIcon().getType(), (inGame > 64) ? 64 : ((inGame == 0) ? 1 : inGame), kit.getIcon().getDurability(), "§d" + kitName, lore);
            this.unrankedInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setRankedInventory() {
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (kit.isEnabled()) {
                if (!kit.isRanked()) {
                    continue;
                }
                final int inGame = this.handler.getDuelManager().getRankedDuelsFromKit(kitName);
                final int inQueue = this.handler.getQueueManager().getRankedKitQueueMap().containsKey(kitName) ? this.handler.getQueueManager().getRankedKitQueueMap().get(kitName).size() : 0;
                final List<String> lore = Arrays.asList(ChatColor.YELLOW + "In Game §7» " + ChatColor.WHITE + inGame, ChatColor.YELLOW + "In Queue §7» " + ChatColor.WHITE + inQueue);
                final ItemStack kitIcon = UtilItem.createItem(kit.getIcon().getType(), (inGame > 64) ? 64 : ((inGame == 0) ? 1 : inGame), kit.getIcon().getDurability(), "§d" + kitName, lore);
                this.rankedInventory.setItem(count, kitIcon);
                ++count;
            }
        }
    }
    
    public void setEditorInventory() {
        this.editorInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (kit.isEnabled()) {
                if (!kit.isEditable()) {
                    continue;
                }
                final ItemStack kitIcon = UtilItem.createItem(kit.getIcon().getType(), 1, kit.getIcon().getDurability(), "§d" + kitName);
                this.editorInventory.setItem(count, kitIcon);
                ++count;
            }
        }
    }
    
    public void setRequestInventory() {
        this.requestInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§d" + kitName);
            this.requestInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setJoinTournamentInventory() {
        this.joinTournamentInventory.clear();
        if (Tournament.getTournaments().size() == 0) {
            return;
        }
        int count = 1;
        for (final Tournament tournament : Tournament.getTournaments()) {
            final String[] arrstring = { ChatColor.YELLOW + "Players: (" + tournament.getTeams().size() + "/" + tournament.getPlayersLimit() + ")", ChatColor.GRAY + "Stage: " + ChatColor.GOLD + ((tournament.getTournamentStage() == null) ? "Waiting for players" : tournament.getTournamentStage().name().replace("_", " ")), ChatColor.GRAY + "Click here to join the tournament" };
            final ItemStack item = UtilItem.createItem(Material.IRON_SWORD, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + tournament.getMaximumPerTeam() + "v" + tournament.getMaximumPerTeam(), Arrays.asList(ChatColor.YELLOW + "Players: (" + tournament.getTeams().size() + "/" + tournament.getPlayersLimit() + ")", ChatColor.GRAY + "Stage: " + ChatColor.GOLD + ((tournament.getTournamentStage() == null) ? "Waiting for players" : tournament.getTournamentStage().name().replace("_", " ")), ChatColor.GRAY + "Click here to join the tournament"));
            this.joinTournamentInventory.addItem(new ItemStack[] { item });
            ++count;
        }
    }
    
    public void setTournamentInventory(final Kit kit, final boolean isPlayer) {
        this.tournamentInventory.clear();
        for (int i = 1; i <= 5; ++i) {
            final String[] arrstring = { ChatColor.GRAY + "Type: " + (isPlayer ? "Player" : "System"), ChatColor.GRAY + "Kit: " + kit.getName() };
            final ItemStack item = UtilItem.createItem(Material.NETHER_STAR, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + i + "v" + i, Arrays.asList(ChatColor.GRAY + "Type: " + (isPlayer ? "Player" : "System"), ChatColor.GRAY + "Kit: " + kit.getName()));
            this.tournamentInventory.addItem(new ItemStack[] { item });
        }
    }
    
    private void setPartyEventsInventory() {
        this.splitFightInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§d" + kitName);
            this.splitFightInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setSplitFightInventory() {
        this.splitFightInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§d" + kitName);
            this.splitFightInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void setFfaPartyInventory() {
        this.ffaPartyInventory.clear();
        int count = 0;
        for (final Map.Entry<String, Kit> mapEntry : this.handler.getKitManager().getKitMap().entrySet()) {
            final String kitName = mapEntry.getKey();
            final Kit kit = mapEntry.getValue();
            if (!kit.isEnabled()) {
                continue;
            }
            final ItemStack kitIcon = UtilItem.name(kit.getIcon(), "§d" + kitName);
            this.ffaPartyInventory.setItem(count, kitIcon);
            ++count;
        }
    }
    
    public void addParty(final Party party) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        final ItemStack skull = UtilItem.createItem(Material.SKULL_ITEM, 1, (short)3, ChatColor.GOLD + player.getName());
        this.partiesInventory.addItem(new ItemStack[] { skull });
    }
    
    public void delParty(final Party party) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        final String leaderName = (player == null) ? party.getLeaderName() : player.getName();
        for (final ItemStack itemStack : this.partiesInventory.getContents()) {
            if (itemStack != null && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(leaderName)) {
                this.partiesInventory.remove(itemStack);
                break;
            }
        }
    }
    
    public void updateParty(final Party party) {
        final Player player = this.handler.getPlugin().getServer().getPlayer(party.getLeader());
        final String leaderName = (player == null) ? party.getLeaderName() : player.getName();
        final ArrayList<String> lores = new ArrayList<String>();
        for (final UUID uuid : party.getMembers()) {
            final Player memberPlayer = this.handler.getPlugin().getServer().getPlayer(uuid);
            if (memberPlayer == null) {
                continue;
            }
            lores.add("§d" + memberPlayer.getName());
        }
        for (final ItemStack itemStack : this.partiesInventory.getContents()) {
            if (itemStack != null && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains(leaderName)) {
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setLore((List)lores);
                itemMeta.setDisplayName(ChatColor.GOLD + leaderName + "§d" + " (" + (party.getMembers().size() + 1) + ")");
                itemStack.setItemMeta(itemMeta);
                break;
            }
        }
    }
    
    public void setSelectingDuel(final UUID uuid, final UUID uuid1) {
        this.selectingDuel.put(uuid, uuid1);
    }
    
    public UUID getSelectingDuelPlayerUUID(final UUID uuid) {
        return this.selectingDuel.get(uuid);
    }
    
    public void removeSelectingDuel(final UUID uuid) {
        this.selectingDuel.remove(uuid);
    }
    
    public Inventory getEditKitItemsInventory(final UUID uuid) {
        return this.editingKitItems.get(uuid);
    }
    
    public void addEditKitItemsInventory(final UUID uuid, final Kit kit) {
        final Inventory inventory = this.handler.getPlugin().getServer().createInventory((InventoryHolder)null, 54, kit.getName());
        for (final ItemStack itemStack : kit.getMainContents()) {
            if (itemStack != null) {
                inventory.addItem(new ItemStack[] { itemStack });
            }
        }
        for (final ItemStack itemStack : kit.getArmorContents()) {
            if (itemStack != null) {
                inventory.addItem(new ItemStack[] { itemStack });
            }
        }
        inventory.addItem(new ItemStack[] { new ItemStack(Material.COOKED_BEEF, 64) });
        this.editingKitItems.put(uuid, inventory);
    }
    
    public void destroyEditKitItemsInventory(final UUID uuid) {
        this.editingKitItems.remove(uuid);
    }
    
    public Inventory getEditKitKitsInventory(final UUID uuid) {
        return this.editingKitKits.get(uuid);
    }
    
    public void addEditKitKitsInventory(final UUID uuid, final Kit kit) {
        final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(uuid);
        final Map<Integer, PlayerKit> kitMap = practicePlayer.getKitMap().get(kit.getName());
        final Inventory inventory = this.handler.getPlugin().getServer().createInventory((InventoryHolder)null, 36, "§8" + "Kit Layout");
        for (int i = 1; i <= 7; ++i) {
            final ItemStack save = UtilItem.createItem(Material.CHEST, 1, (short)0, ChatColor.YELLOW + "Create Kit " + "§d" + kit.getName() + " #" + i);
            inventory.setItem(i, save);
            if (kitMap != null) {
                if (kitMap.containsKey(i)) {
                    final ItemStack loadedKit = UtilItem.createItem(Material.ENDER_CHEST, 1, (short)0, ChatColor.YELLOW + "KIT: " + "§d" + kit.getName() + " #" + i);
                    final ItemStack load = UtilItem.createItem(Material.BOOK, 1, (short)0, ChatColor.YELLOW + "Load/Edit Kit " + "§d" + kit.getName() + " #" + i);
                    final ItemStack rename = UtilItem.createItem(Material.NAME_TAG, 1, (short)0, ChatColor.YELLOW + "Rename Kit " + "§d" + kit.getName() + " #" + i);
                    final ItemStack delete = UtilItem.createItem(Material.FLINT, 1, (short)0, ChatColor.YELLOW + "Delete Kit " + "§d" + kit.getName() + " #" + i);
                    inventory.setItem(i, loadedKit);
                    inventory.setItem(i + 9, load);
                    inventory.setItem(i + 18, rename);
                    inventory.setItem(i + 27, delete);
                }
            }
        }
        final ItemStack back = UtilItem.createItem(Material.STAINED_GLASS_PANE, 1, (short)14, ChatColor.RED + "\u23ce Go Back");
        inventory.setItem(0, back);
        inventory.setItem(9, back);
        inventory.setItem(18, back);
        inventory.setItem(27, back);
        inventory.setItem(8, back);
        inventory.setItem(17, back);
        inventory.setItem(26, back);
        inventory.setItem(35, back);
        this.editingKitKits.put(uuid, inventory);
    }
    
    public void destroyEditKitKitsInventory(final UUID uuid) {
        this.editingKitKits.remove(uuid);
    }
    
    public Inventory getUnrankedInventory() {
        return this.unrankedInventory;
    }
    
    public Inventory getRankedInventory() {
        return this.rankedInventory;
    }
    
    public Inventory getEditorInventory() {
        return this.editorInventory;
    }
    
    public Inventory getRequestInventory() {
        return this.requestInventory;
    }
    
    public Inventory getSplitFightInventory() {
        return this.splitFightInventory;
    }
    
    public Inventory getPartiesInventory() {
        return this.partiesInventory;
    }
    
    public Inventory getFfaPartyInventory() {
        return this.ffaPartyInventory;
    }
    
    public Inventory getPartyEventsInventory() {
        return this.partyEventsInventory;
    }

    
    public Inventory getTournamentInventory() {
        return this.tournamentInventory;
    }
    
    public Inventory getJoinTournamentInventory() {
        return this.joinTournamentInventory;
    }
}
