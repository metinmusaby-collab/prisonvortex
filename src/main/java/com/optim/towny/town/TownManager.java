package com.optim.towny.town;

import com.optim.towny.OptimTowny;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.*;

public class TownManager {

    private final OptimTowny plugin;
    private final Map<String, Town> townsByName = new LinkedHashMap<>();
    private final Map<UUID, Town> townByMember = new HashMap<>();

    public TownManager(OptimTowny plugin) {
        this.plugin = plugin;
    }

    public int getMaxTowns() {
        return plugin.getConfig().getInt("genel.maksimum-kasaba-sayisi", 10);
    }

    public boolean canCreateMoreTowns() {
        return townsByName.size() < getMaxTowns();
    }

    public Town createTown(String name, Player owner) {
        if (townsByName.containsKey(name.toLowerCase())) return null;
        if (!canCreateMoreTowns()) return null;
        Town town = new Town(name, owner.getUniqueId());
        town.setSpawn(owner.getLocation());
        townsByName.put(name.toLowerCase(), town);
        townByMember.put(owner.getUniqueId(), town);
        return town;
    }

    public boolean deleteTown(String name) {
        Town town = townsByName.remove(name.toLowerCase());
        if (town == null) return false;
        for (UUID member : town.getMembers()) {
            townByMember.remove(member);
        }
        return true;
    }

    public Town getTown(String name) {
        if (name == null) return null;
        return townsByName.get(name.toLowerCase());
    }

    public Town getTownOf(UUID player) {
        return townByMember.get(player);
    }

    public Town getTownAt(Chunk chunk) {
        for (Town town : townsByName.values()) {
            if (town.isClaimed(chunk)) return town;
        }
        return null;
    }

    public boolean joinTown(Player player, Town town) {
        if (townByMember.containsKey(player.getUniqueId())) return false;
        int maxUye = plugin.getConfig().getInt("genel.kasaba-basina-max-oyuncu", 20);
        if (town.getMembers().size() >= maxUye) return false;
        town.addMember(player.getUniqueId());
        townByMember.put(player.getUniqueId(), town);
        return true;
    }

    public boolean leaveTown(Player player) {
        Town town = townByMember.get(player.getUniqueId());
        if (town == null) return false;
        town.removeMember(player.getUniqueId());
        townByMember.remove(player.getUniqueId());
        // Kurucu ayrılırsa ve başka üye yoksa kasaba dağılır
        if (town.getMembers().isEmpty()) {
            deleteTown(town.getName());
        } else if (town.getOwner().equals(player.getUniqueId())) {
            UUID newOwner = town.getMembers().iterator().next();
            town.setOwner(newOwner);
            town.setRank(newOwner, "Kurucu");
        }
        return true;
    }

    public Collection<Town> getAllTowns() {
        return townsByName.values();
    }

    /**
     * data.yml'den yüklenen bir kasabayı bellek içi haritalara kaydeder.
     */
    public void registerLoadedTown(Town town) {
        townsByName.put(town.getName().toLowerCase(), town);
        for (UUID member : town.getMembers()) {
            townByMember.put(member, town);
        }
    }
}
