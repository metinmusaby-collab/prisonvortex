package com.optim.towny.util;

import com.optim.towny.OptimTowny;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Basit, harici eklenti bağımlılığı olmayan dahili para sistemi.
 * Sunucuda Vault + bir ekonomi eklentisi varsa ileride entegre edilebilir;
 * şimdilik OptimTowny kendi içinde oyuncu bakiyelerini tutar.
 */
public class Economy {

    private final OptimTowny plugin;
    private final Map<UUID, Double> balances = new HashMap<>();

    public Economy(OptimTowny plugin) {
        this.plugin = plugin;
    }

    public double getBalance(UUID player) {
        return balances.computeIfAbsent(player, k -> plugin.getConfig().getDouble("genel.varsayilan-para", 200.0));
    }

    public boolean has(UUID player, double amount) {
        return getBalance(player) >= amount;
    }

    public void deposit(UUID player, double amount) {
        balances.put(player, getBalance(player) + amount);
    }

    public boolean withdraw(UUID player, double amount) {
        double bal = getBalance(player);
        if (bal < amount) return false;
        balances.put(player, bal - amount);
        return true;
    }

    public Map<UUID, Double> raw() {
        return balances;
    }

    public void setBalance(UUID player, double amount) {
        balances.put(player, amount);
    }
}
