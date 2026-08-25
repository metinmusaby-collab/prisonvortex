package com.optim.towny.nation;

import com.optim.towny.OptimTowny;
import com.optim.towny.town.Town;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class NationManager {

    private final OptimTowny plugin;
    private final Map<String, Nation> nations = new LinkedHashMap<>();

    public NationManager(OptimTowny plugin) {
        this.plugin = plugin;
    }

    public Nation createNation(String name, Town capitalTown) {
        if (nations.containsKey(name.toLowerCase())) return null;
        if (capitalTown.getNationName() != null) return null;
        Nation nation = new Nation(name, capitalTown.getName());
        nations.put(name.toLowerCase(), nation);
        capitalTown.setNationName(name);
        return nation;
    }

    public Nation getNation(String name) {
        if (name == null) return null;
        return nations.get(name.toLowerCase());
    }

    public boolean addTown(Nation nation, Town town) {
        int max = plugin.getConfig().getInt("ulus.ulus-basina-max-kasaba", 10);
        if (nation.getTownNames().size() >= max) return false;
        if (town.getNationName() != null) return false;
        nation.addTown(town.getName());
        town.setNationName(nation.getName());
        return true;
    }

    public boolean removeTown(Nation nation, Town town) {
        nation.removeTown(town.getName());
        town.setNationName(null);
        if (nation.getTownNames().isEmpty()) {
            nations.remove(nation.getName().toLowerCase());
        }
        return true;
    }

    public Collection<Nation> getAllNations() {
        return nations.values();
    }

    /**
     * data.yml'den yüklenen bir ulusu bellek içi haritaya kaydeder.
     */
    public void registerLoadedNation(Nation nation) {
        nations.put(nation.getName().toLowerCase(), nation);
    }
}
