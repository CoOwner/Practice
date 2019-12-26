package com.ubuntu.practice.scoreboard.provider.prefix;

import com.ubuntu.practice.scoreboard.provider.*;
import net.milkbowl.vault.chat.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.apache.commons.lang.*;

public class VaultPrefixProvider implements PrefixProvider
{
    private Chat chat;
    
    public VaultPrefixProvider() {
        this.setupChat();
    }
    
    private boolean setupChat() {
        final RegisteredServiceProvider rsp = Bukkit.getServer().getServicesManager().getRegistration((Class)Chat.class);
        this.chat = (Chat)rsp.getProvider();
        return this.chat != null;
    }
    
    @Override
    public String getPrefix(final Player player) {
        final String priamryGroup = this.chat.getPrimaryGroup(player);
        if (StringUtils.isEmpty(priamryGroup)) {
            return "";
        }
        final String prefix = this.chat.getGroupPrefix(player.getWorld(), priamryGroup);
        if (StringUtils.isEmpty(prefix)) {
            return "";
        }
        return prefix;
    }
}
