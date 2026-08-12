package com.prisonmaden.core.commands;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MadenCommand implements CommandExecutor {

    private final PrisonMaden plugin;

    public MadenCommand(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player oyuncu)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        // /maden sil -> ARTIK SADECE YETKILILER (prisonmaden.admin)
        if (args.length > 0 && args[0].equalsIgnoreCase("sil")) {
            if (!sender.hasPermission("prisonmaden.admin")) {
                oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&6Maden&8] &cBu komutu kullanma yetkin yok!"));
                return true;
            }
            if (args.length > 1) {
                // /maden sil <oyuncu> - baska birinin madenini sil
                OfflinePlayer hedef = Bukkit.getOfflinePlayer(args[1]);
                oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&6Maden&8] &7" + args[1] + "'in madeni siliniyor..."));
                if (hedef.isOnline() && hedef.getPlayer() != null) {
                    plugin.getMineWorldManager().sil(hedef.getPlayer());
                } else {
                    oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&6Maden&8] &cOyuncu cevrimdisiyken maden silinemez (Multiverse tasima gerektirir)."));
                }
            } else {
                plugin.getMineWorldManager().sil(oyuncu);
            }
            return true;
        }

        // /maden yenile
        if (args.length > 0 && args[0].equalsIgnoreCase("yenile")) {
            plugin.getMineWorldManager().yenile(oyuncu);
            return true;
        }

        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (!veri.madeniVarMi()) {
            plugin.getMineWorldManager().girisVeyaOlustur(oyuncu);
            return true;
        }

        // Madeni var: gerekirse isinlat, sonra paneli ac (/maden ve /maden ayarlar ayni davranir)
        World hedefDunya = Bukkit.getWorld(veri.getDunyaAdi());
        if (hedefDunya == null || !oyuncu.getWorld().equals(hedefDunya)) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7Madenine isinlaniyorsun..."));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + oyuncu.getName() + " " + veri.getDunyaAdi());
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGUIManager().panelMenusuAc(oyuncu), 20L);
        } else {
            plugin.getGUIManager().panelMenusuAc(oyuncu);
        }

        return true;
    }
}
