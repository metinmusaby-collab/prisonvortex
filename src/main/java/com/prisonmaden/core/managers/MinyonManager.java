package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Yerlestirilebilir, otomatik cevher kiran, seviye atlatilabilen minyon sistemi.
 * Minyonun topladiklari oyuncunun kendi cantasindan AYRI bir "minyon cantasi"na gider;
 * bu canta minyona sag tiklayarak acilir ve elle satilir.
 */
public class MinyonManager {

    private static final int YUKSELTME_TABAN_MALIYET = 500;
    private static final int MAX_SEVIYE = 10;

    private final PrisonMaden plugin;
    private final NamespacedKey minyonTasiEtiketi;
    private final NamespacedKey minyonSahibiEtiketi;

    public MinyonManager(PrisonMaden plugin) {
        this.plugin = plugin;
        this.minyonTasiEtiketi = new NamespacedKey(plugin, "prisonmaden_minyontasi");
        this.minyonSahibiEtiketi = new NamespacedKey(plugin, "prisonmaden_minyon_sahibi");
    }

    // =========================================================
    // MINYON TASI (yerlestirme esyasi)
    // =========================================================

    public ItemStack minyonTasiOlustur() {
        ItemStack esya = new ItemStack(Material.IRON_BLOCK);
        ItemMeta meta = esya.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lMinyon Tasi"));
            meta.setLore(java.util.List.of(
                    ChatColor.translateAlternateColorCodes('&', "&7Yere koymak icin tikla."),
                    ChatColor.translateAlternateColorCodes('&', "&7Otomatik cevher toplar.")
            ));
            meta.getPersistentDataContainer().set(minyonTasiEtiketi, PersistentDataType.BYTE, (byte) 1);
            esya.setItemMeta(meta);
        }
        return esya;
    }

    public boolean minyonTasiMi(ItemStack esya) {
        if (esya == null || !esya.hasItemMeta()) return false;
        ItemMeta meta = esya.getItemMeta();
        if (meta == null) return false;
        Byte deger = meta.getPersistentDataContainer().get(minyonTasiEtiketi, PersistentDataType.BYTE);
        return deger != null && deger == 1;
    }

    // =========================================================
    // SAHIPLIK KONTROLU (NPC/minyon tiklama dinleyicisi icin)
    // =========================================================

    /**
     * Verilen entity bizim minyon ArmorStand'imizse sahibinin UUID'sini dondurur, degilse null.
     */
    public UUID minyonSahibiUuidAl(Entity entity) {
        if (!(entity instanceof ArmorStand)) return null;
        String deger = entity.getPersistentDataContainer().get(minyonSahibiEtiketi, PersistentDataType.STRING);
        if (deger == null) return null;
        try {
            return UUID.fromString(deger);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // =========================================================
    // YERLESTIRME
    // =========================================================

    /**
     * Minyonu verilen konuma yerlestirmeye calisir.
     * @return basariliysa true (item bu durumda tuketilmeli), aksi halde false
     */
    public boolean yerlestir(Player oyuncu, Location konum) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (veri.minyonVarMi()) {
            mesajGonder(oyuncu, "&cZaten bir minyonun var! Once onu kullan.");
            return false;
        }

        World dunya = konum.getWorld();
        ArmorStand stand = dunya.spawn(konum.clone().add(0.5, 0, 0.5), ArmorStand.class, es -> {
            es.setInvulnerable(true);
            es.setGravity(false);
            es.setCanPickupItems(false);
            es.setCustomNameVisible(true);
            es.setCustomName(ChatColor.translateAlternateColorCodes('&', "&6&l" + oyuncu.getName() + "'in Minyonu &7(Seviye 1)"));
            es.setSmall(true);
            es.getPersistentDataContainer().set(minyonSahibiEtiketi, PersistentDataType.STRING, oyuncu.getUniqueId().toString());
        });

        veri.setMinyonSeviye(1);
        veri.setMinyonAcik(true);
        veri.setMinyonKonum(konum.getX(), konum.getY(), konum.getZ());
        veri.setMinyonUuid(stand.getUniqueId().toString());
        plugin.getPlayerDataManager().kaydet();

        mesajGonder(oyuncu, "&aMinyon yerlestirildi! Otomatik cevher toplamaya basladi.");
        return true;
    }

    // =========================================================
    // AC/KAPA VE YUKSELTME
    // =========================================================

    public void acKapaDegistir(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        veri.setMinyonAcik(!veri.isMinyonAcik());
        plugin.getPlayerDataManager().kaydet();
        String durum = veri.isMinyonAcik() ? "&aACIK" : "&cKAPALI";
        mesajGonder(oyuncu, "&7Minyon artik " + durum + "&7!");
    }

    public void gelistir(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (!veri.minyonVarMi()) {
            mesajGonder(oyuncu, "&cOnce bir minyon yerlestirmelisin!");
            return;
        }
        if (veri.getMinyonSeviye() >= MAX_SEVIYE) {
            mesajGonder(oyuncu, "&cMinyon zaten maksimum seviyede! (" + MAX_SEVIYE + ")");
            return;
        }

        long maliyet = (long) YUKSELTME_TABAN_MALIYET * (veri.getMinyonSeviye() + 1);
        if (veri.getPara() < maliyet) {
            mesajGonder(oyuncu, "&cYetersiz para! Gerekli: " + maliyet + " para.");
            return;
        }

        veri.setPara(veri.getPara() - maliyet);
        veri.setMinyonSeviye(veri.getMinyonSeviye() + 1);
        plugin.getPlayerDataManager().kaydet();

        standIsimGuncelle(oyuncu, veri);
        mesajGonder(oyuncu, "&aMinyon seviye " + veri.getMinyonSeviye() + " oldu!");
    }

    private void standIsimGuncelle(Player oyuncu, OyuncuVerisi veri) {
        if (veri.getMinyonUuid() == null) return;
        try {
            UUID standUuid = UUID.fromString(veri.getMinyonUuid());
            if (plugin.getServer().getEntity(standUuid) instanceof ArmorStand stand) {
                stand.setCustomName(ChatColor.translateAlternateColorCodes('&',
                        "&6&l" + oyuncu.getName() + "'in Minyonu &7(Seviye " + veri.getMinyonSeviye() + ")"));
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    // =========================================================
    // OTOMATIK CALISMA (periyodik olarak PrisonMaden tarafindan cagrilir)
    // =========================================================

    public void tumMinyonlariCalistir() {
        for (UUID uuid : plugin.getPlayerDataManager().tumUuidler()) {
            OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(uuid);
            if (!veri.madeniVarMi() || !veri.minyonVarMi() || !veri.isMinyonAcik()) continue;

            World dunya = plugin.getServer().getWorld(veri.getDunyaAdi());
            if (dunya == null) continue; // dunya su an yuklu degil, atla

            minyonTikCalistir(veri, dunya);
        }
    }

    private void minyonTikCalistir(OyuncuVerisi veri, World dunya) {
        int yaricap = 3 + veri.getMinyonSeviye();
        int islenecekBlok = 1 + veri.getMinyonSeviye();

        int mx = (int) veri.getMinyonX();
        int my = (int) veri.getMinyonY();
        int mz = (int) veri.getMinyonZ();

        Map<Material, Long> fiyatlar = plugin.getEkonomiManager().tumFiyatlar();
        int bulunan = 0;

        for (int x = mx - yaricap; x <= mx + yaricap && bulunan < islenecekBlok; x++) {
            for (int y = my - 2; y <= my + 3 && bulunan < islenecekBlok; y++) {
                for (int z = mz - yaricap; z <= mz + yaricap && bulunan < islenecekBlok; z++) {
                    Block blok = dunya.getBlockAt(x, y, z);
                    if (fiyatlar.containsKey(blok.getType())) {
                        Material tur = blok.getType();
                        blok.setType(Material.STONE);
                        veri.minyonCantayaEkle(tur.name(), 1);
                        bulunan++;
                    }
                }
            }
        }

        if (bulunan > 0) {
            plugin.getPlayerDataManager().kaydet();
        }
    }

    private void mesajGonder(Player oyuncu, String mesaj) {
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + mesaj));
    }
}
