package com.prisonmaden.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class WorldSafetyListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player kurban)) return;
        if (!kurban.getWorld().getName().startsWith("maden_")) return;

        Player saldiran = null;
        if (event.getDamager() instanceof Player oyuncu) {
            saldiran = oyuncu;
        } else if (event.getDamager() instanceof Projectile mermi && mermi.getShooter() instanceof Player oyuncu) {
            saldiran = oyuncu;
        }

        if (saldiran != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getWorld().getName().startsWith("maden_")) {
            event.setCancelled(true);
        }
    }
}
