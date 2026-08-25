package com.optim.towny.war;

import com.optim.towny.OptimTowny;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * config.yml -> savas.savas-saatleri listesindeki "HH:mm-HH:mm" aralıklarına göre
 * otomatik olarak savaş modunu açıp kapatır.
 */
public class WarManager {

    private final OptimTowny plugin;
    private BukkitTask checkTask;
    private boolean warActive = false;
    private boolean manualOverride = false; // /savas baslat|bitir ile elle kontrol

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    public WarManager(OptimTowny plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("savas.aktif", true)) return;
        // Her dakika kontrol et
        checkTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L * 60L);
    }

    public void stop() {
        if (checkTask != null) checkTask.cancel();
    }

    private void tick() {
        if (manualOverride) return; // elle ayarlanmışsa otomatik döngüyü ezme
        boolean shouldBeActive = isWithinScheduledWindow();
        if (shouldBeActive != warActive) {
            setWarActive(shouldBeActive, true);
        }
    }

    public boolean isWithinScheduledWindow() {
        LocalTime now = LocalTime.now();
        List<String> windows = plugin.getConfig().getStringList("savas.savas-saatleri");
        for (String w : windows) {
            String[] parts = w.split("-");
            if (parts.length != 2) continue;
            try {
                LocalTime start = LocalTime.parse(parts[0].trim(), FMT);
                LocalTime end = LocalTime.parse(parts[1].trim(), FMT);
                if (start.isBefore(end)) {
                    if (!now.isBefore(start) && now.isBefore(end)) return true;
                } else {
                    // gece yarısını geçen aralık, örn. 23:00-01:00
                    if (!now.isBefore(start) || now.isBefore(end)) return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public void setWarActive(boolean active, boolean automatic) {
        this.warActive = active;
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));
        String msg = active
                ? prefix + ChatColor.RED + "" + ChatColor.BOLD + "SAVAŞ MODU BAŞLADI! Kasabalar arası PVP ve ele geçirme aktif."
                : prefix + ChatColor.GREEN + "Savaş modu sona erdi. Barış zamanı.";
        Bukkit.broadcastMessage(msg);

        if (active && plugin.getConfig().getBoolean("boss.savas-modunda-otomatik-spawn", true)) {
            plugin.getWarBoss().maybeSpawnBossesForActiveWar();
        }
    }

    public void forceStart() {
        manualOverride = true;
        setWarActive(true, false);
    }

    public void forceEnd() {
        manualOverride = true;
        setWarActive(false, false);
    }

    public void resetToSchedule() {
        manualOverride = false;
    }

    public boolean isWarActive() { return warActive; }

    public boolean isPvpAllowed() {
        if (warActive) return true;
        return plugin.getConfig().getBoolean("savas.savas-disinda-pvp", false);
    }

    public List<String> getScheduleDescription() {
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("savas.savas-saatleri"));
        return list;
    }
}
