package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

/**
 * /maden komutunun kalbi: dunya olusturma, isinlanma, insaat ve silme akislarini yonetir.
 * Multiverse-Core komutlarini konsoldan calistirir (sert bagimlilik gerektirmez).
 */
public class MineWorldManager {

    private final PrisonMaden plugin;

    public MineWorldManager(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    // =========================================================
    // 1) OLUSTURMA VE ISINLANMA
    // =========================================================

    public void girisVeyaOlustur(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (!veri.madeniVarMi()) {
            String dunyaAdi = "maden_" + oyuncu.getName();
            veri.setDunyaAdi(dunyaAdi);
            plugin.getPlayerDataManager().kaydet();

            mesajGonder(oyuncu, "&7Maden dunyan olusturuluyor, lutfen bekle...");
            konsolKomut("mv create " + dunyaAdi + " NORMAL -t FLAT");

            // Dunyanin tamamen yuklenmesi icin en az 4 saniye bekle
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                konsolKomut("mv tp " + oyuncu.getName() + " " + dunyaAdi);
                konsolKomut("mv modify set pvp false " + dunyaAdi);
                konsolKomut("mv modify set animals false " + dunyaAdi);
                konsolKomut("mv modify set monsters false " + dunyaAdi);

                // Isinlanmanin ve chunklarin tam oturmasi icin ek bekleme
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    insaMaden(oyuncu);
                    mesajGonder(oyuncu, "&aMaden dunyan hazir! Iyi kazilar.");
                }, 40L); // 2 saniye
            }, 80L); // 4 saniye
        } else {
            World hedefDunya = Bukkit.getWorld(veri.getDunyaAdi());
            if (hedefDunya == null || !oyuncu.getWorld().equals(hedefDunya)) {
                mesajGonder(oyuncu, "&7Madenine isinlaniyorsun...");
                konsolKomut("mv tp " + oyuncu.getName() + " " + veri.getDunyaAdi());
            }
        }
    }

    // =========================================================
    // INSAAT: OYUNCUNUN O ANKI (YENI DUNYADAKI) KONUMUNA GORE
    // =========================================================

    private void insaMaden(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        // GUVENLIK KONTROLU: oyuncu gercekten kendi maden dunyasinda mi?
        // Degilse insaati kesinlikle durdur (eski dunyada insa edilmesini engelle).
        World dunyasi = Bukkit.getWorld(veri.getDunyaAdi());
        if (dunyasi == null || !oyuncu.getWorld().equals(dunyasi)) {
            mesajGonder(oyuncu, "&cHata: Insaat icin yanlis dunyadasin, islem iptal edildi.");
            return;
        }

        Location merkez = oyuncu.getLocation();
        World dunya = merkez.getWorld();
        int mx = merkez.getBlockX();
        int my = merkez.getBlockY();
        int mz = merkez.getBlockZ();

        // Merkezi kalici olarak kaydet (spawn noktasi + ileride yenileme icin)
        veri.setMerkez(mx, my, mz);
        plugin.getPlayerDataManager().kaydet();

        for (int x = mx - 10; x <= mx + 10; x++) {
            for (int y = my - 1; y <= my + 7; y++) {
                for (int z = mz - 10; z <= mz + 10; z++) {
                    boolean duvar = (x == mx - 10 || x == mx + 10 || z == mz - 10 || z == mz + 10);
                    boolean taban = (y == my - 1);
                    boolean tavan = (y == my + 7);

                    if (taban) {
                        // Taban islevsel oldugu icin kirilamaz kalmali
                        dunya.getBlockAt(x, y, z).setType(Material.BEDROCK);
                    } else if (duvar || tavan) {
                        // Etrafta gorunur/kaba bedrock istenmiyor: gorunmez, kirilamaz sinir
                        dunya.getBlockAt(x, y, z).setType(Material.BARRIER);
                    } else {
                        dunya.getBlockAt(x, y, z).setType(rastgeleMadenBlogu());
                    }
                }
            }
        }

        // Oyuncunun sikismamasi icin merkeze kucuk bir bosluk ac
        for (int x = mx - 1; x <= mx + 1; x++) {
            for (int y = my; y <= my + 5; y++) {
                for (int z = mz - 1; z <= mz + 1; z++) {
                    dunya.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        oyuncu.teleport(new Location(dunya, mx + 0.5, my + 1, mz + 0.5));

        // Dunya sinirini maden alaniyla sinirla (vanilla Bukkit WorldBorder API)
        WorldBorder sinir = dunya.getWorldBorder();
        sinir.setCenter(merkez);
        sinir.setSize(40);

        // Panel NPC'sini merkeze yerlestir (tiklaninca maden paneli acilir)
        Location npcKonumu = new Location(dunya, mx + 2.5, my + 1, mz + 0.5, 180f, 0f);
        plugin.getNPCManager().panelNpcOlustur(npcKonumu);
    }

    private Material rastgeleMadenBlogu() {
        int sans = ThreadLocalRandom.current().nextInt(1, 101);
        if (sans <= 40) return Material.STONE;
        if (sans <= 55) return Material.COAL_ORE;
        if (sans <= 70) return Material.IRON_ORE;
        if (sans <= 82) return Material.GOLD_ORE;
        if (sans <= 90) return Material.REDSTONE_ORE;
        if (sans <= 96) return Material.LAPIS_ORE;
        return Material.DIAMOND_ORE;
    }

    // =========================================================
    // MADEN YENILEME (icerideki bloklari yeniden dagitir)
    // =========================================================

    public void yenile(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        if (!veri.madeniVarMi()) {
            mesajGonder(oyuncu, "&cOnce bir maden olusturmalisin!");
            return;
        }

        World dunya = Bukkit.getWorld(veri.getDunyaAdi());
        if (dunya == null) {
            mesajGonder(oyuncu, "&cMaden dunyan su an yuklu degil.");
            return;
        }

        int mx = (int) veri.getMerkezX();
        int my = (int) veri.getMerkezY();
        int mz = (int) veri.getMerkezZ();

        // Sadece ic kisim yenilenir; duvar (barrier) ve taban (bedrock) dokunulmaz
        for (int x = mx - 9; x <= mx + 9; x++) {
            for (int y = my; y <= my + 6; y++) {
                for (int z = mz - 9; z <= mz + 9; z++) {
                    dunya.getBlockAt(x, y, z).setType(rastgeleMadenBlogu());
                }
            }
        }

        // Merkezdeki bosluk tekrar acilsin
        for (int x = mx - 1; x <= mx + 1; x++) {
            for (int y = my; y <= my + 5; y++) {
                for (int z = mz - 1; z <= mz + 1; z++) {
                    dunya.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }

        mesajGonder(oyuncu, "&aMaden basariyla yenilendi!");
    }

    // =========================================================
    // 2) SILME VE MULTIVERSE CONFIRM FIXI
    // =========================================================

    public void sil(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (!veri.madeniVarMi()) {
            mesajGonder(oyuncu, "&cSilinecek bir maden dunyan yok!");
            return;
        }

        String dunyaAdi = veri.getDunyaAdi();
        mesajGonder(oyuncu, "&7Maden dunyan siliniyor, cikariliyorsun...");

        // Multiverse'in "World in use / Lock" hatasi vermemesi icin
        // ONCE oyuncuyu ana dunyanin spawn'ina cikar.
        World anaDunya = Bukkit.getWorld("world");
        if (anaDunya != null) {
            oyuncu.teleport(anaDunya.getSpawnLocation());
        }

        // Oyuncunun dunyadan tamamen cikmasi icin bekleme
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            konsolKomut("mv unload " + dunyaAdi);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                konsolKomut("mv delete " + dunyaAdi);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    konsolKomut("mv confirm");
                    konsolKomut("mvconfirm");

                    veri.sifirla();
                    plugin.getPlayerDataManager().kaydet();
                    mesajGonder(oyuncu, "&aMaden dunyan basariyla silindi!");
                }, 20L); // 1 saniye
            }, 40L); // 2 saniye
        }, 60L); // 3 saniye
    }

    // =========================================================
    // YARDIMCILAR
    // =========================================================

    private void konsolKomut(String komut) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), komut);
    }

    private void mesajGonder(Player oyuncu, String mesaj) {
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + mesaj));
    }
}
