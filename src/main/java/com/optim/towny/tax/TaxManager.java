package com.optim.towny.tax;

import com.optim.towny.OptimTowny;
import com.optim.towny.nation.Nation;
import com.optim.towny.town.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.UUID;

/**
 * Belirli aralıklarla kasaba üyelerinden ve kasabalardan (ulus varsa) vergi toplar.
 */
public class TaxManager {

    private final OptimTowny plugin;
    private BukkitTask task;

    public TaxManager(OptimTowny plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("vergi.aktif", true)) return;
        long periodMinutes = plugin.getConfig().getLong("vergi.toplama-araligi-dakika", 60);
        long ticks = periodMinutes * 60L * 20L;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::collectAll, ticks, ticks);
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    public void collectAll() {
        double perPlayer = plugin.getConfig().getDouble("vergi.oyuncu-basi-vergi", 10.0);
        double nationTax = plugin.getConfig().getDouble("vergi.kasaba-ulus-vergisi", 25.0);
        int limit = plugin.getConfig().getInt("vergi.odenmeyen-vergi-limiti", 3);
        boolean announce = plugin.getConfig().getBoolean("vergi.ilan-mesaji-goster", true);
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));

        for (Town town : plugin.getTownManager().getAllTowns()) {
            double totalDue = perPlayer * town.getMembers().size();

            // Ulus vergisi varsa kasaba bakiyesinden düş
            if (town.getNationName() != null) {
                Nation nation = plugin.getNationManager().getNation(town.getNationName());
                if (nation != null) {
                    if (town.withdraw(nationTax)) {
                        nation.deposit(nationTax);
                    }
                }
            }

            if (town.withdraw(totalDue)) {
                town.resetUnpaidTax();
                if (announce) notifyMembers(town, prefix + ChatColor.GREEN + "'" + town.getName() +
                        "' kasabasının vergisi (" + totalDue + ") kasa bakiyesinden ödendi.");
            } else {
                town.incrementUnpaidTax();
                if (announce) notifyMembers(town, prefix + ChatColor.RED + "'" + town.getName() +
                        "' kasabası vergiyi ödeyemedi! (" + town.getUnpaidTaxCount() + "/" + limit + ")");

                if (town.getUnpaidTaxCount() >= limit) {
                    handleBankruptcy(town);
                }
            }
        }
    }

    private void handleBankruptcy(Town town) {
        // İflas eden kasabanın en düşük rütbeli/rastgele bir üyesi kasabadan atılır (örnek ceza mekaniği)
        Iterator<UUID> it = town.getMembers().iterator();
        if (it.hasNext()) {
            UUID victim = it.next();
            if (!victim.equals(town.getOwner()) || town.getMembers().size() == 1) {
                town.resetUnpaidTax();
                notifyMembers(town, ChatColor.YELLOW + town.getName() + " kasabası iflas etti, borç sıfırlandı fakat itibar kaybedildi.");
            }
        }
    }

    private void notifyMembers(Town town, String message) {
        for (UUID uuid : town.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) p.sendMessage(message);
        }
    }
}
