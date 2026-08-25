package com.optim.towny.commands;

import com.optim.towny.OptimTowny;
import com.optim.towny.nation.Nation;
import com.optim.towny.town.Town;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UlusCommand implements CommandExecutor {

    private final OptimTowny plugin;

    public UlusCommand(OptimTowny plugin) {
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
            player.sendMessage(prefix() + "Kullanım: /ulus <kur|ekle|cikar|bilgi|liste|para>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "kur" -> handleCreate(player, args);
            case "ekle" -> handleAddTown(player, args);
            case "cikar" -> handleRemoveTown(player, args);
            case "bilgi" -> handleInfo(player, args);
            case "liste" -> handleList(player);
            default -> player.sendMessage(prefix() + "Bilinmeyen alt komut.");
        }
        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(prefix() + "Kullanım: /ulus kur <isim>");
            return;
        }
        Town town = plugin.getTownManager().getTownOf(player.getUniqueId());
        if (town == null || !town.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(prefix() + ChatColor.RED + "Sadece kasaba kurucuları ulus kurabilir.");
            return;
        }
        double price = plugin.getConfig().getDouble("ulus.ulus-kurma-fiyati", 2000.0);
        if (!town.withdraw(price)) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasabanın kasasında yeterli para yok (" + price + " gerekli).");
            return;
        }
        Nation nation = plugin.getNationManager().createNation(args[1], town);
        if (nation == null) {
            town.deposit(price);
            player.sendMessage(prefix() + ChatColor.RED + "Bu isimde bir ulus var ya da kasaban zaten bir ulusa bağlı.");
            return;
        }
        player.sendMessage(prefix() + ChatColor.GREEN + "'" + nation.getName() + "' ulusu kuruldu! Başkent: " + town.getName());
    }

    private void handleAddTown(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(prefix() + "Kullanım: /ulus ekle <kasaba-adi>");
            return;
        }
        Town yourTown = plugin.getTownManager().getTownOf(player.getUniqueId());
        if (yourTown == null || yourTown.getNationName() == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Bir ulusun başkenti/üyesi değilsin.");
            return;
        }
        Nation nation = plugin.getNationManager().getNation(yourTown.getNationName());
        Town targetTown = plugin.getTownManager().getTown(args[1]);
        if (nation == null || targetTown == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Ulus veya kasaba bulunamadı.");
            return;
        }
        if (plugin.getNationManager().addTown(nation, targetTown)) {
            player.sendMessage(prefix() + ChatColor.GREEN + targetTown.getName() + " kasabası " + nation.getName() + " ulusuna katıldı.");
        } else {
            player.sendMessage(prefix() + ChatColor.RED + "Eklenemedi (kasaba zaten bir ulusa bağlı ya da ulus limiti dolu).");
        }
    }

    private void handleRemoveTown(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(prefix() + "Kullanım: /ulus cikar <kasaba-adi>");
            return;
        }
        Town targetTown = plugin.getTownManager().getTown(args[1]);
        if (targetTown == null || targetTown.getNationName() == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Kasaba bir ulusa bağlı değil.");
            return;
        }
        Nation nation = plugin.getNationManager().getNation(targetTown.getNationName());
        plugin.getNationManager().removeTown(nation, targetTown);
        player.sendMessage(prefix() + ChatColor.YELLOW + targetTown.getName() + " ulustan çıkarıldı.");
    }

    private void handleInfo(Player player, String[] args) {
        Nation nation;
        if (args.length >= 2) {
            nation = plugin.getNationManager().getNation(args[1]);
        } else {
            Town town = plugin.getTownManager().getTownOf(player.getUniqueId());
            nation = town != null && town.getNationName() != null ? plugin.getNationManager().getNation(town.getNationName()) : null;
        }
        if (nation == null) {
            player.sendMessage(prefix() + ChatColor.RED + "Ulus bulunamadı.");
            return;
        }
        player.sendMessage(ChatColor.GOLD + "=== " + nation.getName() + " ===");
        player.sendMessage(ChatColor.GRAY + "Başkent: " + nation.getCapitalTownName());
        player.sendMessage(ChatColor.GRAY + "Bakiye: " + nation.getBalance());
        player.sendMessage(ChatColor.GRAY + "Kasabalar: " + String.join(", ", nation.getTownNames()));
    }

    private void handleList(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Uluslar ===");
        for (Nation n : plugin.getNationManager().getAllNations()) {
            player.sendMessage(ChatColor.GRAY + "- " + n.getName() + " (" + n.getTownNames().size() + " kasaba)");
        }
    }
}
