package com.example.skriptveri.util;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skript uzerinden set edilen verileri tutan basit bellek-ici depo.
 * - Global veri: sunucu genelinde tek deger (oyuncudan bagimsiz)
 * - Oyuncu verisi: her oyuncuya ozel deger
 *
 * Ileride kalici tutmak istersen (sunucu restart sonrasi kaybolmasin diye)
 * buraya bir YAML/SQLite kaydetme-yukleme katmani eklenebilir.
 */
public final class DataManager {

    private static final Map<String, Object> GLOBAL_DATA = new ConcurrentHashMap<>();
    private static final Map<UUID, Map<String, Object>> PLAYER_DATA = new ConcurrentHashMap<>();

    private DataManager() {
    }

    // ---- Global veri ----

    public static void setGlobal(String key, Object value) {
        if (value == null) {
            GLOBAL_DATA.remove(key.toLowerCase());
        } else {
            GLOBAL_DATA.put(key.toLowerCase(), value);
        }
    }

    public static Object getGlobal(String key) {
        return GLOBAL_DATA.get(key.toLowerCase());
    }

    // ---- Oyuncu verisi ----

    public static void setPlayer(Player player, String key, Object value) {
        Map<String, Object> map = PLAYER_DATA.computeIfAbsent(player.getUniqueId(), u -> new ConcurrentHashMap<>());
        if (value == null) {
            map.remove(key.toLowerCase());
        } else {
            map.put(key.toLowerCase(), value);
        }
    }

    public static Object getPlayer(Player player, String key) {
        Map<String, Object> map = PLAYER_DATA.get(player.getUniqueId());
        if (map == null) return null;
        return map.get(key.toLowerCase());
    }

    public static void clearPlayer(UUID uuid) {
        PLAYER_DATA.remove(uuid);
    }
}
