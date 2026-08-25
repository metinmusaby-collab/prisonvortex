package com.optim.towny.util;

import com.optim.towny.OptimTowny;
import com.optim.towny.town.Town;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class MapRenderer {

    private final OptimTowny plugin;

    public MapRenderer(OptimTowny plugin) {
        this.plugin = plugin;
    }

    /**
     * Oyuncunun bulunduğu bölgeyi merkez alarak metin tabanlı bir kasaba haritası basar.
     */
    public void sendMap(Player viewer) {
        int width = plugin.getConfig().getInt("harita.genislik", 21);
        int height = plugin.getConfig().getInt("harita.yukseklik", 21);
        if (width % 2 == 0) width++;
        if (height % 2 == 0) height++;

        String selfColor = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("harita.kendi-kasaba-rengi", "&a"));
        String allyColor = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("harita.dost-kasaba-rengi", "&b"));
        String enemyColor = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("harita.dusman-kasaba-rengi", "&c"));
        String emptyColor = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("harita.bos-alan-rengi", "&7"));

        Chunk center = viewer.getLocation().getChunk();
        Town viewerTown = plugin.getTownManager().getTownOf(viewer.getUniqueId());

        int halfW = width / 2;
        int halfH = height / 2;

        viewer.sendMessage(ChatColor.GOLD + "==== Kasaba Haritası (" + viewer.getWorld().getName() + ") ====");

        StringBuilder sb = new StringBuilder();
        for (int dz = -halfH; dz <= halfH; dz++) {
            sb.setLength(0);
            for (int dx = -halfW; dx <= halfW; dx++) {
                int cx = center.getX() + dx;
                int cz = center.getZ() + dz;

                if (dx == 0 && dz == 0) {
                    sb.append(ChatColor.WHITE).append("+");
                    continue;
                }

                Town town = plugin.getTownManager().getTownAt(viewer.getWorld().getChunkAt(cx, cz));
                if (town == null) {
                    sb.append(emptyColor).append(".");
                } else if (viewerTown != null && town.getName().equals(viewerTown.getName())) {
                    sb.append(selfColor).append("#");
                } else if (viewerTown != null && town.getNationName() != null &&
                        town.getNationName().equals(viewerTown.getNationName())) {
                    sb.append(allyColor).append("#");
                } else {
                    sb.append(enemyColor).append("#");
                }
            }
            viewer.sendMessage(sb.toString());
        }

        viewer.sendMessage(selfColor + "# " + ChatColor.GRAY + "Senin kasaban  " +
                allyColor + "# " + ChatColor.GRAY + "Dost/ulus  " +
                enemyColor + "# " + ChatColor.GRAY + "Diğer  " +
                emptyColor + ". " + ChatColor.GRAY + "Boş");
    }
}
