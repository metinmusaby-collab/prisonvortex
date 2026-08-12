package com.prisonmaden.core.commands;

import com.prisonmaden.core.PrisonMaden;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankupCommand implements CommandExecutor {

    private final PrisonMaden plugin;

    public RankupCommand(PrisonMaden plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player oyuncu)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }
        plugin.getRankManager().rankAtla(oyuncu);
        return true;
    }
}
