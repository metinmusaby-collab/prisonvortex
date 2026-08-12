package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChangeListener implements Listener {

    private final PrisonMaden plugin;

    public WorldChangeListener(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!event.getPlayer().getWorld().getName().startsWith("maden_")) {
            plugin.getUcusManager().ucusuKapat(event.getPlayer());
        }
    }
}
