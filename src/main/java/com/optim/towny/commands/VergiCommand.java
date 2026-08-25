package com.optim.towny.commands;

import com.optim.towny.OptimTowny;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VergiCommand implements CommandExecutor {

    private final OptimTowny plugin;

    public VergiCommand(OptimTowny plugin) {
        this.plugin = plugin;
    }

    private String prefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("topla")) {
            if (!sender.hasPermission("optimtowny.admin")) {
                sender.sendMessage(prefix() + ChatColor.RED + "Bu komutu kullanma yetkin yok.");
                return true;
            }
            plugin.getTaxManager().collectAll();
            sender.sendMessage(prefix() + ChatColor.GREEN + "Vergi toplama işlemi manuel olarak tetiklendi.");
            return true;
        }

        sender.sendMessage(prefix() + ChatColor.GOLD + "=== Vergi Sistemi ===");
        sender.sendMessage(ChatColor.GRAY + "Oyuncu başı vergi: " + plugin.getConfig().getDouble("vergi.oyuncu-basi-vergi"));
        sender.sendMessage(ChatColor.GRAY + "Ulus vergisi: " + plugin.getConfig().getDouble("vergi.kasaba-ulus-vergisi"));
        sender.sendMessage(ChatColor.GRAY + "Toplama aralığı: " + plugin.getConfig().getLong("vergi.toplama-araligi-dakika") + " dakika");
        return true;
    }
}
