package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.lang.*;
import org.bukkit.*;
import com.ubuntu.practice.player.*;

public class BuilderCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public BuilderCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.commands.builder")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "BUILDER_DISABLED"));
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return true;
        }
        practicePlayer.setCurrentState(PlayerState.BUILDER);
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "BUILDER_ENABLED"));
        return true;
    }
}
