package com.ubuntu.practice.listeners;

import com.ubuntu.practice.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class HitDetectionListener implements Listener
{
    private uPractice plugin;
    
    public HitDetectionListener(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)uPractice.getInstance());
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.setMaximumNoDamageTicks(19);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        event.getPlayer().setMaximumNoDamageTicks(19);
    }
}
