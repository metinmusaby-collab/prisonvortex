package com.prisonmaden.core.listeners;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.gui.MadenGUIHolder;
import com.prisonmaden.core.model.OyuncuVerisi;
import com.prisonmaden.core.model.YukseltmeTuru;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIClickListener implements Listener {

    private final PrisonMaden plugin;

    public GUIClickListener(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MadenGUIHolder holder)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player oyuncu)) return;
        int slot = event.getRawSlot();

        if (holder.getTur() == MadenGUIHolder.Tur.AYARLAR) {
            ayarlarTiklandi(oyuncu, slot);
        } else if (holder.getTur() == MadenGUIHolder.Tur.YUKSELT) {
            yukseltTiklandi(oyuncu, slot);
        }
    }

    private void ayarlarTiklandi(Player oyuncu, int slot) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (slot == 11) {
            oyuncu.closeInventory();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + oyuncu.getName() + " " + veri.getDunyaAdi());
        } else if (slot == 13) {
            oyuncu.closeInventory();
            plugin.getMineWorldManager().sil(oyuncu);
        } else if (slot == 15) {
            veri.setZiyaretAcik(!veri.isZiyaretAcik());
            plugin.getPlayerDataManager().kaydet();
            String durum = veri.isZiyaretAcik() ? "&aACIK" : "&cKAPALI";
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7Ziyaret artik " + durum + "&7!"));
            plugin.getGUIManager().ayarlarMenusuAc(oyuncu); // menuyu tazele
        }
    }

    private void yukseltTiklandi(Player oyuncu, int slot) {
        ItemStack kazma = oyuncu.getInventory().getItemInMainHand();
        if (!plugin.getKitManager().ozelKazmaMi(kazma)) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &cBaslangic kazani elinde tutmalisin!"));
            oyuncu.closeInventory();
            return;
        }

        YukseltmeTuru tur = switch (slot) {
            case 11 -> YukseltmeTuru.SERVET;
            case 13 -> YukseltmeTuru.VERIMLILIK;
            case 15 -> YukseltmeTuru.KIRILMAZLIK;
            default -> null;
        };

        if (tur == null) return;

        plugin.getPickaxeManager().yukselt(oyuncu, kazma, tur);
        plugin.getGUIManager().yukseltMenusuAc(oyuncu); // guncel seviyeleri goster
    }
}
