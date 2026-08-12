package com.prisonmaden.core.model;

import java.util.HashMap;
import java.util.Map;

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

    // Ekonomi / rank
    private long para = 0;
    private int rankSeviye = 0;

    // Madenin sabit merkezi (spawn noktasi + yenileme sinirlari icin kullanilir)
    private double merkezX, merkezY, merkezZ;

    // Minyon
    private int minyonSeviye = 0; // 0 = minyon yok
    private boolean minyonAcik = true;
    private double minyonX, minyonY, minyonZ;
    private String minyonUuid; // yerlestirilen ArmorStand'in UUID'si (String olarak)

    // Canta (toplanan cevherler, satilmayi bekliyor)
    private final Map<String, Integer> canta = new HashMap<>();

    // Minyonun kendi cantasi (oyuncunun cantasindan AYRI, elle satilmasi gerekir)
    private final Map<String, Integer> minyonCanta = new HashMap<>();

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

    public long getPara() {
        return para;
    }

    public void setPara(long para) {
        this.para = Math.max(0, para);
    }

    public void paraEkle(long miktar) {
        this.para += miktar;
    }

    public int getRankSeviye() {
        return rankSeviye;
    }

    public void setRankSeviye(int rankSeviye) {
        this.rankSeviye = rankSeviye;
    }

    public double getMerkezX() {
        return merkezX;
    }

    public double getMerkezY() {
        return merkezY;
    }

    public double getMerkezZ() {
        return merkezZ;
    }

    public void setMerkez(double x, double y, double z) {
        this.merkezX = x;
        this.merkezY = y;
        this.merkezZ = z;
    }

    public int getMinyonSeviye() {
        return minyonSeviye;
    }

    public void setMinyonSeviye(int minyonSeviye) {
        this.minyonSeviye = minyonSeviye;
    }

    public boolean minyonVarMi() {
        return minyonSeviye > 0;
    }

    public boolean isMinyonAcik() {
        return minyonAcik;
    }

    public void setMinyonAcik(boolean minyonAcik) {
        this.minyonAcik = minyonAcik;
    }

    public double getMinyonX() {
        return minyonX;
    }

    public double getMinyonY() {
        return minyonY;
    }

    public double getMinyonZ() {
        return minyonZ;
    }

    public void setMinyonKonum(double x, double y, double z) {
        this.minyonX = x;
        this.minyonY = y;
        this.minyonZ = z;
    }

    public String getMinyonUuid() {
        return minyonUuid;
    }

    public void setMinyonUuid(String minyonUuid) {
        this.minyonUuid = minyonUuid;
    }

    public Map<String, Integer> getCanta() {
        return canta;
    }

    public void cantayaEkle(String materyalAdi, int miktar) {
        canta.merge(materyalAdi, miktar, Integer::sum);
    }

    public void cantayiBosalt() {
        canta.clear();
    }

    public Map<String, Integer> getMinyonCanta() {
        return minyonCanta;
    }

    public void minyonCantayaEkle(String materyalAdi, int miktar) {
        minyonCanta.merge(materyalAdi, miktar, Integer::sum);
    }

    public void minyonCantayiBosalt() {
        minyonCanta.clear();
    }

    /**
     * Dunya silindiginde cagrilir: madenle ilgili tum degerleri sifirlar.
     * Ekonomi (para, rank) SIFIRLANMAZ - oyuncunun kazanimi kalir.
     */
    public void sifirla() {
        this.dunyaAdi = null;
        this.ziyaretAcik = true;
        this.servetSeviye = 0;
        this.verimlilikSeviye = 0;
        this.kirilmazlikSeviye = 0;
        this.minyonSeviye = 0;
        this.minyonAcik = true;
        this.minyonUuid = null;
        this.canta.clear();
        this.minyonCanta.clear();
    }
}
