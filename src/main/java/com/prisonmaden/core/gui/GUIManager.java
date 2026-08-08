package com.prisonmaden.core.gui;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import com.prisonmaden.core.model.YukseltmeTuru;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIManager {

    public static final String AYARLAR_BASLIK = "&8Maden Ayarlari";
    public static final String YUKSELT_BASLIK = "&8Kazma Yukseltme";

    private final PrisonMaden plugin;

    public GUIManager(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    // =========================================================
    // AYARLAR MENUSU (/maden, /maden ayarlar)
    // =========================================================

    public void ayarlarMenusuAc(Player oyuncu) {
        MadenGUIHolder holder = new MadenGUIHolder(MadenGUIHolder.Tur.AYARLAR);
        Inventory envanter = Bukkit.createInventory(holder, 27, renkli(AYARLAR_BASLIK));
        holder.setEnvanter(envanter);

        envanter.setItem(11, esyaOlustur(Material.COMPASS, "&aMadenime Isinlan", List.of()));
        envanter.setItem(13, esyaOlustur(Material.BARRIER, "&cMadenimi Sil", List.of("&7Bu islem geri alinamaz!")));

        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        String durum = veri.isZiyaretAcik() ? "&aAcik" : "&cKapali";
        envanter.setItem(15, esyaOlustur(Material.NETHER_STAR, "&bZiyaret Ayari",
                List.of("&7Durum: " + durum, "&7Degistirmek icin tikla")));

        oyuncu.openInventory(envanter);
    }

    // =========================================================
    // KAZMA YUKSELTME MENUSU (sag tik ile acilir)
    // =========================================================

    public void yukseltMenusuAc(Player oyuncu) {
        MadenGUIHolder holder = new MadenGUIHolder(MadenGUIHolder.Tur.YUKSELT);
        Inventory envanter = Bukkit.createInventory(holder, 27, renkli(YUKSELT_BASLIK));
        holder.setEnvanter(envanter);

        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        int servet = plugin.getPickaxeManager().seviyeAl(veri, YukseltmeTuru.SERVET);
        int verimlilik = plugin.getPickaxeManager().seviyeAl(veri, YukseltmeTuru.VERIMLILIK);
        int kirilmazlik = plugin.getPickaxeManager().seviyeAl(veri, YukseltmeTuru.KIRILMAZLIK);

        envanter.setItem(11, esyaOlustur(Material.DIAMOND, YukseltmeTuru.SERVET.getMenuAdi(),
                List.of("&7Mevcut Seviye: &e" + servet, "&71 Yukseltme Sisesi + 3 Zumrut")));
        envanter.setItem(13, esyaOlustur(Material.REDSTONE, YukseltmeTuru.VERIMLILIK.getMenuAdi(),
                List.of("&7Mevcut Seviye: &e" + verimlilik, "&71 Yukseltme Sisesi + 3 Zumrut")));
        envanter.setItem(15, esyaOlustur(Material.ANVIL, YukseltmeTuru.KIRILMAZLIK.getMenuAdi(),
                List.of("&7Mevcut Seviye: &e" + kirilmazlik, "&71 Yukseltme Sisesi + 3 Zumrut")));

        oyuncu.openInventory(envanter);
    }

    // =========================================================
    // YARDIMCI
    // =========================================================

    private ItemStack esyaOlustur(Material materyal, String isim, List<String> lore) {
        ItemStack esya = new ItemStack(materyal);
        ItemMeta meta = esya.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(renkli(isim));
            meta.setLore(lore.stream().map(this::renkli).toList());
            esya.setItemMeta(meta);
        }
        return esya;
    }

    private String renkli(String metin) {
        return ChatColor.translateAlternateColorCodes('&', metin);
    }
}
