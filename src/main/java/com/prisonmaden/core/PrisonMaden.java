package com.prisonmaden.core;

import com.prisonmaden.core.commands.MadenCommand;
import com.prisonmaden.core.commands.MadenZiyaretCommand;
import com.prisonmaden.core.gui.GUIManager;
import com.prisonmaden.core.listeners.BlockBreakListener;
import com.prisonmaden.core.listeners.GUIClickListener;
import com.prisonmaden.core.listeners.JoinListener;
import com.prisonmaden.core.listeners.PlayerInteractListener;
import com.prisonmaden.core.listeners.WorldSafetyListener;
import com.prisonmaden.core.managers.KitManager;
import com.prisonmaden.core.managers.MineWorldManager;
import com.prisonmaden.core.managers.PickaxeManager;
import com.prisonmaden.core.managers.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonMaden extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private MineWorldManager mineWorldManager;
    private KitManager kitManager;
    private PickaxeManager pickaxeManager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        this.playerDataManager = new PlayerDataManager(this);
        this.mineWorldManager = new MineWorldManager(this);
        this.kitManager = new KitManager(this);
        this.pickaxeManager = new PickaxeManager(this);
        this.guiManager = new GUIManager(this);

        playerDataManager.yukle();

        getCommand("maden").setExecutor(new MadenCommand(this));
        getCommand("madenziyaret").setExecutor(new MadenZiyaretCommand(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldSafetyListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIClickListener(this), this);

        getLogger().info("PrisonMaden basariyla etkinlestirildi!");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.kaydet();
        }
        getLogger().info("PrisonMaden devre disi birakildi.");
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public MineWorldManager getMineWorldManager() {
        return mineWorldManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public PickaxeManager getPickaxeManager() {
        return pickaxeManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }
}
