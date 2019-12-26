package com.ubuntu.practice.tournament;

import org.apache.commons.lang.*;
import com.google.common.base.*;

public enum TournamentStage
{
    FIRST_ROUND, 
    SECOND_ROUND, 
    THIRD_ROUND, 
    QUARTER_S, 
    SEMI_S, 
    S;
    
    public String toReadable() {
        final String[] split = this.name().split("_");
        for (int i = 0; i < split.length; ++i) {
            split[i] = WordUtils.capitalize(split[i].toLowerCase());
        }
        return Joiner.on(" ").join((Object[])split);
    }
}
