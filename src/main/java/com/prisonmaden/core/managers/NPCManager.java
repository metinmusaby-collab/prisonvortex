package com.prisonmaden.core.managers;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

/**
 * Her maden dunyasinin spawn noktasina yerlestirilen, tiklaninca
 * maden panelini acan NPC'yi (ArmorStand) yonetir.
 */
public class NPCManager {

    private final PrisonMaden plugin;
    private final NamespacedKey panelEtiketi;

    public NPCManager(PrisonMaden plugin) {
        this.plugin = plugin;
        this.panelEtiketi = new NamespacedKey(plugin, "prisonmaden_panel_npc");
    }

    public void panelNpcOlustur(Location konum) {
        konum.getWorld().spawn(konum, ArmorStand.class, es -> {
            es.setInvulnerable(true);
            es.setGravity(false);
            es.setCanPickupItems(false);
            es.setCustomNameVisible(true);
            es.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e&lMaden Paneli"));
            es.setArms(true);
            es.getPersistentDataContainer().set(panelEtiketi, PersistentDataType.BYTE, (byte) 1);
        });
    }

    public boolean panelNpcMi(Entity entity) {
        if (!(entity instanceof ArmorStand)) return false;
        Byte deger = entity.getPersistentDataContainer().get(panelEtiketi, PersistentDataType.BYTE);
        return deger != null && deger == 1;
    }
}
