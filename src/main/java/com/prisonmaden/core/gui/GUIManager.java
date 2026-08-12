package com.prisonmaden.core.gui;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import com.prisonmaden.core.model.Rank;
import com.prisonmaden.core.model.YukseltmeTuru;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIManager {

    public static final String PANEL_BASLIK = "&8Maden Paneli";
    public static final String YUKSELT_BASLIK = "&8Kazma Yukseltme";
    public static final String CANTA_BASLIK = "&8Cantam";
    public static final String MINYON_CANTA_BASLIK = "&8Minyon Cantasi";

    private final PrisonMaden plugin;

    public GUIManager(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    // =========================================================
    // ANA PANEL (NPC'ye tiklayinca / /maden ile acilir)
    // =========================================================

    public void panelMenusuAc(Player oyuncu) {
        MadenGUIHolder holder = new MadenGUIHolder(MadenGUIHolder.Tur.AYARLAR);
        Inventory envanter = Bukkit.createInventory(holder, 45, renkli(PANEL_BASLIK));
        holder.setEnvanter(envanter);

        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        Rank rank = plugin.getRankManager().rankAl(veri.getRankSeviye());

        envanter.setItem(10, esyaOlustur(Material.COMPASS, "&aMadenime Isinlan", List.of()));

        envanter.setItem(12, esyaOlustur(Material.CHEST, "&eCantam",
                List.of("&7Toplanan cevherleri gor ve sat", "&7Tikla ve ac")));

        String minyonDurum = veri.isMinyonAcik() ? "&aAcik" : "&cKapali";
        List<String> minyonLore = new ArrayList<>();
        minyonLore.add("&7Seviye: &e" + veri.getMinyonSeviye());
        minyonLore.add("&7Durum: " + minyonDurum);
        minyonLore.add("&7Sol tik: Ac/Kapat");
        minyonLore.add("&7Sag tik: Yukselt");
        if (!veri.minyonVarMi()) {
            minyonLore.add("");
            minyonLore.add("&e2000 para karsiliginda satin al");
        }
        envanter.setItem(14, esyaOlustur(Material.IRON_GOLEM_SPAWN_EGG, "&6Minyon Yonetimi", minyonLore));

        boolean ucusAcik = oyuncu.getAllowFlight();
        envanter.setItem(16, esyaOlustur(Material.FEATHER, "&bUcus " + (ucusAcik ? "&a(Acik)" : "&c(Kapali)"),
                List.of("&7Sadece kendi madeninde", "&7Tikla ve degistir")));

        envanter.setItem(28, esyaOlustur(Material.EMERALD, "&d/rankup",
                List.of("&7Mevcut rank: " + renkli(rank.getIsim()), "&7Tikla ve rank atlamayi dene")));

        envanter.setItem(30, esyaOlustur(Material.DIAMOND_PICKAXE, "&bBaslangic Kiti Al",
                List.of("&7Kazmani kaybettiysen", "&7buradan yeniden alabilirsin")));

        envanter.setItem(32, esyaOlustur(Material.GLOWSTONE, "&eMadeni Yenile",
                List.of("&7Tukenen cevherleri yeniden dagitir")));

        String ziyaretDurum = veri.isZiyaretAcik() ? "&aAcik" : "&cKapali";
        envanter.setItem(34, esyaOlustur(Material.NETHER_STAR, "&bZiyaret Ayari",
                List.of("&7Durum: " + ziyaretDurum, "&7Degistirmek icin tikla")));

        if (oyuncu.hasPermission("prisonmaden.admin")) {
            envanter.setItem(40, esyaOlustur(Material.BARRIER, "&c&lMadeni Sil (Yetkili)",
                    List.of("&7Bu islem geri alinamaz!")));
        }

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
    // CANTA MENULERI (oyuncu cantasi + minyon cantasi)
    // =========================================================

    public void cantaMenusuAc(Player oyuncu) {
        cantaMenusuOlustur(oyuncu, MadenGUIHolder.Tur.CANTA, CANTA_BASLIK,
                plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId()).getCanta());
    }

    public void minyonCantaMenusuAc(Player oyuncu) {
        cantaMenusuOlustur(oyuncu, MadenGUIHolder.Tur.MINYON_CANTA, MINYON_CANTA_BASLIK,
                plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId()).getMinyonCanta());
    }

    private void cantaMenusuOlustur(Player oyuncu, MadenGUIHolder.Tur tur, String baslik, Map<String, Integer> icerik) {
        MadenGUIHolder holder = new MadenGUIHolder(tur);
        Inventory envanter = Bukkit.createInventory(holder, 45, renkli(baslik));
        holder.setEnvanter(envanter);

        int slot = 0;
        long toplamDeger = 0;
        for (Map.Entry<String, Integer> giris : icerik.entrySet()) {
            if (slot >= 36) break; // en alt sira sat butonuna ayrildi
            try {
                Material materyal = Material.valueOf(giris.getKey());
                long fiyat = plugin.getEkonomiManager().fiyatAl(materyal);
                long buGrupDeger = fiyat * giris.getValue();
                toplamDeger += buGrupDeger;

                envanter.setItem(slot, esyaOlustur(materyal, "&f" + materyal.name(),
                        List.of("&7Adet: &e" + giris.getValue(), "&7Toplam Deger: &e" + buGrupDeger + " para")));
                slot++;
            } catch (IllegalArgumentException ignored) {
            }
        }

        envanter.setItem(40, esyaOlustur(Material.EMERALD_BLOCK, "&a&lHEPSINI SAT",
                List.of("&7Toplam: &e" + toplamDeger + " para", "&7Tikla ve sat!")));

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
