package com.optim.towny.war;

import com.optim.towny.OptimTowny;
import com.optim.towny.town.Town;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Savaş modu sırasında kasabalarda belirli bir olasılıkla güçlü bir "muhafız" boss'u spawn eder.
 * Öldürüldüğünde kasaba kasasına ödül yatırır.
 */
public class WarBoss {

    private final OptimTowny plugin;
    private final Random random = new Random();
    private final Map<UUID, BossBar> activeBars = new HashMap<>();
    private final Map<UUID, String> bossTownOwner = new HashMap<>(); // entityUUID -> townName

    public WarBoss(OptimTowny plugin) {
        this.plugin = plugin;
    }

    public void maybeSpawnBossesForActiveWar() {
        if (!plugin.getConfig().getBoolean("boss.aktif", true)) return;
        int chance = plugin.getConfig().getInt("boss.spawn-sansi-yuzde", 35);

        for (Town town : plugin.getTownManager().getAllTowns()) {
            if (town.getSpawn() == null) continue;
            if (random.nextInt(100) >= chance) continue;
            spawnBossAt(town);
        }
    }

    public void spawnBossAt(Town town) {
        Location loc = town.getSpawn();
        if (loc == null || loc.getWorld() == null) return;

        Zombie boss = (Zombie) loc.getWorld().spawnEntity(loc, org.bukkit.entity.EntityType.ZOMBIE);
        double hp = plugin.getConfig().getDouble("boss.boss-can", 400.0);
        double dmg = plugin.getConfig().getDouble("boss.boss-hasar", 12.0);
        String name = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("boss.boss-adi", "&4&lKasaba Muhafızı"));

        boss.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hp);
        boss.setHealth(hp);
        boss.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(dmg);
        boss.setCustomName(name + " §7(" + town.getName() + ")");
        boss.setCustomNameVisible(true);
        boss.setBaby(false);
        boss.setRemoveWhenFarAway(false);

        BossBar bar = plugin.getServer().createBossBar(name, BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
        activeBars.put(boss.getUniqueId(), bar);
        bossTownOwner.put(boss.getUniqueId(), town.getName());
        // Not: BossBar'a oyuncular, hasar verdiklerinde onBossDamaged() içinde otomatik eklenir.
    }

    public void onBossDamaged(LivingEntity entity, Player damager) {
        BossBar bar = activeBars.get(entity.getUniqueId());
        if (bar == null) return;
        if (!bar.getPlayers().contains(damager)) bar.addPlayer(damager);
        double max = entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        bar.setProgress(Math.max(0, Math.min(1, entity.getHealth() / max)));
    }

    public void onBossDeath(LivingEntity entity, Player killer) {
        BossBar bar = activeBars.remove(entity.getUniqueId());
        if (bar != null) bar.removeAll();

        String townName = bossTownOwner.remove(entity.getUniqueId());
        double reward = plugin.getConfig().getDouble("boss.odul-para", 750.0);
        if (townName != null) {
            Town town = plugin.getTownManager().getTown(townName);
            if (town != null) town.deposit(reward);
        }
        if (killer != null) {
            killer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("dil.prefix", "")) +
                    ChatColor.GOLD + "Kasaba muhafızını yendin! Kasabaya " + reward + " ödül eklendi.");
        }
    }

    public boolean isTrackedBoss(UUID entityId) {
        return bossTownOwner.containsKey(entityId);
    }
}
