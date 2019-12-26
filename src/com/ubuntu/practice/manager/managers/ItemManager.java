package com.ubuntu.practice.manager.managers;

import org.bukkit.inventory.*;
import com.ubuntu.practice.manager.*;
import org.bukkit.*;
import com.ubuntu.practice.util.*;
import org.bukkit.enchantments.*;

public class ItemManager extends Manager
{
    private ItemStack[] spawnItems;
    private ItemStack[] queueItems;
    private ItemStack[] specItems;
    private ItemStack[] partyItems;
    private ItemStack[] spectatorModeItems;
    
    public ItemManager(final ManagerHandler handler) {
        super(handler);
        this.loadSpawnItems();
        this.loadQueueItems();
        this.loadPartyItems();
        this.loadSpecItems();
        this.loadSpectatorModeItems();
    }
    
    private void loadSpawnItems() {
        this.spawnItems = new ItemStack[] { UtilItem.createItem(Material.IRON_SWORD, 1, (short)0, "§dUnranked Queue"), UtilItem.createItem(Material.DIAMOND_SWORD, 1, (short)0, "§dRanked Queue"),null, UtilItem.createItem(Material.CHEST, 1, (short)0, "§dShop"), null, UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§dCreate Party"), null, UtilItem.createItem(Material.REDSTONE_COMPARATOR, 1, (short)0, "§dSettings"), UtilItem.createItem(Material.BOOK, 1, (short)0, "§dKit Editor") };
    }
    
    private void loadPartyItems() {
        this.partyItems = new ItemStack[] {UtilItem.createItem(Material.GOLD_SWORD, 1, (short)0, "§d2v2 Queue"),null,UtilItem.createItem(Material.IRON_AXE, 1, (short)0, "§dSplit Fights"),null,UtilItem.createItem(Material.NAME_TAG, 1, (short)0, "§dParty Information") , null, UtilItem.createItem(Material.SKULL_ITEM, 1, (short)0, "§dFight Other Parties"),null, UtilItem.createItem(Material.REDSTONE, 1, (short)0, "§cLeave Party") };
    }
    
    private void loadSpecItems() {
        this.specItems = new ItemStack[] { UtilItem.createItem(Material.COMPASS, 1, (short)0, "§dInspect Inventory"), null, null, null, null, null, null, null, UtilItem.createItem(Material.REDSTONE, 1, (short)0, "§cReturn to Lobby") };
    }
    
    private void loadSpectatorModeItems() {
        this.spectatorModeItems = new ItemStack[] { UtilItem.createItem(Material.PAPER, 1, (short)0, "§dSpectate Random Match"), null, null, null,null, null,null,null, UtilItem.createItem(Material.NETHER_STAR, 1, (short)0, "§cLeave Spectator Mode")};
    }
    
    private void loadQueueItems() {
        this.queueItems = new ItemStack[] {  null, null, null, null, null, null, null, null ,UtilItem.createItem(Material.REDSTONE, 1, (short)0, "§cLeave Queue")};
    }
    
    public ItemStack[] getSpawnItems() {
        return this.spawnItems;
    }
    
    public ItemStack[] getQueueItems() {
        return this.queueItems;
    }
    
    public ItemStack[] getSpecItems() {
        return this.specItems;
    }
    
    public ItemStack[] getPartyItems() {
        return this.partyItems;
    }
    
    public ItemStack[] getSpectatorModeItems() {
        return this.spectatorModeItems;
    }
}
