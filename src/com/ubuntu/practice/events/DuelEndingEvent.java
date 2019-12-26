package com.ubuntu.practice.events;

import org.bukkit.event.*;
import com.ubuntu.practice.duel.*;

public class DuelEndingEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    private int teamNumber;
    
    public DuelEndingEvent(final Duel duel, final int teamNumber) {
        this.duel = duel;
        this.teamNumber = teamNumber;
    }
    
    public DuelEndingEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public int getTeamNumber() {
        return this.teamNumber;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelEndingEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return DuelEndingEvent.handlerList;
    }
    
    static {
        DuelEndingEvent.handlerList = new HandlerList();
    }
}
