package com.ubuntu.practice.manager.managers;

import com.ubuntu.practice.arena.*;
import com.ubuntu.practice.*;
import com.ubuntu.practice.manager.*;
import org.bukkit.plugin.java.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.configuration.*;
import java.util.*;
import java.util.stream.*;
import com.ubuntu.practice.util.*;

public class ArenaManager extends Manager
{
    private Map<String, Arena> arenaMap;
    public Config mainConfig;
    private uPractice plugin;
    
    public ArenaManager(final ManagerHandler handler) {
        super(handler);
        this.arenaMap = new HashMap<String, Arena>();
        this.mainConfig = new Config(handler.getPlugin(), "", "arena");
        this.loadArenas();
    }
    
    public void disable() {
        this.saveArenas();
    }
    
    public Map<String, Arena> getArenaMap() {
        return this.arenaMap;
    }
    
    public Arena getArena(final String arenaName) {
        return this.getArenaMap().get(arenaName);
    }
    
    public Arena createArena(final String arenaName) {
        final Arena arena = new Arena(arenaName, null, null);
        this.arenaMap.put(arenaName, arena);
        return arena;
    }
    
    public Arena closest(final Location location) {
        double distancee = 262144.0;
        Arena closest = null;
        for (final Arena arena : this.getArenaMap().values()) {
            if (location.getWorld() == arena.getFirstTeamLocation().getWorld() && arena.getFirstTeamLocation().distanceSquared(location) < distancee) {
                distancee = arena.getFirstTeamLocation().distanceSquared(location);
                closest = arena;
            }
            if (location.getWorld() == arena.getSecondTeamLocation().getWorld()) {
                if (arena.getSecondTeamLocation().distanceSquared(location) >= distancee) {
                    continue;
                }
                distancee = arena.getSecondTeamLocation().distanceSquared(location);
                closest = arena;
            }
        }
        return closest;
    }
    
    public void destroyArena(final String arenaName) {
        this.arenaMap.remove(arenaName);
    }
    
    public void loadArenas() {
        final FileConfiguration fileConfig = this.mainConfig.getConfig();
        final ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
        if (arenaSection == null) {
            return;
        }
        for (final String arenaName : arenaSection.getKeys(false)) {
            final String serializedFirstTeamLocation = arenaSection.getString(arenaName + ".firstTeamLocation");
            final String serializedSecondTeamLocation = arenaSection.getString(arenaName + ".secondTeamLocation");
            final boolean enabled = arenaSection.getBoolean(arenaName + ".enabled");
            final Location firstTeamLocation = LocationSerializer.deserializeLocation(serializedFirstTeamLocation);
            final Location secondTeamLocation = LocationSerializer.deserializeLocation(serializedSecondTeamLocation);
            final Arena arena = new Arena(arenaName, firstTeamLocation, secondTeamLocation);
            arena.setEnabled(enabled);
            this.arenaMap.put(arenaName, arena);
        }
    }
    
    public void saveArenas() {
        final FileConfiguration fileConfig = this.mainConfig.getConfig();
        for (final Map.Entry<String, Arena> arenaEntry : this.arenaMap.entrySet()) {
            final String arenaName = arenaEntry.getKey();
            final Arena arena = arenaEntry.getValue();
            if (arena.getFirstTeamLocation() != null) {
                if (arena.getSecondTeamLocation() == null) {
                    continue;
                }
                final boolean enabled = arena.isEnabled();
                final List<String> kits = (List<String>)fileConfig.getStringList("arenas." + arenaName + ".kits");
                final String serializedFirstTeamLocation = LocationSerializer.serializeLocation(arena.getFirstTeamLocation());
                final String serializedSecondTeamLocation = LocationSerializer.serializeLocation(arena.getSecondTeamLocation());
                fileConfig.set("arenas." + arenaName + ".firstTeamLocation", (Object)null);
                fileConfig.set("arenas." + arenaName + ".secondTeamLocation", (Object)null);
                fileConfig.set("arenas." + arenaName + ".enabled", (Object)null);
                fileConfig.set("arenas." + arenaName + ".firstTeamLocation", (Object)serializedFirstTeamLocation);
                fileConfig.set("arenas." + arenaName + ".secondTeamLocation", (Object)serializedSecondTeamLocation);
                fileConfig.set("arenas." + arenaName + ".enabled", (Object)enabled);
                fileConfig.set("arenas." + arenaName + ".kits", (Object)kits);
            }
        }
        this.mainConfig.save();
    }
    
    public Arena getRandomArena() {
        final List arenas = this.arenaMap.values().stream().filter(Arena::isEnabled).filter(Arena::isOpen).collect(Collectors.toList());
        if (arenas.isEmpty()) {
            return null;
        }
        return (Arena) arenas.get(UtilMath.getRandom().nextInt(arenas.size()));
    }
    
    public List<Arena> getArenas() {
        final List arenas = this.arenaMap.values().stream().filter(Arena::isEnabled).filter(Arena::isOpen).collect(Collectors.toList());
        return (List<Arena>)arenas;
    }
}
