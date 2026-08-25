package com.optim.towny.commands;

import com.optim.towny.OptimTowny;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HaritaCommand implements CommandExecutor {

    private final OptimTowny plugin;

    public HaritaCommand(OptimTowny plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Bu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }
        plugin.getMapRenderer().sendMap(player);
        return true;
    }
}
