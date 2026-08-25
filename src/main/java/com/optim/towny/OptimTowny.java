package com.optim.towny;

import com.optim.towny.commands.*;
import com.optim.towny.data.DataManager;
import com.optim.towny.listeners.ProtectionListener;
import com.optim.towny.nation.NationManager;
import com.optim.towny.quest.QuestManager;
import com.optim.towny.tax.TaxManager;
import com.optim.towny.town.TownManager;
import com.optim.towny.town.TownRank;
import com.optim.towny.util.Economy;
import com.optim.towny.util.MapRenderer;
import com.optim.towny.war.WarBoss;
import com.optim.towny.war.WarManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OptimTowny extends JavaPlugin {

    private static OptimTowny instance;

    private TownManager townManager;
    private NationManager nationManager;
    private TaxManager taxManager;
    private WarManager warManager;
    private WarBoss warBoss;
    private QuestManager questManager;
    private TownRank townRank;
    private Economy economy;
    private MapRenderer mapRenderer;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Yönetici sınıfları oluşturuluyor
        this.economy = new Economy(this);
        this.townManager = new TownManager(this);
        this.nationManager = new NationManager(this);
        this.townRank = new TownRank(this);
        this.questManager = new QuestManager(this);
        this.warBoss = new WarBoss(this);
        this.warManager = new WarManager(this);
        this.taxManager = new TaxManager(this);
        this.mapRenderer = new MapRenderer(this);
        this.dataManager = new DataManager(this);

        // Kayıtlı verileri yükle
        dataManager.load();

        // Komutları kaydet
        getCommand("kasaba").setExecutor(new KasabaCommand(this));
        getCommand("ulus").setExecutor(new UlusCommand(this));
        getCommand("harita").setExecutor(new HaritaCommand(this));
        getCommand("savas").setExecutor(new SavasCommand(this));
        getCommand("gorev").setExecutor(new GorevCommand(this));
        getCommand("vergi").setExecutor(new VergiCommand(this));

        // Dinleyicileri kaydet
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        // Zamanlanmış görevleri başlat
        taxManager.start();
        warManager.start();

        getLogger().info("OptimTowny başarıyla etkinleştirildi. Maksimum kasaba: " + townManager.getMaxTowns());
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.save();
        if (taxManager != null) taxManager.stop();
        if (warManager != null) warManager.stop();
        getLogger().info("OptimTowny devre dışı bırakıldı, veriler kaydedildi.");
    }

    public static OptimTowny getInstance() { return instance; }

    public TownManager getTownManager() { return townManager; }
    public NationManager getNationManager() { return nationManager; }
    public TaxManager getTaxManager() { return taxManager; }
    public WarManager getWarManager() { return warManager; }
    public WarBoss getWarBoss() { return warBoss; }
    public QuestManager getQuestManager() { return questManager; }
    public TownRank getTownRank() { return townRank; }
    public Economy getEconomy() { return economy; }
    public MapRenderer getMapRenderer() { return mapRenderer; }
    public DataManager getDataManager() { return dataManager; }
}
