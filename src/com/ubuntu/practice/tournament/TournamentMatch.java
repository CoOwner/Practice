
package com.ubuntu.practice.tournament;

import com.ubuntu.practice.*;
import com.ubuntu.practice.kit.*;
import org.bukkit.*;
import com.ubuntu.practice.arena.*;
import java.util.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;

public class TournamentMatch
{
    private TournamentTeam firstTeam;
    private TournamentTeam secondTeam;
    private List<UUID> matchPlayers;
    private MatchState matchState;
    private int winndingId;
    
    public void start(final uPractice plugin, final Kit defaultKit) {
        (this.matchPlayers = new ArrayList<UUID>()).addAll(this.firstTeam.getPlayers());
        this.matchPlayers.addAll(this.secondTeam.getPlayers());
        Bukkit.getScheduler().runTaskLater((Plugin)plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                Arena arena = plugin.getManagerHandler().getArenaManager().getRandomArena();
                final List<Arena> avaiable = new ArrayList<Arena>();
                for (final Arena allarenas : plugin.getManagerHandler().getArenaManager().getArenas()) {
                    avaiable.add(allarenas);
                }
                for (final Arena allarenas : plugin.getManagerHandler().getArenaManager().getArenas()) {
                    for (arena = plugin.getManagerHandler().getArenaManager().getRandomArena(); !avaiable.contains(arena); arena = plugin.getManagerHandler().getArenaManager().getRandomArena()) {}
                    final FileConfiguration fileConfig = plugin.getManagerHandler().getArenaManager().mainConfig.getConfig();
                    final List<String> kits = (List<String>)fileConfig.getStringList("arenas." + arena.getName() + ".kits");
                    if (kits.contains(defaultKit.getName())) {
                        plugin.getManagerHandler().getDuelManager().createDuel(arena, defaultKit, false, TournamentMatch.this.firstTeam.getPlayers().get(0), TournamentMatch.this.secondTeam.getPlayers().get(0), TournamentMatch.this.firstTeam.getPlayers(), TournamentMatch.this.secondTeam.getPlayers(), true);
                        return;
                    }
                    avaiable.remove(arena);
                }
            }
        }, 40L);
        this.matchState = MatchState.FIGHTING;
        this.winndingId = 0;
    }
    
    public TournamentTeam getTournamentTeam(final TournamentTeam team) {
        if (this.firstTeam == team) {
            return this.firstTeam;
        }
        if (this.secondTeam == team) {
            return this.secondTeam;
        }
        return null;
    }
    
    public TournamentTeam getOtherTeam(final TournamentTeam tournamentTeam) {
        return this.getOtherDuelTeam(this.getTournamentTeam(tournamentTeam));
    }
    
    public TournamentTeam getOtherDuelTeam(final TournamentTeam duelTeam) {
        return (duelTeam == null) ? null : (duelTeam.equals(this.firstTeam) ? this.secondTeam : this.firstTeam);
    }
    
    public TournamentTeam getFirstTeam() {
        return this.firstTeam;
    }
    
    public void setFirstTeam(final TournamentTeam firstTeam) {
        this.firstTeam = firstTeam;
    }
    
    public TournamentTeam getSecondTeam() {
        return this.secondTeam;
    }
    
    public void setSecondTeam(final TournamentTeam secondTeam) {
        this.secondTeam = secondTeam;
    }
    
    public List<UUID> getMatchPlayers() {
        return this.matchPlayers;
    }
    
    public void setMatchPlayers(final List<UUID> matchPlayers) {
        this.matchPlayers = matchPlayers;
    }
    
    public MatchState getMatchState() {
        return this.matchState;
    }
    
    public void setMatchState(final MatchState matchState) {
        this.matchState = matchState;
    }
    
    public int getWinndingId() {
        return this.winndingId;
    }
    
    public void setWinndingId(final int winndingId) {
        this.winndingId = winndingId;
    }
    
    public enum MatchState
    {
        WAITING, 
        FIGHTING, 
        ENDING;
    }
}
