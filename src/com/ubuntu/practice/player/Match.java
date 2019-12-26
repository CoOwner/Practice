package com.ubuntu.practice.player;

import java.util.*;

public class Match
{
    UUID matchId;
    private String firstTeam;
    private String secondTeam;
    boolean ranked;
    String items;
    String armor;
    String potions;
    int eloChangeWinner;
    int eloChangeLoser;
    int winningTeamId;
    
    public UUID getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(UUID matchId) {
        matchId = matchId;
    }
    
    public String getFirstTeam() {
        return this.firstTeam;
    }
    
    public void setFirstTeam(String firstTeam) {
        firstTeam = firstTeam;
    }
    
    public String getSecondTeam() {
        return this.secondTeam;
    }
    
    public void setSecondTeam(String secondTeam) {
        secondTeam = secondTeam;
    }
    
    public boolean isRanked() {
        return this.ranked;
    }
    
    public void setRanked(boolean ranked) {
        ranked = ranked;
    }
    
    public String getItems() {
        return this.items;
    }
    
    public void setItems(String items) {
        items = items;
    }
    
    public String getArmor() {
        return this.armor;
    }
    
    public void setArmor(String armor) {
        armor = armor;
    }
    
    public String getPotions() {
        return this.potions;
    }
    
    public void setPotions(String potions) {
        potions = potions;
    }
    
    public int getEloChangeWinner() {
        return this.eloChangeWinner;
    }
    
    public void setEloChangeWinner(int eloChangeWinner) {
        eloChangeWinner = eloChangeWinner;
    }
    
    public int getEloChangeLoser() {
        return this.eloChangeLoser;
    }
    
    public void setEloChangeLoser(int eloChangeLoser) {
        eloChangeLoser = eloChangeLoser;
    }
    
    public int getWinningTeamId() {
        return this.winningTeamId;
    }
    
    public void setWinningTeamId(int winningTeamId) {
        winningTeamId = winningTeamId;
    }
}
