package com.prisonmaden.core.model;

/**
 * Kazmaya uygulanabilecek yukseltme turleri.
 */
public enum YukseltmeTuru {
    SERVET("Servet", "&aServet Yukselt"),
    VERIMLILIK("Verimlilik", "&bVerimlilik Yukselt"),
    KIRILMAZLIK("Kirilmazlik", "&eKirilmazlik Yukselt");

    private final String gosterimAdi;
    private final String menuAdi;

    YukseltmeTuru(String gosterimAdi, String menuAdi) {
        this.gosterimAdi = gosterimAdi;
        this.menuAdi = menuAdi;
    }

    public String getGosterimAdi() {
        return gosterimAdi;
    }

    public String getMenuAdi() {
        return menuAdi;
    }
}
