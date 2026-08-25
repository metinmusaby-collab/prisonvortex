package com.optim.towny.listeners;

import com.optim.towny.OptimTowny;
import com.optim.towny.quest.Quest;
import com.optim.towny.town.Town;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class ProtectionListener implements Listener {

    private final OptimTowny plugin;

    public ProtectionListener(OptimTowny plugin) {
        this.plugin = plugin;
    }

    private String prefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Town town = plugin.getTownManager().getTownAt(event.getBlock().getChunk());
        if (town != null && !town.isMember(event.getPlayer().getUniqueId()) && !plugin.getWarManager().isWarActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(prefix() + ChatColor.RED + "Burası '" + town.getName() + "' kasabasına ait, kazamazsın!");
            return;
        }
        plugin.getQuestManager().addProgress(event.getPlayer(), Quest.Type.BLOK_KIR, 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Town town = plugin.getTownManager().getTownAt(event.getBlock().getChunk());
        if (town != null && !town.isMember(event.getPlayer().getUniqueId()) && !plugin.getWarManager().isWarActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(prefix() + ChatColor.RED + "Burası '" + town.getName() + "' kasabasına ait, buraya inşa edemezsin!");
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // Savaş dışı ve kasaba içi PVP engelleme
        if (event.getEntity() instanceof Player victim && event.getDamager() instanceof Player attacker) {
            if (!plugin.getWarManager().isPvpAllowed()) {
                Town victimTown = plugin.getTownManager().getTownOf(victim.getUniqueId());
                Town attackerTown = plugin.getTownManager().getTownOf(attacker.getUniqueId());
                if (victimTown != null && attackerTown != null && victimTown != attackerTown) {
                    event.setCancelled(true);
                    attacker.sendMessage(prefix() + ChatColor.RED + "Savaş modu aktif değilken kasabalar arası PVP yasak!");
                }
            }
        }

        // Boss hasar takibi
        if (event.getEntity() instanceof Zombie && event.getDamager() instanceof Player p) {
            if (plugin.getWarBoss().isTrackedBoss(event.getEntity().getUniqueId())) {
                plugin.getWarBoss().onBossDamaged((LivingEntity) event.getEntity(), p);
            }
        }

        // Mob öldürme görevi ilerlemesi burada değil, EntityDeathEvent'te işlenir.
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (plugin.getWarBoss().isTrackedBoss(entity.getUniqueId())) {
            plugin.getWarBoss().onBossDeath(entity, killer);
            return;
        }

        if (killer != null) {
            Quest.Type type = Quest.Type.MOB_OLDUR;
            plugin.getQuestManager().addProgress(killer, type, 1);
        }
    }
}
