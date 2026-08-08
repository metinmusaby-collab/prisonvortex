package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import com.prisonmaden.core.model.YukseltmeTuru;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PickaxeManager {

    private static final int MAX_SEVIYE = 10;
    private static final int GEREKEN_ZUMRUT = 3;
    private static final int SISE_DUSME_SANSI = 5; // yuzde

    private final PrisonMaden plugin;
    private final NamespacedKey siseEtiketi;

    public PickaxeManager(PrisonMaden plugin) {
        this.plugin = plugin;
        this.siseEtiketi = new NamespacedKey(plugin, "prisonmaden_sise");
    }

    // =========================================================
    // YUKSELTME SISESI
    // =========================================================

    public boolean siseDusecekMi() {
        return ThreadLocalRandom.current().nextInt(1, 101) <= SISE_DUSME_SANSI;
    }

    public ItemStack siseOlustur() {
        ItemStack sise = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta meta = sise.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d&lYukseltme Sisesi"));
            meta.setLore(List.of(
                    ChatColor.translateAlternateColorCodes('&', "&7Kazmani yukseltmek icin"),
                    ChatColor.translateAlternateColorCodes('&', "&7prison kazmana sag tikla.")
            ));
            meta.getPersistentDataContainer().set(siseEtiketi, PersistentDataType.BYTE, (byte) 1);
            sise.setItemMeta(meta);
        }
        return sise;
    }

    public boolean ozelSiseMi(ItemStack esya) {
        if (esya == null || !esya.hasItemMeta()) return false;
        ItemMeta meta = esya.getItemMeta();
        if (meta == null) return false;
        Byte deger = meta.getPersistentDataContainer().get(siseEtiketi, PersistentDataType.BYTE);
        return deger != null && deger == 1;
    }

    // =========================================================
    // YUKSELTME ISLEMI
    // =========================================================

    /**
     * Oyuncunun elindeki ozel kazmayi verilen turde bir seviye yukseltir.
     * Sart kontrolleri (kazma tutuyor mu, sisesi var mi, zumrutu var mi) burada yapilir.
     */
    public void yukselt(Player oyuncu, ItemStack kazma, YukseltmeTuru tur) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        int mevcutSeviye = seviyeAl(veri, tur);

        if (mevcutSeviye >= MAX_SEVIYE) {
            mesajGonder(oyuncu, "&cBu ozellik zaten maksimum seviyede! (" + MAX_SEVIYE + ")");
            return;
        }

        if (!oyuncu.getInventory().containsAtLeast(bosSise(), 1)) {
            mesajGonder(oyuncu, "&cYukseltme Sisen yok! Demir cevheri kirarak bulabilirsin.");
            return;
        }

        if (!oyuncu.getInventory().contains(Material.EMERALD, GEREKEN_ZUMRUT)) {
            mesajGonder(oyuncu, "&cEn az " + GEREKEN_ZUMRUT + " zumrude ihtiyacin var!");
            return;
        }

        // Etiketli sise ve zumrutleri envanterden dus
        siseTuket(oyuncu);
        oyuncu.getInventory().removeItem(new ItemStack(Material.EMERALD, GEREKEN_ZUMRUT));

        int yeniSeviye = mevcutSeviye + 1;
        seviyeAyarla(veri, tur, yeniSeviye);
        plugin.getPlayerDataManager().kaydet();

        Enchantment buyu = buyuAl(tur);
        ItemMeta meta = kazma.getItemMeta();
        if (meta != null) {
            meta.addEnchant(buyu, yeniSeviye, true);
            kazma.setItemMeta(meta);
        }

        mesajGonder(oyuncu, "&a" + tur.getGosterimAdi() + " seviye " + yeniSeviye + " oldu!");
    }

    private void siseTuket(Player oyuncu) {
        for (ItemStack esya : oyuncu.getInventory().getContents()) {
            if (ozelSiseMi(esya)) {
                if (esya.getAmount() > 1) {
                    esya.setAmount(esya.getAmount() - 1);
                } else {
                    oyuncu.getInventory().remove(esya);
                }
                return;
            }
        }
    }

    private ItemStack bosSise() {
        return new ItemStack(Material.GLASS_BOTTLE);
    }

    public int seviyeAl(OyuncuVerisi veri, YukseltmeTuru tur) {
        return switch (tur) {
            case SERVET -> veri.getServetSeviye();
            case VERIMLILIK -> veri.getVerimlilikSeviye();
            case KIRILMAZLIK -> veri.getKirilmazlikSeviye();
        };
    }

    private void seviyeAyarla(OyuncuVerisi veri, YukseltmeTuru tur, int seviye) {
        switch (tur) {
            case SERVET -> veri.setServetSeviye(seviye);
            case VERIMLILIK -> veri.setVerimlilikSeviye(seviye);
            case KIRILMAZLIK -> veri.setKirilmazlikSeviye(seviye);
        }
    }

    private Enchantment buyuAl(YukseltmeTuru tur) {
        return switch (tur) {
            case SERVET -> Enchantment.FORTUNE;
            case VERIMLILIK -> Enchantment.EFFICIENCY;
            case KIRILMAZLIK -> Enchantment.UNBREAKING;
        };
    }

    private void mesajGonder(Player oyuncu, String mesaj) {
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + mesaj));
    }
}
