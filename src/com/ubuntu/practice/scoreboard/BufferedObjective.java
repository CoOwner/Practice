package com.ubuntu.practice.scoreboard;

import org.apache.commons.lang.*;
import net.minecraft.util.gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.util.gnu.trove.procedure.TIntObjectProcedure;

import org.bukkit.scoreboard.*;
import java.util.*;
import org.bukkit.*;

public class BufferedObjective
{
    private static int MAX_SIDEBAR_ENTRIES;
    private static int MAX_NAME_LENGTH;
    private static int MAX_PREFIX_LENGTH;
    private static int MAX_SUFFIX_LENGTH;
    private Scoreboard scoreboard;
    private Set<String> previousLines;
    private TIntObjectHashMap<SidebarEntry> contents;
    private boolean requiresUpdate;
    private String title;
    private Objective current;
    private DisplaySlot displaySlot;
    
    public BufferedObjective(final Scoreboard scoreboard) {
        this.previousLines = new HashSet<String>();
        this.contents = (TIntObjectHashMap<SidebarEntry>)new TIntObjectHashMap();
        this.requiresUpdate = false;
        this.scoreboard = scoreboard;
        this.title = RandomStringUtils.randomAlphabetic(4);
        this.current = scoreboard.registerNewObjective("buffered", "dummy");
    }
    
    public void setTitle(final String title) {
        if (this.title == null || !this.title.equals(title)) {
            this.title = title;
            this.requiresUpdate = true;
        }
    }
    
    public void setDisplaySlot(final DisplaySlot slot) {
        this.displaySlot = slot;
        this.current.setDisplaySlot(slot);
    }
    
    public void setAllLines(final List<SidebarEntry> lines) {
        if (lines.size() != this.contents.size()) {
            this.contents.clear();
            if (lines.isEmpty()) {
                this.requiresUpdate = true;
                return;
            }
        }
        final int size = Math.min(16, lines.size());
        int count = 0;
        for (final SidebarEntry sidebarEntry : lines) {
            this.setLine(size - count++, sidebarEntry);
        }
    }
    
    public void setLine(final int lineNumber, final SidebarEntry sidebarEntry) {
        final SidebarEntry value = (SidebarEntry)this.contents.get(lineNumber);
        if (value == null || !value.equals(sidebarEntry)) {
            this.contents.put(lineNumber, (SidebarEntry)sidebarEntry);
            this.requiresUpdate = true;
        }
    }
    
    @SuppressWarnings("unchecked")
	public void flip() {
        if (!this.requiresUpdate) {
            return;
        }
        final HashSet<String> adding = new HashSet<String>();
        this.contents.forEachEntry((TIntObjectProcedure)new TIntObjectProcedure<SidebarEntry>() {
            public boolean execute(final int i, final SidebarEntry sidebarEntry) {
                final String name = BufferedObjective.this.getInvisibleNameForIndex(i);
                Team team = BufferedObjective.this.scoreboard.getTeam(name);
                if (team == null) {
                    team = BufferedObjective.this.scoreboard.registerNewTeam(name);
                }
                String prefix;
                if ((prefix = sidebarEntry.prefix) != null) {
                    if (prefix.length() > 16) {
                        prefix = prefix.substring(0, 16);
                    }
                    team.setPrefix(prefix);
                }
                String suffix;
                if ((suffix = sidebarEntry.suffix) != null) {
                    if (suffix.length() > 16) {
                        suffix = suffix.substring(0, 16);
                    }
                    team.setSuffix(suffix);
                }
                adding.add(name);
                if (!team.hasEntry(name)) {
                    team.addEntry(name);
                }
                BufferedObjective.this.current.getScore(name).setScore(i);
                return true;
            }
        });
        this.previousLines.removeAll(adding);
        final Iterator<String> iterator = this.previousLines.iterator();
        while (iterator.hasNext()) {
            final String last = iterator.next();
            final Team team = this.scoreboard.getTeam(last);
            if (team != null) {
                team.removeEntry(last);
            }
            this.scoreboard.resetScores(last);
            iterator.remove();
        }
        this.previousLines = adding;
        this.current.setDisplayName(this.title);
        this.requiresUpdate = false;
    }
    
    private String getInvisibleNameForIndex(final int index) {
        return ChatColor.values()[index].toString() + ChatColor.RESET;
    }
    
    public void setVisible(final boolean value) {
        if (this.displaySlot != null && !value) {
            this.scoreboard.clearSlot(this.displaySlot);
            this.displaySlot = null;
        }
        else if (this.displaySlot == null && value) {
            this.displaySlot = DisplaySlot.SIDEBAR;
            this.current.setDisplaySlot(this.displaySlot);
        }
    }
    
    static {
        BufferedObjective.MAX_SIDEBAR_ENTRIES = 16;
        BufferedObjective.MAX_NAME_LENGTH = 16;
        BufferedObjective.MAX_PREFIX_LENGTH = 16;
        BufferedObjective.MAX_SUFFIX_LENGTH = 16;
    }
}
