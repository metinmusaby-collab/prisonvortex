package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

        // 1) Ozel kazma -> yukseltme menusu ac
        if (plugin.getKitManager().ozelKazmaMi(elindeki)) {
            event.setCancelled(true);
            plugin.getGUIManager().yukseltMenusuAc(event.getPlayer());
            return;
        }

        // 2) Minyon Tasi -> kendi madeninde yere yerlestir
        if (plugin.getMinyonManager().minyonTasiMi(elindeki)) {
            event.setCancelled(true);

            OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(event.getPlayer().getUniqueId());
            if (!veri.madeniVarMi() || !event.getPlayer().getWorld().getName().equals(veri.getDunyaAdi())) {
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&6Maden&8] &cMinyonu sadece kendi madeninde yerlestirebilirsin!"));
                return;
            }

            Location konum = event.getPlayer().getLocation().getBlock().getLocation();
            boolean basarili = plugin.getMinyonManager().yerlestir(event.getPlayer(), konum);
            if (basarili) {
                elindeki.setAmount(elindeki.getAmount() - 1);
            }
        }
    }
}
