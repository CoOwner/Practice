package com.ubuntu.practice.util;

import org.bukkit.*;

public class Messages
{
    public static String PLAYER_NOT_FOUND;
    public static String PLAYER_BUSY;
    public static String WAIT_SENDING_DUEL;
    public static String NO_REQUESTS_FOUND;
    public static String WAIT_EPEARL;
    public static String REQUESTED_PLAYER_NOT_IN_FIGHT;
    public static String NO_PERMISSION;
    public static String CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE;
    public static String CANNOT_FIND_SNAPSHOT;
    public static String CANNOT_DUEL_YOURSELF;
    public static String SPECIFY_KIT;
    
    static {
        Messages.PLAYER_NOT_FOUND = ChatColor.RED + "Player not found";
        Messages.PLAYER_BUSY = ChatColor.RED + "This player is currently busy";
        Messages.WAIT_SENDING_DUEL = ChatColor.RED + "You already sent a duel request to this player, you must wait before sending one again.";
        Messages.NO_REQUESTS_FOUND = ChatColor.RED + "You do not have any pending requests";
        Messages.WAIT_EPEARL = ChatColor.RED + "You must wait before throwing another ender pearl";
        Messages.REQUESTED_PLAYER_NOT_IN_FIGHT = ChatColor.RED + "The requested player is currently not in a fight";
        Messages.NO_PERMISSION = ChatColor.WHITE + "Unknown Command.";
        Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE = ChatColor.RED + "Cannot execute this command in your current state";
        Messages.CANNOT_FIND_SNAPSHOT = ChatColor.RED + "Cannot find the requested inventory. Maybe it expired?";
        Messages.CANNOT_DUEL_YOURSELF = ChatColor.RED + "Cannot duel yourself!";
        Messages.SPECIFY_KIT = ChatColor.RED + "Please Specify Kit";
    }
}
