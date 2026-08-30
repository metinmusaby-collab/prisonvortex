package com.example.skriptveri.placeholder;

import com.example.skriptveri.util.DataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI uzerinden kullanim:
 *
 *   %skriptveri_isim%          -> oyuncuya ozel "isim" verisi (oyuncu online ise)
 *   %skriptveri_global_isim%   -> global "isim" verisi
 *
 * Skript tarafinda:
 *   set skript veri "isim" to "deger" for player
 *   set skript veri "isim" to "deger"   (global)
 */
public class SkriptVeriExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "skriptveri";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Sen";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (params.startsWith("global_")) {
            String key = params.substring("global_".length());
            Object value = DataManager.getGlobal(key);
            return value != null ? String.valueOf(value) : "";
        }

        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "";
        }

        Player player = (Player) offlinePlayer;
        Object value = DataManager.getPlayer(player, params);
        return value != null ? String.valueOf(value) : "";
    }
}
