package com.ubuntu.practice.scoreboard.provider.sidebar;

import com.ubuntu.practice.*;
import org.bukkit.entity.*;
import java.util.*;
import com.ubuntu.practice.scoreboard.*;
import com.ubuntu.practice.scoreboard.config.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.duel.*;

public class DuelScoreboardProvider extends SidebarProvider
{
    private uPractice plugin;
    
    public DuelScoreboardProvider(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player paramPlayer) {
        return DuelScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer == null || !practicePlayer.getSettings().isScoreboard()) {
            return null;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId());
        final List<String> entries = (duel == null) ? this.plugin.getScoreboardConfig().get(ScoreboardType.LOADING) : ((duel.getFfaPlayers() != null && duel.getFfaPlayers().size() > 0) ? this.plugin.getScoreboardConfig().get(ScoreboardType.DUEL_FFA) : this.plugin.getScoreboardConfig().get(ScoreboardType.DUEL_NORMAL));
        return this.preprocess(player, entries);
    }
}
