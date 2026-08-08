package com.prisonmaden.core.commands;

import com.prisonmaden.core.PrisonMaden;
import com.prisonmaden.core.model.OyuncuVerisi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        if (args.length > 0 && args[0].equalsIgnoreCase("sil")) {
            plugin.getMineWorldManager().sil(oyuncu);
            return true;
        }

        OyuncuVerisi veri = plugin.getPlayerDataManager().veriAl(oyuncu.getUniqueId());

        if (!veri.madeniVarMi()) {
            plugin.getMineWorldManager().girisVeyaOlustur(oyuncu);
            return true;
        }

        // Madeni var: gerekirse isinlat, sonra menuyu ac (/maden ve /maden ayarlar ayni davranir)
        World hedefDunya = Bukkit.getWorld(veri.getDunyaAdi());
        if (hedefDunya == null || !oyuncu.getWorld().equals(hedefDunya)) {
            oyuncu.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Maden&8] &7Madenine isinlaniyorsun..."));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv tp " + oyuncu.getName() + " " + veri.getDunyaAdi());
            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGUIManager().ayarlarMenusuAc(oyuncu), 20L);
        } else {
            plugin.getGUIManager().ayarlarMenusuAc(oyuncu);
        }

        return true;
    }
}
