package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PrisonMaden plugin;

    public JoinListener(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(event.getPlayer().getUniqueId());
        if (!event.getPlayer().hasPlayedBefore() && !veri.isKitAlindi()) {
            plugin.getKitManager().kitVer(event.getPlayer());
            veri.setKitAlindi(true);
            plugin.getPlayerDataManager().kaydet();
        }
    }
}
