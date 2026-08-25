package com.optim.towny.commands;

import com.optim.towny.OptimTowny;
import com.optim.towny.town.Town;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KasabaCommand implements CommandExecutor {

    private final OptimTowny plugin;

    public KasabaCommand(OptimTowny plugin) {
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
            player.sendMessage(prefix() + "Kullanım: /kasaba <kur|katil|ayril|bilgi|liste|davet|rutbe|para>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "kur" -> handleCreate(player, args);
            case "katil" -> handleJoin(player, args);
            case "ayril" -> handleLeave(player);
            case "bilgi" -> handleInfo(player, args);
            case "liste" -> handleList(player);
            case "para" -> handleMoney(player, args);
            case "rutbe" -> handlePromote(player, args);
            case "claim", "iddia" -> handleClaim(player);
            default -> player.sendMessage(prefix() + "Bilinmeyen alt komut.");
        }
        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(prefix() + "Kullanım: /kasaba kur <isim>");
            return;
        }
        if (plugin.getTownManager().getTownOf(player.getUniqueId()) != null) {
            player.sendMessage(prefix() + ChatColor.RED + "Zaten bir kasabaya üyesin.");
            return;
        }
        if (!plugin.getTownManager().canCreateMoreTowns()) {
            player.sendMessage(prefix() + ChatColor.RED + "Sunucudaki kasaba limitine (" +
                    plugin.getTownManager().getMaxTowns() + ") ulaşıldı!");
            return;
        }
        double price = plugin.getConfig().getDouble("genel.kasaba-kurma-fiyati", 500.0);
        if (!plugin.getEconomy().has(player.getUniqueId(), price)) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasaba kurmak için " + price + " paran yok.");
            return;
        }
        Town town = plugin.getTownManager().createTown(args[1], player);
        if (town == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Bu isimde bir kasaba zaten var.");
            return;
        }
        plugin.getEconomy().withdraw(player.getUniqueId(), price);
        player.sendMessage(prefix() + ChatColor.GREEN + "'" + town.getName() + "' kasabası kuruldu!");
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(prefix() + "Kullanım: /kasaba katil <isim>");
            return;
        }
        Town town = plugin.getTownManager().getTown(args[1]);
        if (town == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasaba bulunamadı.");
            return;
        }
        if (plugin.getTownManager().joinTown(player, town)) {
            player.sendMessage(prefix() + ChatColor.GREEN + "'" + town.getName() + "' kasabasına katıldın!");
        } else {
            player.sendMessage(prefix() + ChatColor.RED + "Katılamadın (zaten üyesin ya da kasaba dolu).");
        }
    }

    private void handleLeave(Player player) {
        if (plugin.getTownManager().leaveTown(player)) {
            player.sendMessage(prefix() + ChatColor.YELLOW + "Kasabandan ayrıldın.");
        } else {
            player.sendMessage(prefix() + ChatColor.RED + "Bir kasabaya üye değilsin.");
        }
    }

    private void handleInfo(Player player, String[] args) {
        Town town = args.length >= 2 ? plugin.getTownManager().getTown(args[1])
                : plugin.getTownManager().getTownOf(player.getUniqueId());
        if (town == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasaba bulunamadı.");
            return;
        }
        player.sendMessage(ChatColor.GOLD + "=== " + town.getName() + " ===");
        player.sendMessage(ChatColor.GRAY + "Sahip: " + plugin.getServer().getOfflinePlayer(town.getOwner()).getName());
        player.sendMessage(ChatColor.GRAY + "Üye sayısı: " + town.getMembers().size());
        player.sendMessage(ChatColor.GRAY + "Bakiye: " + town.getBalance());
        player.sendMessage(ChatColor.GRAY + "Ulus: " + (town.getNationName() == null ? "Yok" : town.getNationName()));
        player.sendMessage(ChatColor.GRAY + "Toprak (chunk): " + town.getTotalClaims());
        player.sendMessage(ChatColor.GRAY + "Ödenmemiş vergi: " + town.getUnpaidTaxCount());
    }

    private void handleList(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Kasabalar (" + plugin.getTownManager().getAllTowns().size() +
                "/" + plugin.getTownManager().getMaxTowns() + ") ===");
        for (Town t : plugin.getTownManager().getAllTowns()) {
            player.sendMessage(ChatColor.GRAY + "- " + t.getName() + " (" + t.getMembers().size() + " üye)");
        }
    }

    private void handleMoney(Player player, String[] args) {
        Town town = plugin.getTownManager().getTownOf(player.getUniqueId());
        if (town == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Bir kasabaya üye değilsin.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage(prefix() + "Kullanım: /kasaba para <yatir|cek> <miktar>");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(prefix() + ChatColor.RED + "Geçersiz miktar.");
            return;
        }
        if (args[1].equalsIgnoreCase("yatir")) {
            if (!plugin.getEconomy().withdraw(player.getUniqueId(), amount)) {
                player.sendMessage(prefix() + ChatColor.RED + "Yeterli paran yok.");
                return;
            }
            town.deposit(amount);
            player.sendMessage(prefix() + ChatColor.GREEN + amount + " kasaba kasasına yatırıldı.");
        } else if (args[1].equalsIgnoreCase("cek")) {
            if (!town.getRank(player.getUniqueId()).equals("Kurucu") &&
                    !town.getRank(player.getUniqueId()).equals("Yönetici")) {
                player.sendMessage(prefix() + ChatColor.RED + "Kasadan para çekmek için yetkin yok.");
                return;
            }
            if (!town.withdraw(amount)) {
                player.sendMessage(prefix() + ChatColor.RED + "Kasada yeterli para yok.");
                return;
            }
            plugin.getEconomy().deposit(player.getUniqueId(), amount);
            player.sendMessage(prefix() + ChatColor.GREEN + amount + " kasadan çekildi.");
        }
    }

    private void handlePromote(Player player, String[] args) {
        Town town = plugin.getTownManager().getTownOf(player.getUniqueId());
        if (town == null || !town.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(prefix() + ChatColor.RED + "Sadece kasaba kurucusu rütbe verebilir.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage(prefix() + "Kullanım: /kasaba rutbe <oyuncu>");
            return;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null || !town.isMember(target.getUniqueId())) {
            player.sendMessage(prefix() + ChatColor.RED + "Oyuncu bu kasabada değil.");
            return;
        }
        UUID uuid = target.getUniqueId();
        int completed = plugin.getQuestManager().getCompletedCount(uuid);
        if (plugin.getTownRank().promote(town, uuid, completed)) {
            player.sendMessage(prefix() + ChatColor.GREEN + target.getName() + " rütbesi yükseltildi: " + town.getRank(uuid));
            target.sendMessage(prefix() + ChatColor.GOLD + "Rütben yükseltildi: " + town.getRank(uuid));
        } else {
            player.sendMessage(prefix() + ChatColor.RED + "Yeterli görev tamamlanmamış ya da zaten en üst rütbede.");
        }
    }

    private void handleClaim(Player player) {
        Town town = plugin.getTownManager().getTownOf(player.getUniqueId());
        if (town == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Bir kasabaya üye değilsin.");
            return;
        }
        int maxChunk = plugin.getConfig().getInt("genel.kasaba-basina-max-chunk", 36);
        if (town.getTotalClaims() >= maxChunk) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasabanın toprak limiti (" + maxChunk + ") doldu.");
            return;
        }
        double price = plugin.getConfig().getDouble("genel.chunk-fiyati", 50.0);
        if (!town.withdraw(price)) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasabanın kasasında yeterli para yok (" + price + " gerekli).");
            return;
        }
        if (town.claimChunk(player.getLocation().getChunk())) {
            player.sendMessage(prefix() + ChatColor.GREEN + "Bu bölge kasabaya eklendi!");
        } else {
            town.deposit(price); // zaten claimliyse parayı iade et
            player.sendMessage(prefix() + ChatColor.RED + "Bu bölge zaten kasabana ait.");
        }
    }
}
