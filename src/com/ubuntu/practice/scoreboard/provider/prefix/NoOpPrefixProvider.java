package com.ubuntu.practice.scoreboard.provider.prefix;

import com.ubuntu.practice.scoreboard.provider.*;
import org.bukkit.entity.*;

public class NoOpPrefixProvider implements PrefixProvider
{
    @Override
    public String getPrefix(final Player player) {
        return "";
    }
}
