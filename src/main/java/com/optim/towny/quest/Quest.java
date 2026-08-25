package com.optim.towny.quest;

public class Quest {

    public enum Type { BLOK_KIR, MOB_OLDUR, PARA_BIRIKTIR }

    private final String id;
    private final String aciklama;
    private final Type tip;
    private final int hedefMiktar;
    private final double odulPara;

    public Quest(String id, String aciklama, Type tip, int hedefMiktar, double odulPara) {
        this.id = id;
        this.aciklama = aciklama;
        this.tip = tip;
        this.hedefMiktar = hedefMiktar;
        this.odulPara = odulPara;
    }

    public String getId() { return id; }
    public String getAciklama() { return aciklama; }
    public Type getTip() { return tip; }
    public int getHedefMiktar() { return hedefMiktar; }
    public double getOdulPara() { return odulPara; }
}
