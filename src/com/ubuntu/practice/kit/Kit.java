package com.ubuntu.practice.kit;

import org.bukkit.inventory.*;
import java.util.*;

public class Kit
{
    private boolean enabled;
    private String name;
    private ItemStack icon;
    private boolean combo;
    private boolean editable;
    private boolean ranked;
    private boolean builduhc;
    private ItemStack[] mainContents;
    private ItemStack[] armorContents;
    
    public Kit(final String name, final ItemStack icon, final boolean combo, final boolean editable, final boolean ranked, final ItemStack[] mainContents, final ItemStack[] armorContents, final boolean premium, final boolean build) {
        this.name = name;
        this.icon = icon;
        this.combo = combo;
        this.editable = editable;
        this.ranked = ranked;
        this.mainContents = mainContents;
        this.armorContents = armorContents;
        this.builduhc = build;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ItemStack getIcon() {
        return this.icon;
    }
    
    public boolean isCombo() {
        return this.combo;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    
    public ItemStack[] getMainContents() {
        return this.mainContents;
    }
    
    public ItemStack[] getArmorContents() {
        return this.armorContents;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setIcon(final ItemStack icon) {
        this.icon = icon;
    }
    
    public void setCombo(final boolean combo) {
        this.combo = combo;
    }
    
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }
    
    public void setRanked(final boolean ranked) {
        this.ranked = ranked;
    }

    
    public void setMainContents(final ItemStack[] mainContents) {
        this.mainContents = mainContents;
    }
    
    public void setArmorContents(final ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }
    
    public List<ItemStack> getEditableContents() {
        final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (final ItemStack item : this.getMainContents()) {
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        return items;
    }
    
    public boolean isBuilduhc() {
        return this.builduhc;
    }
    
    public void setBuilduhc(final boolean builduhc) {
        this.builduhc = builduhc;
    }
}
