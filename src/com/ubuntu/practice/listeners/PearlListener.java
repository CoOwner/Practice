package com.ubuntu.practice.listeners;

import com.ubuntu.practice.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class PearlListener implements Listener
{
    private uPractice plugin;
    
    public PearlListener(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPearlClip(final PlayerTeleportEvent event) {
        if (event.getCause().equals((Object)PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            final Location location = event.getTo();
            location.setX(location.getBlockX() + 0.5);
            location.setY((double)location.getBlockY());
            location.setZ(location.getBlockZ() + 0.5);
            event.setTo(location);
        }
    }
}
