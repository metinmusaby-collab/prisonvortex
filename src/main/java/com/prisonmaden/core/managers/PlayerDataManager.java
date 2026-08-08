package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final PrisonMaden plugin;
    private final Map<UUID, OyuncuVerisi> onbellek = new HashMap<>();
    private File dosya;

    public PlayerDataManager(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    public void yukle() {
        dosya = new File(plugin.getDataFolder(), "oyuncular.yml");
        if (!dosya.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dosya.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("oyuncular.yml olusturulamadi: " + e.getMessage());
            }
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(dosya);
        onbellek.clear();

        if (cfg.isConfigurationSection("oyuncular")) {
            for (String uuidStr : cfg.getConfigurationSection("oyuncular").getKeys(false)) {
                String yol = "oyuncular." + uuidStr;
                OyuncuVerisi veri = new OyuncuVerisi();
                veri.setDunyaAdi(cfg.getString(yol + ".dunya", null));
                veri.setZiyaretAcik(cfg.getBoolean(yol + ".ziyaret", true));
                veri.setServetSeviye(cfg.getInt(yol + ".servet", 0));
                veri.setVerimlilikSeviye(cfg.getInt(yol + ".verimlilik", 0));
                veri.setKirilmazlikSeviye(cfg.getInt(yol + ".kirilmazlik", 0));
                veri.setKitAlindi(cfg.getBoolean(yol + ".kit_alindi", false));
                onbellek.put(UUID.fromString(uuidStr), veri);
            }
        }
    }

    public void kaydet() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, OyuncuVerisi> giris : onbellek.entrySet()) {
            String yol = "oyuncular." + giris.getKey();
            OyuncuVerisi veri = giris.getValue();
            cfg.set(yol + ".dunya", veri.getDunyaAdi());
            cfg.set(yol + ".ziyaret", veri.isZiyaretAcik());
            cfg.set(yol + ".servet", veri.getServetSeviye());
            cfg.set(yol + ".verimlilik", veri.getVerimlilikSeviye());
            cfg.set(yol + ".kirilmazlik", veri.getKirilmazlikSeviye());
            cfg.set(yol + ".kit_alindi", veri.isKitAlindi());
        }
        try {
            cfg.save(dosya);
        } catch (IOException e) {
            plugin.getLogger().warning("oyuncular.yml kaydedilemedi: " + e.getMessage());
        }
    }

    public OyuncuVerisi veriAl(UUID uuid) {
        return onbellek.computeIfAbsent(uuid, k -> new OyuncuVerisi());
    }

    /**
     * Dunya adindan hangi oyuncuya ait oldugunu bulur (madenziyaret icin degil,
     * ic kontroller icin kullanilabilir).
     */
    public UUID uuidBulDunyaAdiyla(String dunyaAdi) {
        for (Map.Entry<UUID, OyuncuVerisi> giris : onbellek.entrySet()) {
            if (dunyaAdi.equals(giris.getValue().getDunyaAdi())) {
                return giris.getKey();
            }
        }
        return null;
    }
}
