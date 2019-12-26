package com.ubuntu.practice;

import org.bukkit.plugin.java.*;
import com.ubuntu.practice.manager.*;
import com.ubuntu.practice.util.database.*;


import java.util.regex.*;
import com.ubuntu.practice.scoreboard.provider.*;
import com.ubuntu.practice.scoreboard.config.*;
import org.bukkit.configuration.file.*;
import org.bukkit.*;
import com.ubuntu.practice.scoreboard.*;
import com.ubuntu.practice.util.*;
import com.ubuntu.practice.scoreboard.provider.prefix.*;
import org.bukkit.event.*;
import com.ubuntu.practice.listeners.*;
import com.ubuntu.practice.tournament.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import com.ubuntu.practice.lang.*;
import com.ubuntu.practice.commands.*;
import com.ubuntu.practice.settings.*;

public class uPractice extends JavaPlugin
{
    private static uPractice instance;
    private ManagerHandler managerHandler;
    PracticeDatabase practiceDatabase;
    private Location spawn;
    public static Pattern splitPattern;
    public static Pattern UUID_PATTER;
    public static String MAIN_COLOR;
    public static String SIDE_COLOR;
    public static String EXTRA_COLOR;
    private PrefixProvider prefixProvider;
    private ScoreboardConfig scoreboardConfig;
    
    
    public void onEnable() {
        uPractice.instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        final ConfigurationWrapper languageConfig = new ConfigurationWrapper("lang.yml", this);
        languageConfig.saveDefault();
        Lang.getLang().add((FileConfiguration)languageConfig.getConfig());
        final ConfigurationWrapper scoreboardConfig = new ConfigurationWrapper("scoreboard.yml", this);
        scoreboardConfig.saveDefault();
        this.scoreboardConfig = new ScoreboardConfig((FileConfiguration)scoreboardConfig.getConfig());
        SidebarProvider.SCOREBOARD_TITLE = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("SCOREBOARD_TITLE"));
        this.practiceDatabase = new PracticeDatabase(this);
        this.managerHandler = new ManagerHandler(this);
        if (this.getConfig().contains("spawn")) {
            this.spawn = LocationSerializer.deserializeLocation(this.getConfig().getString("spawn"));
        }
        try {
            uPractice.MAIN_COLOR = ChatColor.valueOf(this.getConfig().getString("COLORS.MAIN_COLOR").toUpperCase()).toString();
            uPractice.SIDE_COLOR = ChatColor.valueOf(this.getConfig().getString("COLORS.SIDE_COLOR").toUpperCase()).toString();
            uPractice.EXTRA_COLOR = ChatColor.valueOf(this.getConfig().getString("COLORS.EXTRA_COLOR").toUpperCase()).toString();
        }
        catch (Exception e) {
            System.out.print(e.getClass() + ": " + e.getMessage());
            System.out.print("Oops your config colors are invalid, colors has been set to default.");
            System.out.print("Oops your config colors are invalid, colors has been set to default.");
            System.out.print("Oops your config colors are invalid, colors has been set to default.");
        }
        this.registerListeners();
        this.registerCommands();
        uPractice.splitPattern = Pattern.compile("\\s");
        this.prefixProvider = new VaultPrefixProvider();
    }
    
    public void onDisable() {
        this.saveConfig();
        this.reloadConfig();
        this.managerHandler.disable();
    }
    
    private void registerListeners() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents((Listener)new PlayerListener(this), (Plugin)this);
        pm.registerEvents((Listener)new PearlListener(this), (Plugin)this);
        pm.registerEvents((Listener)new HitDetectionListener(this), (Plugin)this);
        pm.registerEvents((Listener)new InventoryListener(this), (Plugin)this);
        pm.registerEvents((Listener)new EntityListener(this), (Plugin)this);
        pm.registerEvents((Listener)new DuelListener(this), (Plugin)this);
        pm.registerEvents((Listener)new BlockListener(this), (Plugin)this);
        pm.registerEvents((Listener)new TournamentListener(), (Plugin)this);
        pm.registerEvents((Listener)new XpListener(), (Plugin)this);
    }
    
    private void registerCommands() {
        this.getCommand("arena").setExecutor((CommandExecutor)new ArenaCommand(this));
        this.getCommand("duel").setExecutor((CommandExecutor)new DuelCommand(this));
        this.getCommand("accept").setExecutor((CommandExecutor)new AcceptCommand(this));
        this.getCommand("kit").setExecutor((CommandExecutor)new KitCommand(this));
        this.getCommand("spectate").setExecutor((CommandExecutor)new SpectateCommand(this));
        this.getCommand("builder").setExecutor((CommandExecutor)new BuilderCommand(this));
        this.getCommand("inventory").setExecutor((CommandExecutor)new InventoryCommand(this));
        this.getCommand("party").setExecutor((CommandExecutor)new PartyCommand(this));
        this.getCommand("reset").setExecutor((CommandExecutor)new ResetEloCommand(this));
        this.getCommand("join").setExecutor((CommandExecutor)new JoinCommand(this));
        this.getCommand("leave").setExecutor((CommandExecutor)new LeaveCommand(this));
        this.getCommand("credits").setExecutor((CommandExecutor)new CreditsCommand(this));
        this.getCommand("tournament").setExecutor((CommandExecutor)new TournamentCommand(this));
        this.getCommand("scoreboard").setExecutor((CommandExecutor)new ScoreboardCommand(this));
        this.getCommand("setspawn").setExecutor((CommandExecutor)new SetSpawnCommand(this));
        this.getCommand("host").setExecutor((CommandExecutor)new HostCommand(this));
        this.getCommand("ping").setExecutor((CommandExecutor)new PingCommand(this));
        this.getCommand("setting").setExecutor((CommandExecutor)new SettingsHandler(this));
    }
    
    public ManagerHandler getManagerHandler() {
        return this.managerHandler;
    }
    
    public static uPractice getInstance() {
        return uPractice.instance;
    }
    
    public PracticeDatabase getPracticeDatabase() {
        return this.practiceDatabase;
    }
    
    public Location getSpawn() {
        return this.spawn;
    }
    
    public void setSpawn(final Location spawn) {
        this.spawn = spawn;
    }
    
    public PrefixProvider getPrefixProvider() {
        return this.prefixProvider;
    }
    
    public ScoreboardConfig getScoreboardConfig() {
        return this.scoreboardConfig;
    }
    
    static {
        uPractice.MAIN_COLOR = ChatColor.DARK_GREEN.toString();
        uPractice.SIDE_COLOR = ChatColor.GREEN.toString();
        uPractice.EXTRA_COLOR = ChatColor.GRAY.toString();
        uPractice.UUID_PATTER = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    }
}
