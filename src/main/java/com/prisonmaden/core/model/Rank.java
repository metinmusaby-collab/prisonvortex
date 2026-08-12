package com.prisonmaden.core.model;

/**
 * Tek bir ranki temsil eder.
 */
public class Rank {

    private final String isim;
    private final long gerekliPara;

    public Rank(String isim, long gerekliPara) {
        this.isim = isim;
        this.gerekliPara = gerekliPara;
    }

    public String getIsim() {
        return isim;
    }

    public long getGerekliPara() {
        return gerekliPara;
    }
}
