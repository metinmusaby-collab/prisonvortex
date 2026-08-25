package com.optim.towny.commands;

import com.optim.towny.OptimTowny;
import com.optim.towny.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GorevCommand implements CommandExecutor {

    private final OptimTowny plugin;

    public GorevCommand(OptimTowny plugin) {
        this.plugin = plugin;
    }

    private String prefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("dil.prefix", ""));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Bu komut sadece oyuncular tarafından kullanılabilir.");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(prefix() + "Kullanım: /gorev <liste|al|ilerleme>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "liste" -> {
                player.sendMessage(ChatColor.GOLD + "=== Görevler ===");
                for (Quest q : plugin.getQuestManager().getAvailableQuests()) {
                    player.sendMessage(ChatColor.GRAY + "- [" + q.getId() + "] " + q.getAciklama() +
                            " (Ödül: " + q.getOdulPara() + ")");
                }
            }
            case "al" -> {
                if (args.length < 2) {
                    player.sendMessage(prefix() + "Kullanım: /gorev al <gorev-id>");
                    return true;
                }
                if (plugin.getQuestManager().acceptQuest(player, args[1])) {
                    player.sendMessage(prefix() + ChatColor.GREEN + "Görev alındı: " + args[1]);
                } else {
                    player.sendMessage(prefix() + ChatColor.RED + "Görev bulunamadı.");
                }
            }
            case "ilerleme" -> {
                player.sendMessage(ChatColor.GOLD + "=== Aktif Görevlerin ===");
                for (String id : plugin.getQuestManager().getActiveQuestIds(player.getUniqueId())) {
                    Quest q = plugin.getQuestManager().findQuest(id);
                    if (q == null) continue;
                    int prog = plugin.getQuestManager().getProgress(player.getUniqueId(), id);
                    player.sendMessage(ChatColor.GRAY + "- " + q.getAciklama() + ": " + prog + "/" + q.getHedefMiktar());
                }
                player.sendMessage(ChatColor.YELLOW + "Toplam tamamlanan görev: " +
                        plugin.getQuestManager().getCompletedCount(player.getUniqueId()));
            }
            default -> player.sendMessage(prefix() + "Bilinmeyen alt komut.");
        }
        return true;
    }
}
