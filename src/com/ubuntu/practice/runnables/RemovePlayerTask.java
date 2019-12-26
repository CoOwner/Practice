package com.ubuntu.practice.runnables;

import org.bukkit.entity.*;
import com.ubuntu.practice.player.*;
import com.ubuntu.practice.*;

public class RemovePlayerTask implements Runnable
{
    private Player player;
    
    public RemovePlayerTask(final Player player) {
        this.player = player;
    }
    
    @Override
    public void run() {
        final PracticePlayer profile = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (profile != null) {
            profile.setGlobalPersonalElo(uPractice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, true));
            profile.setGlobalPartyElo(uPractice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, false));
            profile.save();
            PracticePlayer.getProfiles().remove(profile);
        }
    }
}
