package com.example.skriptveri;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.example.skriptveri.placeholder.SkriptVeriExpansion;
import com.example.skriptveri.skript.EffSetSkriptVeri;
import com.example.skriptveri.skript.ExprSkriptVeri;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class SkriptVeriPlugin extends JavaPlugin {

    private SkriptAddon addon;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Skript") == null) {
            getLogger().severe("Skript bulunamadi! Eklenti devre disi birakiliyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerSkriptAddon();
        registerPlaceholderApi();

        getLogger().info("SkriptVeri aktif edildi.");
    }

    private void registerSkriptAddon() {
        try {
            addon = Skript.registerAddon(this);
            // Sozdizimi siniflarinin static blogunu tetiklemek icin yukleniyor
            addon.loadClasses("com.example.skriptveri", "skript");
            // Not: Bazi Skript surumlerinde registerAddon sonrasi dogrudan
            // Class.forName ile de tetiklenebilir, gerekirse asagidakini kullan:
            // Class.forName(EffSetSkriptVeri.class.getName());
            // Class.forName(ExprSkriptVeri.class.getName());
        } catch (IOException e) {
            getLogger().severe("Skript sozdizimleri yuklenirken hata olustu: " + e.getMessage());
        }
    }

    private void registerPlaceholderApi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SkriptVeriExpansion().register();
            getLogger().info("PlaceholderAPI koprusu aktif: %skriptveri_<isim>% ve %skriptveri_global_<isim>%");
        } else {
            getLogger().warning("PlaceholderAPI bulunamadi, placeholder destegi devre disi.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("SkriptVeri devre disi birakildi.");
    }
}
