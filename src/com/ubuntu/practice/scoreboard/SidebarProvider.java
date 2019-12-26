package com.ubuntu.practice.scoreboard;

import org.bukkit.entity.*;
import com.ubuntu.practice.*;
import com.ubuntu.practice.listeners.*;
import java.text.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.scoreboard.config.*;
import com.ubuntu.practice.duel.*;
import java.util.*;
import org.apache.commons.lang.time.*;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import com.google.common.base.*;

public abstract class SidebarProvider
{
    public static String SCOREBOARD_TITLE;
    protected static String STRAIGHT_LINE;
    
    public abstract String getTitle(final Player p0);
    
    public abstract List<SidebarEntry> getLines(final Player p0);
    
    public List<SidebarEntry> preprocess(final Player player, final List<String> list) {
        final PracticePlayer practicePlayer = uPractice.getInstance().getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        final Duel duel = uPractice.getInstance().getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId());
        final ArrayList<SidebarEntry> copy = new ArrayList<SidebarEntry>();
        for (String line : list) {
            if (duel != null) {
                if (line.contains("%enderpearl%")) {
                    if (!PlayerListener.getLastPearl().containsKey(player.getUniqueId())) {
                        continue;
                    }
                    final long now;
                    final double diff;
                    final double result;
                    if ((result = 15.0 - (diff = (now = System.currentTimeMillis()) - PlayerListener.getLastPearl().get(player.getUniqueId())) / 1000.0) <= 0.0) {
                        continue;
                    }
                    line = line.replace("%enderpearl%", new DecimalFormat("#0.0").format(result) + "s");
                }
                if (line.contains("%opponent%")) {
                    if (duel.getFfaPlayers() != null && duel.getFfaPlayers().size() > 0) {
                        continue;
                    }
                    final boolean bl;
                    final boolean isParty = bl = (duel.getOtherDuelTeam(player).size() >= 2);
                    final String opponent = (duel.getOtherDuelTeam(player).get(0) != null) ? Bukkit.getOfflinePlayer((UUID)duel.getOtherDuelTeam(player).get(0)).getName() : ("Undefined" + (isParty ? "'s Party" : ""));
                    line = line.replace("%opponent%", opponent);
                }
                line = line.replace("%time_left%", this.getRemaining((duel.getEndMatchTime() != 0L) ? duel.getDuration() : duel.getStartDuration()));
            }
            line = line.replace("%online_players%", String.valueOf(Bukkit.getServer().getOnlinePlayers().length));
            line = line.replace("%in_fights%", String.valueOf(this.getTotalInGame()));
            line = line.replace("%in_queue%", String.valueOf(uPractice.getInstance().getManagerHandler().getQueueManager().getTotalInQueues()));
            copy.add(new SidebarEntry(line));
        }
        return copy;
    }
    
    private int getTotalInGame() {
        int count = 0;
        for (final Duel duel : uPractice.getInstance().getManagerHandler().getDuelManager().getUuidIdentifierToDuel().values()) {
            if (duel.getFirstTeam() != null) {
                count += duel.getFirstTeam().size();
            }
            if (duel.getSecondTeam() != null) {
                count += duel.getSecondTeam().size();
            }
            if (duel.getFfaPlayers() == null) {
                continue;
            }
            count += duel.getFfaPlayers().size();
        }
        return count;
    }
    
    private String getRemaining(final long duration) {
        return DurationFormatUtils.formatDuration(duration, "mm:ss");
    }
    
    static {
        SidebarProvider.STRAIGHT_LINE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256).substring(0, 10);
    }
}
