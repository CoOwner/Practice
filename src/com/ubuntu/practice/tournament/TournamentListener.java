package com.ubuntu.practice.tournament;

import com.ubuntu.practice.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;

public class TournamentListener implements Listener
{
    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (Tournament.getTournaments().size() == 0) {
            return;
        }
        for (final Tournament tournament : Tournament.getTournaments()) {
            if (tournament.isInTournament(player)) {
                if (tournament.getTournamentMatch(player) != null) {
                    player.setHealth(0.0);
                }
                final TournamentTeam tournamentTeam = tournament.getTournamentTeam(player);
                tournamentTeam.setPlayers(null);
                tournament.getTeams().remove(tournamentTeam);
                tournament.getCurrentQueue().remove(player);
                uPractice.getInstance().getManagerHandler().getDuelManager().removePlayerFromDuel(player);
            }
        }
    }
    
    @EventHandler
    public void onRespawn(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        for (final Tournament tournament : Tournament.getTournaments()) {
            if (tournament.isInTournament(p) && p.getLocation().getWorld() == Bukkit.getServer().getWorld("spawn")) {
                e.setCancelled(true);
            }
        }
    }
}
