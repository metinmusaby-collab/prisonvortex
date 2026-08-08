package com.prisonmaden.core.commands;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MadenZiyaretCommand implements CommandExecutor {

    private final PrisonMaden plugin;

    public MadenZiyaretCommand(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player oyuncu)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        if (args.length < 1) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &cKullanim: /madenziyaret <oyuncu>"));
            return true;
        }

        OfflinePlayer hedef = Bukkit.getOfflinePlayer(args[0]);
        OyuncuVerisi hedefVeri = plugin.getPlayerDataManager().veriAl(hedef.getUniqueId());

        if (!hedefVeri.madeniVarMi()) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &cBu oyuncunun bir madeni yok!"));
            return true;
        }

        if (!hedefVeri.isZiyaretAcik()) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &c" + args[0] + " su an ziyaretlere kapali!"));
            return true;
        }

        oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7" + args[0] + "'in madenine isinlaniyorsun..."));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + oyuncu.getName() + " " + hedefVeri.getDunyaAdi());
        return true;
    }
}
