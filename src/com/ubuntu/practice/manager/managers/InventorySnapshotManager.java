package com.ubuntu.practice.manager.managers;

import java.util.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.manager.*;
import java.util.concurrent.*;
import com.ubuntu.practice.util.*;

public class InventorySnapshotManager extends Manager
{
    private Map<UUID, PlayerInventorySnapshot> snapshotMap;
    
    public InventorySnapshotManager(final ManagerHandler handler) {
        super(handler);
        this.snapshotMap = new TtlHashMap<UUID, PlayerInventorySnapshot>(TimeUnit.MINUTES, 1L);
    }
    
    public void addSnapshot(final UUID uuid, final PlayerInventorySnapshot playerInventorySnapshot) {
        this.snapshotMap.put(uuid, playerInventorySnapshot);
    }
    
    public PlayerInventorySnapshot getSnapshotFromUUID(final UUID uuid) {
        return this.snapshotMap.get(uuid);
    }
}
