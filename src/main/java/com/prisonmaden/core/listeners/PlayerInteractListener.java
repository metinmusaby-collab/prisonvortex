package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final PrisonMaden plugin;

    public PlayerInteractListener(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Sadece ana el ile tetikle, ayni tikin iki kere islenmesini engelle
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action aksiyon = event.getAction();
        if (aksiyon != Action.RIGHT_CLICK_AIR && aksiyon != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack elindeki = event.getItem();
        if (!plugin.getKitManager().ozelKazmaMi(elindeki)) return;

        event.setCancelled(true);
        plugin.getGUIManager().yukseltMenusuAc(event.getPlayer());
    }
}
