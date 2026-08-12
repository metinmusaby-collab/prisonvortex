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
        if (slot < 0 || slot >= event.getInventory().getSize()) return; // oyuncu envanterine tiklama

        switch (holder.getTur()) {
            case AYARLAR -> panelTiklandi(oyuncu, slot, event.getClick().isRightClick());
            case YUKSELT -> yukseltTiklandi(oyuncu, slot);
            case CANTA -> {
                if (slot == 40) {
                    plugin.getEkonomiManager().hepsiniSat(oyuncu);
                    plugin.getGUIManager().cantaMenusuAc(oyuncu);
                }
            }
            case MINYON_CANTA -> {
                if (slot == 40) {
                    plugin.getEkonomiManager().minyonCantasiniSat(oyuncu);
                    plugin.getGUIManager().minyonCantaMenusuAc(oyuncu);
                }
            }
        }
    }

    private void panelTiklandi(Player oyuncu, int slot, boolean sagTik) {
        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        switch (slot) {
            case 10 -> {
                oyuncu.closeInventory();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + oyuncu.getName() + " " + veri.getDunyaAdi());
            }
            case 12 -> plugin.getGUIManager().cantaMenusuAc(oyuncu);
            case 14 -> {
                if (!veri.minyonVarMi()) {
                    minyonSatinAl(oyuncu, veri);
                } else if (sagTik) {
                    plugin.getMinyonManager().gelistir(oyuncu);
                } else {
                    plugin.getMinyonManager().acKapaDegistir(oyuncu);
                }
                plugin.getGUIManager().panelMenusuAc(oyuncu);
            }
            case 16 -> {
                plugin.getUcusManager().acKapaDegistir(oyuncu);
                plugin.getGUIManager().panelMenusuAc(oyuncu);
            }
            case 28 -> {
                oyuncu.closeInventory();
                plugin.getRankManager().rankAtla(oyuncu);
            }
            case 30 -> {
                plugin.getKitManager().kitVer(oyuncu);
                oyuncu.closeInventory();
            }
            case 32 -> {
                plugin.getMineWorldManager().yenile(oyuncu);
                oyuncu.closeInventory();
            }
            case 34 -> {
                veri.setZiyaretAcik(!veri.isZiyaretAcik());
                plugin.getPlayerDataManager().kaydet();
                String durum = veri.isZiyaretAcik() ? "&aACIK" : "&cKAPALI";
                oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7Ziyaret artik " + durum + "&7!"));
                plugin.getGUIManager().panelMenusuAc(oyuncu);
            }
            case 40 -> {
                if (oyuncu.hasPermission("prisonmaden.admin")) {
                    oyuncu.closeInventory();
                    plugin.getMineWorldManager().sil(oyuncu);
                }
            }
            default -> {
            }
        }
    }

    private void minyonSatinAl(Player oyuncu, OyuncuVerisi veri) {
        long maliyet = 2000;
        if (veri.getPara() < maliyet) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&6Maden&8] &cYetersiz para! Gerekli: " + maliyet + " para."));
            return;
        }
        veri.setPara(veri.getPara() - maliyet);
        plugin.getPlayerDataManager().kaydet();
        oyuncu.getInventory().addItem(plugin.getMinyonManager().minyonTasiOlustur());
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&6Maden&8] &aMinyon Tasi envanterine eklendi! Yere koymak icin tikla."));
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
