package com.ubuntu.practice.listeners;

import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import java.text.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.*;

import com.ubuntu.practice.uPractice;
import com.ubuntu.practice.player.PlayerState;
import com.ubuntu.practice.player.PracticePlayer;

import org.bukkit.plugin.*;

public class XpListener implements Listener
{
    private static WeakHashMap<Player, Long> enderpearlCooldown;
    private int cooldowntime;
    private double cooldowntimedouble;
    private float removeThatxp;
    private boolean xpbarenabled;
    static XpListener instance;
    
    static {
        XpListener.instance = new XpListener();
    }
    
    public XpListener() {
        this.enderpearlCooldown = new WeakHashMap<Player, Long>();
        this.cooldowntime = 14;
        this.cooldowntimedouble = 14.0;
        this.removeThatxp = 0.99f / ((float)(this.cooldowntimedouble * 20.0) / 2.0f);
        this.xpbarenabled = true;
    }
    
    public static XpListener getInstance() {
        return XpListener.instance;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if (e.getPlayer() instanceof Player) {
            final Player p = e.getPlayer();
            final PracticePlayer practicePlayer = uPractice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(p);
            if (e.hasItem()) {
                final ItemStack item = e.getItem();
                if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && item.getType() == Material.ENDER_PEARL && p.getGameMode() != GameMode.CREATIVE) {
                    if (practicePlayer.getCurrentState() != PlayerState.FIGHTING) {
                        e.setCancelled(true);
                        return;
                    }        
                	this.applyCooldown(p);
                 }
            }
        }
    }
    
    public boolean isEnderPearlCooldownActive(final Player p) {
        return this.enderpearlCooldown.containsKey(p) && this.enderpearlCooldown.get(p) > System.currentTimeMillis();
    }
    
    public long getEnderPearlCooldown(final Player p) {
        if (this.enderpearlCooldown.containsKey(p)) {
            return Math.max(0L, this.enderpearlCooldown.get(p) - System.currentTimeMillis());
        }
        return 0L;
    }
    
    public void applyCooldown(final Player p) {
        this.enderpearlCooldown.put(p, System.currentTimeMillis() + this.cooldowntime * 1000);
        if (this.xpbarenabled) {
            p.setExp(0.99f);
            new BukkitRunnable() {
                @SuppressWarnings("unused")
				public void run() {
                    if (p != null) {
                        if (XpListener.this.isEnderPearlCooldownActive(p)) {
                            if (p.getGameMode() != GameMode.CREATIVE) {
                                    p.setExp(p.getExp() - XpListener.this.removeThatxp);
                                }
                                else {
                                    XpListener.this.removeCooldown(p);
                                    this.cancel();
                                }
                            }
                            else {
                                XpListener.this.removeCooldown(p);
                                this.cancel();
                            }
                        }
                        else {
                            p.sendMessage("§dYour pearl cooldown has expired!");
                            this.cancel();
                        }
                }
                        
            }.runTaskTimerAsynchronously((Plugin)uPractice.getInstance(), 0L, 2L);
        }
    }
    
    
    public static void removeCooldown(final Player p) {
        if (XpListener.enderpearlCooldown.containsKey(p)) {
            XpListener.enderpearlCooldown.remove(p);
            p.setLevel(0);
            p.setExp(0.0f);
        }
    }
}
