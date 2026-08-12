package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final PrisonMaden plugin;

    public BlockBreakListener(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material tur = event.getBlock().getType();
        if (!event.getPlayer().getWorld().getName().startsWith("maden_")) return;

        // Satilabilir bir cevherse: fiziksel dusme yerine dogrudan cantaya ekle
        if (plugin.getEkonomiManager().satilabilirMi(tur)) {
            event.setDropItems(false);
            plugin.getEkonomiManager().cantayaEkle(event.getPlayer(), tur, 1);
        }

        // Demir cevheri kirildiginda %5 ihtimalle ozel Yukseltme Sisesi dussun
        boolean demirCevheri = (tur == Material.IRON_ORE || tur == Material.DEEPSLATE_IRON_ORE);
        if (demirCevheri && plugin.getPickaxeManager().siseDusecekMi()) {
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(),
                    plugin.getPickaxeManager().siseOlustur()
            );
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&6Maden&8] &7Sanslisin! Bir &d&lYukseltme Sisesi&7 buldun!"));
        }
    }
}
