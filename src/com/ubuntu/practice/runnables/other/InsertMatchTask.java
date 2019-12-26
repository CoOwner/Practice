package com.ubuntu.practice.runnables.other;

import com.ubuntu.practice.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.duel.*;
import com.ubuntu.practice.player.*;

public class InsertMatchTask implements Runnable
{
    private uPractice plugin;
    private Player player;
    private Duel duel;
    private int winningTeamId;
    private String elo_change;
    
    public InsertMatchTask(final uPractice plugin, final Player player, final Duel duel, final int winningTeamId, final String elo_change) {
        this.player = player;
        this.plugin = plugin;
        this.winningTeamId = winningTeamId;
        this.elo_change = elo_change;
    }
    
    @Override
    public void run() {
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (practicePlayer != null) {}
    }
}
