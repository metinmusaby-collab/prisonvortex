package com.prisonmaden.core.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Acilan envanterin PrisonMaden'e ait hangi menu oldugunu belirlemek icin
 * kullanilan isaretleyici. Boylece InventoryClickEvent icinde baslik metniyle
 * ugrasmadan guvenli sekilde "bu bizim menumuz mu, hangisi" diye kontrol edilir.
 */
public class MadenGUIHolder implements InventoryHolder {

    public enum Tur {
        AYARLAR,
        YUKSELT,
        CANTA,
        MINYON_CANTA
    }

    private final Tur tur;
    private Inventory envanter;

    public MadenGUIHolder(Tur tur) {
        this.tur = tur;
    }

    public Tur getTur() {
        return tur;
    }

    public void setEnvanter(Inventory envanter) {
        this.envanter = envanter;
    }

    @Override
    public Inventory getInventory() {
        return envanter;
    }
}
