package com.ubuntu.practice.tournament.tasks;

import org.bukkit.scheduler.*;
import com.ubuntu.practice.tournament.*;
import com.ubuntu.practice.runnables.other.*;
import org.bukkit.plugin.*;

public class TournamentTask extends BukkitRunnable
{
    private Tournament tournament;
    
    public TournamentTask(final Tournament tournament) {
        this.tournament = tournament;
    }
    
    public void run() {
        this.tournament.generateRoundMatches();
        this.tournament.getPlugin().getServer().getScheduler().runTask((Plugin)this.tournament.getPlugin(), (Runnable)new UpdateInventoryTask(this.tournament.getPlugin(), UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
    }
}
