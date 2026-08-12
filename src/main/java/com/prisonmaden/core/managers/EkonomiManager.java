package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cevherlerin satis fiyatlarini ve oyuncularin cantasini (biriktirilen esyalar) yonetir.
 * Toplanan cevherler fiziksel envantere degil, dogrudan bu sanal cantaya gider.
 */
public class EkonomiManager {

    private final PrisonMaden plugin;

    // Materyal -> tek adet satis fiyati (para)
    private final Map<Material, Long> fiyatlar = new LinkedHashMap<>();

    public EkonomiManager(PrisonMaden plugin) {
        this.plugin = plugin;
        fiyatlar.put(Material.COAL_ORE, 2L);
        fiyatlar.put(Material.IRON_ORE, 6L);
        fiyatlar.put(Material.GOLD_ORE, 12L);
        fiyatlar.put(Material.REDSTONE_ORE, 8L);
        fiyatlar.put(Material.LAPIS_ORE, 10L);
        fiyatlar.put(Material.DIAMOND_ORE, 60L);
    }

    public boolean satilabilirMi(Material materyal) {
        return fiyatlar.containsKey(materyal);
    }

    public long fiyatAl(Material materyal) {
        return fiyatlar.getOrDefault(materyal, 0L);
    }

    public Map<Material, Long> tumFiyatlar() {
        return fiyatlar;
    }

    /**
     * Kirilan bir cevheri oyuncunun cantasina ekler.
     */
    public void cantayaEkle(Player oyuncu, Material materyal, int miktar) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        veri.cantayaEkle(materyal.name(), miktar);
    }

    /**
     * Oyuncunun cantasindaki her seyi satar, parayi hesabina ekler ve cantayi bosaltir.
     * Toplam kazanci dondurur (0 ise satilacak bir sey yoktu).
     */
    public long hepsiniSat(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        long toplam = topla(veri.getCanta());

        if (toplam > 0) {
            veri.paraEkle(toplam);
            veri.cantayiBosalt();
            plugin.getPlayerDataManager().kaydet();
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&6Maden&8] &aCantandaki her sey satildi! &7Kazanc: &e" + toplam + " para"));
        } else {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&6Maden&8] &cCantan bos, satacak bir sey yok!"));
        }

        return toplam;
    }

    /**
     * Minyonun kendi cantasindaki her seyi satar (oyuncunun kendi cantasindan AYRI).
     */
    public long minyonCantasiniSat(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        long toplam = topla(veri.getMinyonCanta());

        if (toplam > 0) {
            veri.paraEkle(toplam);
            veri.minyonCantayiBosalt();
            plugin.getPlayerDataManager().kaydet();
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&6Maden&8] &aMinyon cantasi satildi! &7Kazanc: &e" + toplam + " para"));
        } else {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&6Maden&8] &cMinyon cantasi bos, satacak bir sey yok!"));
        }

        return toplam;
    }

    private long topla(Map<String, Integer> canta) {
        long toplam = 0;
        for (Map.Entry<String, Integer> giris : canta.entrySet()) {
            try {
                Material materyal = Material.valueOf(giris.getKey());
                toplam += fiyatAl(materyal) * giris.getValue();
            } catch (IllegalArgumentException ignored) {
                // Bilinmeyen materyal adi varsa atla
            }
        }
        return toplam;
    }
}
