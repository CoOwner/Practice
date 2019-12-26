package com.ubuntu.practice.commands;

import com.ubuntu.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.player.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import java.util.*;

public class CreditsCommand implements CommandExecutor
{
    private uPractice plugin;
    
    public CreditsCommand(final uPractice plugin) {
        this.plugin = plugin;
    }
    
    @SuppressWarnings("unused")
	public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.credits.admin")) {
            return true;
        }
        if (args.length == 0) {
            final Player player = (Player)sender;
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player, "INCORRECT_USAGE"));
            return true;
        }
        if (args.length == 2) {
            String name = "";
            final Player player2 = Bukkit.getPlayer(args[0]);
            UUID uuid;
            if (player2 != null) {
                uuid = player2.getUniqueId();
                name = player2.getName();
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
                    practicePlayer.setCredits(practicePlayer.getCredits() + amount);
                    practicePlayer.save();
                }
            });
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                Bukkit.getPlayer(uuid).sendMessage(Lang.getLang().getLocalized((CommandSender)player2, "RECEIVED_CREDITS").replace("%amount%", String.valueOf(amount)));
            }
            sender.sendMessage(Lang.getLang().getLocalized((CommandSender)player2, "GIVEN_CREDITS").replace("%player%", player2.getName().replace("%amount%", String.valueOf(amount))));
        }
        else {
            sender.sendMessage("Unknown Command.");
        }
        return true;
    }
}
