package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

                veri.setPara(cfg.getLong(yol + ".para", 0));
                veri.setRankSeviye(cfg.getInt(yol + ".rank", 0));
                veri.setMerkez(
                        cfg.getDouble(yol + ".merkez.x", 0),
                        cfg.getDouble(yol + ".merkez.y", 0),
                        cfg.getDouble(yol + ".merkez.z", 0)
                );

                veri.setMinyonSeviye(cfg.getInt(yol + ".minyon.seviye", 0));
                veri.setMinyonAcik(cfg.getBoolean(yol + ".minyon.acik", true));
                veri.setMinyonKonum(
                        cfg.getDouble(yol + ".minyon.x", 0),
                        cfg.getDouble(yol + ".minyon.y", 0),
                        cfg.getDouble(yol + ".minyon.z", 0)
                );
                veri.setMinyonUuid(cfg.getString(yol + ".minyon.uuid", null));

                ConfigurationSection cantaBolumu = cfg.getConfigurationSection(yol + ".canta");
                if (cantaBolumu != null) {
                    for (String materyal : cantaBolumu.getKeys(false)) {
                        veri.cantayaEkle(materyal, cantaBolumu.getInt(materyal));
                    }
                }

                ConfigurationSection minyonCantaBolumu = cfg.getConfigurationSection(yol + ".minyon_canta");
                if (minyonCantaBolumu != null) {
                    for (String materyal : minyonCantaBolumu.getKeys(false)) {
                        veri.minyonCantayaEkle(materyal, minyonCantaBolumu.getInt(materyal));
                    }
                }

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

            cfg.set(yol + ".para", veri.getPara());
            cfg.set(yol + ".rank", veri.getRankSeviye());
            cfg.set(yol + ".merkez.x", veri.getMerkezX());
            cfg.set(yol + ".merkez.y", veri.getMerkezY());
            cfg.set(yol + ".merkez.z", veri.getMerkezZ());

            cfg.set(yol + ".minyon.seviye", veri.getMinyonSeviye());
            cfg.set(yol + ".minyon.acik", veri.isMinyonAcik());
            cfg.set(yol + ".minyon.x", veri.getMinyonX());
            cfg.set(yol + ".minyon.y", veri.getMinyonY());
            cfg.set(yol + ".minyon.z", veri.getMinyonZ());
            cfg.set(yol + ".minyon.uuid", veri.getMinyonUuid());

            for (Map.Entry<String, Integer> cantaGirisi : veri.getCanta().entrySet()) {
                cfg.set(yol + ".canta." + cantaGirisi.getKey(), cantaGirisi.getValue());
            }
            for (Map.Entry<String, Integer> minyonCantaGirisi : veri.getMinyonCanta().entrySet()) {
                cfg.set(yol + ".minyon_canta." + minyonCantaGirisi.getKey(), minyonCantaGirisi.getValue());
            }
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

    public Set<UUID> tumUuidler() {
        return onbellek.keySet();
    }

    /**
     * Verilen minyon UUID'sinin sahibini bulur (minyona sag tiklandiginda kullanilir).
     */
    public UUID minyonSahibiBul(String minyonUuid) {
        for (Map.Entry<UUID, OyuncuVerisi> giris : onbellek.entrySet()) {
            if (minyonUuid.equals(giris.getValue().getMinyonUuid())) {
                return giris.getKey();
            }
        }
        return null;
    }
}
