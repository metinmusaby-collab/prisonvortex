package com.prisonmaden.core.model;

/**
 * Bir oyuncunun maden dunyasiyla ilgili tum kalici verilerini tutar.
 */
public class OyuncuVerisi {

    private String dunyaAdi;
    private boolean ziyaretAcik = true;
    private int servetSeviye = 0;
    private int verimlilikSeviye = 0;
    private int kirilmazlikSeviye = 0;
    private boolean kitAlindi = false;

    public boolean madeniVarMi() {
        return dunyaAdi != null;
    }

    public String getDunyaAdi() {
        return dunyaAdi;
    }

    public void setDunyaAdi(String dunyaAdi) {
        this.dunyaAdi = dunyaAdi;
    }

    public boolean isZiyaretAcik() {
        return ziyaretAcik;
    }

    public void setZiyaretAcik(boolean ziyaretAcik) {
        this.ziyaretAcik = ziyaretAcik;
    }

    public int getServetSeviye() {
        return servetSeviye;
    }

    public void setServetSeviye(int servetSeviye) {
        this.servetSeviye = servetSeviye;
    }

    public int getVerimlilikSeviye() {
        return verimlilikSeviye;
    }

    public void setVerimlilikSeviye(int verimlilikSeviye) {
        this.verimlilikSeviye = verimlilikSeviye;
    }

    public int getKirilmazlikSeviye() {
        return kirilmazlikSeviye;
    }

    public void setKirilmazlikSeviye(int kirilmazlikSeviye) {
        this.kirilmazlikSeviye = kirilmazlikSeviye;
    }

    public boolean isKitAlindi() {
        return kitAlindi;
    }

    public void setKitAlindi(boolean kitAlindi) {
        this.kitAlindi = kitAlindi;
    }

    /**
     * Dunya silindiginde cagrilir: madenle ilgili tum degerleri sifirlar.
     */
    public void sifirla() {
        this.dunyaAdi = null;
        this.ziyaretAcik = true;
        this.servetSeviye = 0;
        this.verimlilikSeviye = 0;
        this.kirilmazlikSeviye = 0;
    }
}
