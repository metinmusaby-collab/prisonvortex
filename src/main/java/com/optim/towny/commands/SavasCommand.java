package com.optim.towny.commands;

import com.optim.towny.OptimTowny;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SavasCommand implements CommandExecutor {

    private final OptimTowny plugin;

    public SavasCommand(OptimTowny plugin) {
        this.plugin = plugin;
    }

    private String prefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendInfo(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "bilgi" -> sendInfo(sender);
            case "baslat" -> {
                if (!sender.hasPermission("optimtowny.admin")) {
                    sender.sendMessage(prefix() + ChatColor.RED + "Bu komutu kullanma yetkin yok.");
                    return true;
                }
                plugin.getWarManager().forceStart();
            }
            case "bitir" -> {
                if (!sender.hasPermission("optimtowny.admin")) {
                    sender.sendMessage(prefix() + ChatColor.RED + "Bu komutu kullanma yetkin yok.");
                    return true;
                }
                plugin.getWarManager().forceEnd();
            }
            case "sifirla" -> {
                if (!sender.hasPermission("optimtowny.admin")) {
                    sender.sendMessage(prefix() + ChatColor.RED + "Bu komutu kullanma yetkin yok.");
                    return true;
                }
                plugin.getWarManager().resetToSchedule();
                sender.sendMessage(prefix() + ChatColor.GREEN + "Savaş modu otomatik takvime döndürüldü.");
            }
            default -> sender.sendMessage(prefix() + "Kullanım: /savas <bilgi|baslat|bitir|sifirla>");
        }
        return true;
    }

    private void sendInfo(CommandSender sender) {
        boolean active = plugin.getWarManager().isWarActive();
        sender.sendMessage(prefix() + (active
                ? ChatColor.RED + "Savaş modu şu an AKTİF."
                : ChatColor.GREEN + "Savaş modu şu an kapalı."));
        sender.sendMessage(ChatColor.GRAY + "Zamanlanmış savaş saatleri: " +
                String.join(", ", plugin.getWarManager().getScheduleDescription()));
    }
}
