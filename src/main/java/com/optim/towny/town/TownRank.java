package com.optim.towny.town;

import com.optim.towny.OptimTowny;

import java.util.List;
import java.util.UUID;

/**
 * Rütbe sıralaması ve atlama mantığını yönetir.
 * config.yml -> rutbeler.siralama listesinden okunur (en düşükten en yükseğe doğru sondan başa).
 */
public class TownRank {

    private final OptimTowny plugin;

    public TownRank(OptimTowny plugin) {
        this.plugin = plugin;
    }

    private List<String> getOrderedRanksHighToLow() {
        return plugin.getConfig().getStringList("rutbeler.siralama");
    }

    public String getDefaultRank() {
        return plugin.getConfig().getString("rutbeler.varsayilan-rutbe", "Aday");
    }

    /**
     * Bir sonraki (daha yüksek) rütbeyi döndürür. Zirvedeyse null döner.
     */
    public String getNextRank(String currentRank) {
        List<String> order = getOrderedRanksHighToLow(); // index 0 = en yüksek
        int idx = order.indexOf(currentRank);
        if (idx <= 0) return null; // bulunamadı ya da zaten en tepede
        return order.get(idx - 1);
    }

    public boolean canPromote(Town town, UUID target, int completedQuests) {
        String current = town.getRank(target);
        String next = getNextRank(current);
        if (next == null) return false;
        int required = plugin.getConfig().getInt("rutbeler.atlama-icin-gereken-gorev", 3);
        return completedQuests >= required;
    }

    public boolean promote(Town town, UUID target, int completedQuests) {
        if (!canPromote(town, target, completedQuests)) return false;
        String next = getNextRank(town.getRank(target));
        town.setRank(target, next);
        return true;
    }
}
