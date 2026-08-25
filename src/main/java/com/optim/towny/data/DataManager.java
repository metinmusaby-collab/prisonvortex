package com.optim.towny.data;

import com.optim.towny.OptimTowny;
import com.optim.towny.nation.Nation;
import com.optim.towny.town.Town;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * data.yml içine kasaba, ulus, chunk claim ve oyuncu bakiye verilerini kaydeder / okur.
 */
public class DataManager {

    private final OptimTowny plugin;
    private final File file;

    public DataManager(OptimTowny plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();

        // Kasabalar
        for (Town town : plugin.getTownManager().getAllTowns()) {
            String path = "kasabalar." + town.getName();
            yaml.set(path + ".sahip", town.getOwner().toString());
            yaml.set(path + ".bakiye", town.getBalance());
            yaml.set(path + ".ulus", town.getNationName());
            yaml.set(path + ".odenmemis-vergi", town.getUnpaidTaxCount());

            int i = 0;
            for (UUID member : town.getMembers()) {
                yaml.set(path + ".uyeler." + i + ".uuid", member.toString());
                yaml.set(path + ".uyeler." + i + ".rutbe", town.getRank(member));
                i++;
            }

            if (town.getSpawn() != null) {
                Location loc = town.getSpawn();
                yaml.set(path + ".spawn.world", loc.getWorld().getName());
                yaml.set(path + ".spawn.x", loc.getX());
                yaml.set(path + ".spawn.y", loc.getY());
                yaml.set(path + ".spawn.z", loc.getZ());
            }

            int ci = 0;
            for (var entry : town.getClaimedChunksByWorld().entrySet()) {
                for (long key : entry.getValue()) {
                    int cx = (int) (key >> 32);
                    int cz = (int) key;
                    yaml.set(path + ".chunklar." + ci + ".world", entry.getKey());
                    yaml.set(path + ".chunklar." + ci + ".x", cx);
                    yaml.set(path + ".chunklar." + ci + ".z", cz);
                    ci++;
                }
            }
        }

        // Uluslar
        for (Nation nation : plugin.getNationManager().getAllNations()) {
            String path = "uluslar." + nation.getName();
            yaml.set(path + ".baskent", nation.getCapitalTownName());
            yaml.set(path + ".bakiye", nation.getBalance());
            yaml.set(path + ".kasabalar", new java.util.ArrayList<>(nation.getTownNames()));
        }

        // Oyuncu bakiyeleri
        for (var entry : plugin.getEconomy().raw().entrySet()) {
            yaml.set("bakiyeler." + entry.getKey(), entry.getValue());
        }

        try {
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "data.yml kaydedilemedi!", e);
        }
    }

    public void load() {
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection townsSection = yaml.getConfigurationSection("kasabalar");
        if (townsSection != null) {
            for (String townName : townsSection.getKeys(false)) {
                String path = "kasabalar." + townName;
                UUID owner = UUID.fromString(yaml.getString(path + ".sahip"));
                Town town = new Town(townName, owner);
                town.deposit(yaml.getDouble(path + ".bakiye", 0.0));
                String nationName = yaml.getString(path + ".ulus", null);
                if (nationName != null) town.setNationName(nationName);

                ConfigurationSection membersSection = yaml.getConfigurationSection(path + ".uyeler");
                if (membersSection != null) {
                    for (String idx : membersSection.getKeys(false)) {
                        UUID uuid = UUID.fromString(yaml.getString(path + ".uyeler." + idx + ".uuid"));
                        String rank = yaml.getString(path + ".uyeler." + idx + ".rutbe", "Aday");
                        town.addMember(uuid);
                        town.setRank(uuid, rank);
                    }
                }

                if (yaml.contains(path + ".spawn.world")) {
                    String worldName = yaml.getString(path + ".spawn.world");
                    if (Bukkit.getWorld(worldName) != null) {
                        Location loc = new Location(Bukkit.getWorld(worldName),
                                yaml.getDouble(path + ".spawn.x"),
                                yaml.getDouble(path + ".spawn.y"),
                                yaml.getDouble(path + ".spawn.z"));
                        town.setSpawn(loc);
                    }
                }

                ConfigurationSection chunksSection = yaml.getConfigurationSection(path + ".chunklar");
                if (chunksSection != null) {
                    for (String idx : chunksSection.getKeys(false)) {
                        String world = yaml.getString(path + ".chunklar." + idx + ".world");
                        int x = yaml.getInt(path + ".chunklar." + idx + ".x");
                        int z = yaml.getInt(path + ".chunklar." + idx + ".z");
                        if (Bukkit.getWorld(world) != null) {
                            town.claimChunk(Bukkit.getWorld(world).getChunkAt(x, z));
                        }
                    }
                }

                plugin.getTownManager().registerLoadedTown(town);
            }
        }

        ConfigurationSection nationsSection = yaml.getConfigurationSection("uluslar");
        if (nationsSection != null) {
            for (String nationName : nationsSection.getKeys(false)) {
                String path = "uluslar." + nationName;
                String capital = yaml.getString(path + ".baskent");
                Nation nation = new Nation(nationName, capital);
                nation.deposit(yaml.getDouble(path + ".bakiye", 0.0));
                for (String t : yaml.getStringList(path + ".kasabalar")) {
                    nation.addTown(t);
                }
                plugin.getNationManager().registerLoadedNation(nation);
            }
        }

        ConfigurationSection balSection = yaml.getConfigurationSection("bakiyeler");
        if (balSection != null) {
            for (String uuidStr : balSection.getKeys(false)) {
                plugin.getEconomy().setBalance(UUID.fromString(uuidStr), yaml.getDouble("bakiyeler." + uuidStr));
            }
        }
    }
}
