package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class KitManager {

    private final PrisonMaden plugin;
    private final NamespacedKey kazmaEtiketi;

    public KitManager(PrisonMaden plugin) {
        this.plugin = plugin;
        this.kazmaEtiketi = new NamespacedKey(plugin, "prisonmaden_kazma");
    }

    /**
     * Sunucuya ilk giriste verilen hazir kit.
     */
    public void kitVer(Player oyuncu) {
        oyuncu.getInventory().addItem(ozelKazmaOlustur());
        oyuncu.getInventory().addItem(new ItemStack(Material.BREAD, 32));

        mesajGonder(oyuncu, "&aHosgeldin! Baslangic kitin verildi.");
        mesajGonder(oyuncu, "&7Kendi madenini olusturmak icin &e/maden &7yaz!");
    }

    public ItemStack ozelKazmaOlustur() {
        ItemStack kazma = new ItemStack(Material.WOODEN_PICKAXE);
        ItemMeta meta = kazma.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bVortex Kazma"));
            meta.setLore(List.of(
                    ChatColor.translateAlternateColorCodes('&', "&7Prison maden kazman."),
                    ChatColor.translateAlternateColorCodes('&', "&8Sag tikla ve yukselt!")
            ));
            meta.getPersistentDataContainer().set(kazmaEtiketi, PersistentDataType.BYTE, (byte) 1);
            kazma.setItemMeta(meta);
        }
        return kazma;
    }

    /**
     * Verilen esyanin ozel prison kazmasi olup olmadigini kontrol eder.
     */
    public boolean ozelKazmaMi(ItemStack esya) {
        if (esya == null || !esya.hasItemMeta()) return false;
        ItemMeta meta = esya.getItemMeta();
        if (meta == null) return false;
        Byte deger = meta.getPersistentDataContainer().get(kazmaEtiketi, PersistentDataType.BYTE);
        return deger != null && deger == 1;
    }

    private void mesajGonder(Player oyuncu, String mesaj) {
        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + mesaj));
    }
}
