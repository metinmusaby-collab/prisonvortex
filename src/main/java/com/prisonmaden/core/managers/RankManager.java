package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import com.prisonmaden.core.model.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * /rankup komutunun mantigi. Rank atlamak icin oyuncunun yeterli parasi olmasi gerekir;
 * para maden'de cevher toplayip cantadan satarak kazanilir (bu, "gorev yaparak
 * ilerleme" seklinde yorumlanmistir).
 */
public class RankManager {

    private final PrisonMaden plugin;

    private final List<Rank> ranklar = List.of(
            new Rank("&7Cirak", 0),
            new Rank("&fIsci", 1000),
            new Rank("&aMadenci", 3000),
            new Rank("&2Usta Madenci", 7000),
            new Rank("&bElmas Avcisi", 15000),
            new Rank("&dZumrut Efendisi", 30000),
            new Rank("&6&lPrison Lordu", 60000)
    );

    public RankManager(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    public Rank rankAl(int seviye) {
        if (seviye < 0 || seviye >= ranklar.size()) return ranklar.get(ranklar.size() - 1);
        return ranklar.get(seviye);
    }

    public boolean sonRankMi(int seviye) {
        return seviye >= ranklar.size() - 1;
    }

    public void rankAtla(Player oyuncu) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());
        int mevcut = veri.getRankSeviye();

        if (sonRankMi(mevcut)) {
            mesajGonder(oyuncu, "&cZaten en yuksek ranktasin!");
            return;
        }

        Rank sonraki = rankAl(mevcut + 1);
        if (veri.getPara() < sonraki.getGerekliPara()) {
            long eksik = sonraki.getGerekliPara() - veri.getPara();
            mesajGonder(oyuncu, "&cYetersiz para! " + eksik + " para daha gerekli. (/canta ile satis yap)");
            return;
        }

        veri.setPara(veri.getPara() - sonraki.getGerekliPara());
        veri.setRankSeviye(mevcut + 1);
        plugin.getPlayerDataManager().kaydet();

        String rankAdi = ChatColor.translateAlternateColorCodes('&', sonraki.getIsim());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&6Maden&8] &e" + oyuncu.getName() + " &7artik " + rankAdi + " &7rankinda!"));
    }

    private void mesajGonder(Player oyuncu, String mesaj) {
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + mesaj));
    }
}
