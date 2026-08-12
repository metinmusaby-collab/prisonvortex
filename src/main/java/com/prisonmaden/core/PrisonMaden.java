package com.prisonmaden.core;

import com.prisonmaden.core.commands.CantaCommand;
import com.prisonmaden.core.commands.KitCommand;
import com.prisonmaden.core.commands.MadenCommand;
import com.prisonmaden.core.commands.MadenZiyaretCommand;
import com.prisonmaden.core.commands.RankupCommand;
import com.prisonmaden.core.gui.GUIManager;
import com.prisonmaden.core.listeners.BlockBreakListener;
import com.prisonmaden.core.listeners.EntityInteractListener;
import com.prisonmaden.core.listeners.GUIClickListener;
import com.prisonmaden.core.listeners.JoinListener;
import com.prisonmaden.core.listeners.PlayerInteractListener;
import com.prisonmaden.core.listeners.WorldChangeListener;
import com.prisonmaden.core.listeners.WorldSafetyListener;
import com.prisonmaden.core.managers.EkonomiManager;
import com.prisonmaden.core.managers.KitManager;
import com.prisonmaden.core.managers.MineWorldManager;
import com.prisonmaden.core.managers.MinyonManager;
import com.prisonmaden.core.managers.NPCManager;
import com.prisonmaden.core.managers.PickaxeManager;
import com.prisonmaden.core.managers.PlayerDataManager;
import com.prisonmaden.core.managers.RankManager;
import com.prisonmaden.core.managers.UcusManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonMaden extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private MineWorldManager mineWorldManager;
    private KitManager kitManager;
    private PickaxeManager pickaxeManager;
    private GUIManager guiManager;
    private EkonomiManager ekonomiManager;
    private MinyonManager minyonManager;
    private RankManager rankManager;
    private NPCManager npcManager;
    private UcusManager ucusManager;

    @Override
    public void onEnable() {
        this.playerDataManager = new PlayerDataManager(this);
        this.ekonomiManager = new EkonomiManager(this);
        this.mineWorldManager = new MineWorldManager(this);
        this.kitManager = new KitManager(this);
        this.pickaxeManager = new PickaxeManager(this);
        this.guiManager = new GUIManager(this);
        this.minyonManager = new MinyonManager(this);
        this.rankManager = new RankManager(this);
        this.npcManager = new NPCManager(this);
        this.ucusManager = new UcusManager(this);

        playerDataManager.yukle();

        getCommand("maden").setExecutor(new MadenCommand(this));
        getCommand("madenziyaret").setExecutor(new MadenZiyaretCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("rankup").setExecutor(new RankupCommand(this));
        getCommand("canta").setExecutor(new CantaCommand(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldSafetyListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new GUIClickListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldChangeListener(this), this);

        // Minyonlari periyodik olarak calistir (her 10 saniyede bir)
        getServer().getScheduler().runTaskTimer(this, () -> minyonManager.tumMinyonlariCalistir(), 200L, 200L);

        // Verileri duzenli araliklarla diske yaz (veri kaybini onlemek icin, her 5 dakikada)
        getServer().getScheduler().runTaskTimer(this, playerDataManager::kaydet, 6000L, 6000L);

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

    public EkonomiManager getEkonomiManager() {
        return ekonomiManager;
    }

    public MinyonManager getMinyonManager() {
        return minyonManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public UcusManager getUcusManager() {
        return ucusManager;
    }
}
