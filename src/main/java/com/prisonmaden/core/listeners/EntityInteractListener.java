package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class EntityInteractListener implements Listener {

    private final PrisonMaden plugin;

    public EntityInteractListener(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        islemeAl(event.getPlayer(), event);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        islemeAl(event.getPlayer(), event);
    }

    private void islemeAl(Player oyuncu, PlayerInteractEntityEvent event) {
        // Panel NPC'si
        if (plugin.getNPCManager().panelNpcMi(event.getRightClicked())) {
            event.setCancelled(true);
            plugin.getGUIManager().panelMenusuAc(oyuncu);
            return;
        }

        // Minyon
        UUID sahip = plugin.getMinyonManager().minyonSahibiUuidAl(event.getRightClicked());
        if (sahip != null) {
            event.setCancelled(true);
            if (!sahip.equals(oyuncu.getUniqueId())) {
                oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&6Maden&8] &cBu minyon sana ait degil!"));
                return;
            }
            plugin.getGUIManager().minyonCantaMenusuAc(oyuncu);
        }
    }
}
