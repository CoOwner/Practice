package com.ubuntu.practice.scoreboard.provider.sidebar;

import com.ubuntu.practice.*;
import org.bukkit.entity.*;
import java.util.*;
import com.ubuntu.practice.scoreboard.*;
import com.ubuntu.practice.scoreboard.config.*;
import com.ubuntu.practice.player.*;

public class LobbyScoreboardProvider extends SidebarProvider
{
    private uPractice plugin;
    
    public LobbyScoreboardProvider(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player player) {
        return LobbyScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer == null || !practicePlayer.getSettings().isScoreboard()) {
            return null;
        }
        final List<String> entries = this.plugin.getScoreboardConfig().get(ScoreboardType.LOBBY);
        return this.preprocess(player, entries);
    }
}
