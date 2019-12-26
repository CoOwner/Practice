package com.ubuntu.practice.runnables.other;

import com.ubuntu.practice.*;

public class UpdateInventoryTask implements Runnable
{
    private uPractice plugin;
    private InventoryTaskType inventoryTaskType;
    
    public UpdateInventoryTask(final uPractice plugin, final InventoryTaskType inventoryTaskType) {
        this.plugin = plugin;
        this.inventoryTaskType = inventoryTaskType;
    }
    
    @Override
    public void run() {
        if (this.inventoryTaskType == InventoryTaskType.UNRANKED_SOLO) {
            this.plugin.getManagerHandler().getInventoryManager().setUnrankedInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.UNRANKED_PARTY) {
            this.plugin.getManagerHandler().getInventoryManager().setUnrankedPartyInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.RANKED_SOLO) {
            this.plugin.getManagerHandler().getInventoryManager().setRankedInventory();
        }
        else if (this.inventoryTaskType == InventoryTaskType.TOURNAMENT) {
            this.plugin.getManagerHandler().getInventoryManager().setJoinTournamentInventory();
        }
    }
    
    public enum InventoryTaskType
    {
        UNRANKED_SOLO, 
        RANKED_SOLO, 
        RANKED_PARTY, 
        UNRANKED_PARTY, 
        PREMIUM_SOLO, 
        SPECTATOR, 
        TOURNAMENT;
    }
}
