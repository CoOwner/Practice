package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.player.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;

public class MatchesCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public MatchesCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("unused")
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.matches.admin")) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(Lang.getLang().getLocalized(sender, "INCORRECT_USEAGE"));
            return true;
        }
        if (args.length == 2) {
            String name = "";
            final Player player = Bukkit.getPlayer(args[0]);
            UUID uuid;
            if (player != null) {
                uuid = player.getUniqueId();
                name = player.getName();
            }
            else {
                try {
                    final Map.Entry<UUID, String> recipient = PracticePlayer.getExternalPlayerInformation(args[0]);
                    uuid = recipient.getKey();
                    name = recipient.getValue();
                }
                catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Failed to find player.");
                    return true;
                }
            }
            final int amount = Integer.parseInt(args[1]);
            final PracticePlayer practicePlayer = PracticePlayer.getByUuid(uuid);
            if (practicePlayer == null) {
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    practicePlayer.save();
                }
            });
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "GIVEN_PREMIUM_MATCHES").replace("%amount%", String.valueOf(amount)).replace("%player%", player.getName()));
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "RECEIVED_PREMIUM_MATCHES").replace("%amount%", String.valueOf(amount)));
            }
        }
        else {
            final Player player2 = (Player)sender;
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player2, "INCORRECT_USAGE"));
        }
        return true;
    }
}
