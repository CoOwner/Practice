package com.ubuntu.practice.settings;

import com.ubuntu.practice.*;

import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;

import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.*;
import com.google.common.collect.*;
import org.bukkit.*;
import java.util.*;
import com.google.common.base.*;
import javax.annotation.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class SettingsHandler implements CommandExecutor, Listener
{
    private uPractice plugin;
    
    public SettingsHandler(final uPractice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        plugin.getCommand("setting").setExecutor((CommandExecutor)this);
    }
    
    public boolean onCommand(final CommandSender sender, final Command arg1, final String arg2, final String[] args) {
        this.open((Player)sender);
        return true;
    }
    
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equals("Options")) {
            event.setCancelled(true);
            final Player player = (Player)event.getWhoClicked();
            final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
            final int slot = event.getSlot();
            if (slot == 2) {
                settings.setTime(!settings.isTime());
                ((Player)event.getWhoClicked()).performCommand("scoreboard");
            }
            else if (slot == 6) {
                settings.setDuelRequests(!settings.isDuelRequests());
                player.sendMessage(ChatColor.YELLOW + "You are now " + this.able(settings.isDuelRequests()) + ChatColor.YELLOW + " to receive duel requests.");
            }
            else if (slot == 4) {
                settings.setMessage(!settings.isMessage());
                ((Player)event.getWhoClicked()).performCommand("famousmod");
            }
            this.open((Player)event.getWhoClicked());
        }
    }
    
    public String able(final boolean value) {
        return value ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable");
    }
    
    public void open(final Player player) {
        final Settings settings = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player.getUniqueId()).getSettings();
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, "Options");
        final String enabled = ChatColor.BLUE + StringEscapeUtils.unescapeHtml4("&#9658;") + " ";
        ArrayList lore = Lists.newArrayList();
        lore.add("  " + (settings.isTime() ? enabled : " ") + ChatColor.GRAY + "Show scoreboard");
        lore.add("  " + (settings.isTime() ? " " : enabled) + ChatColor.GRAY + "Hide scoreboard");
        String name = ChatColor.LIGHT_PURPLE + "Scoreboard";
        inventory.setItem(2, this.create(Material.PAINTING, name, lore));
        lore = Lists.newArrayList();
        lore.add("  " + (settings.isDuelRequests() ? enabled : " ") + ChatColor.GRAY + "Show requests");
        lore.add("  " + (settings.isDuelRequests() ? " " : enabled) + ChatColor.GRAY + "Hide requests");
        name = ChatColor.LIGHT_PURPLE + "Duel Requests";
        inventory.setItem(6, this.create(Material.DIAMOND_SWORD, (short)0, name, lore));
        lore = Lists.newArrayList();
        lore.add("  " + (settings.isMessage() ? enabled : " ") + ChatColor.GRAY + "Toggled on");
        lore.add("  " + (settings.isMessage() ? " " : enabled) + ChatColor.GRAY + "Toggled off");
        name = ChatColor.LIGHT_PURPLE + "Private Message";
        inventory.setItem(4, this.create(Material.PAPER, name, lore));
        player.openInventory(inventory);
    }
    
    public static List<String> translate(final List<String> input) {
        return (List<String>)Lists.newArrayList((Iterable)Lists.transform((List)input, (Function)new Function<String, String>() {
            public String apply(@Nullable final String s) {
                return ChatColor.translateAlternateColorCodes('&', s);
            }
        }));
    }
    
    public ItemStack create(final Material material, final String name, final List<String> lore) {
        return this.create(material, (short)0, name, lore);
    }
    
    public ItemStack create(final Material material, final short data, final String name, final List<String> lore) {
        final ItemStack itemstack = new ItemStack(material, 1, data);
        final ItemMeta meta = itemstack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore((List)translate(lore));
        itemstack.setItemMeta(meta);
        return itemstack;
    }
}
