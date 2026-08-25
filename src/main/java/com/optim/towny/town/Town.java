package com.optim.towny.town;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.*;

/**
 * Tek bir kasabayı temsil eder.
 */
public class Town {

    private final UUID id;
    private String name;
    private UUID owner;
    private double balance;
    private String nationName; // null olabilir

    private final Set<UUID> members = new LinkedHashSet<>();
    private final Map<UUID, String> ranks = new HashMap<>();
    private final Set<Long> claimedChunks = new HashSet<>(); // chunkKey (world hariç, basit sunucu için)
    private final Map<String, Set<Long>> claimedChunksByWorld = new HashMap<>();

    private Location spawn;
    private int unpaidTaxCount = 0;
    private boolean underAttack = false;
    private double captureProgress = 0.0;

    public Town(String name, UUID owner) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.owner = owner;
        this.balance = 0.0;
        this.members.add(owner);
        this.ranks.put(owner, "Kurucu");
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }
    public double getBalance() { return balance; }
    public void deposit(double amount) { this.balance += amount; }
    public boolean withdraw(double amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    public String getNationName() { return nationName; }
    public void setNationName(String nationName) { this.nationName = nationName; }

    public Set<UUID> getMembers() { return members; }
    public boolean addMember(UUID uuid) {
        boolean added = members.add(uuid);
        ranks.putIfAbsent(uuid, "Aday");
        return added;
    }
    public boolean removeMember(UUID uuid) {
        ranks.remove(uuid);
        return members.remove(uuid);
    }
    public boolean isMember(UUID uuid) { return members.contains(uuid); }

    public String getRank(UUID uuid) { return ranks.getOrDefault(uuid, "Aday"); }
    public void setRank(UUID uuid, String rank) { ranks.put(uuid, rank); }

    public Map<String, Set<Long>> getClaimedChunksByWorld() { return claimedChunksByWorld; }

    public boolean claimChunk(Chunk chunk) {
        String world = chunk.getWorld().getName();
        long key = chunkKey(chunk.getX(), chunk.getZ());
        return claimedChunksByWorld.computeIfAbsent(world, w -> new HashSet<>()).add(key);
    }

    public boolean unclaimChunk(Chunk chunk) {
        String world = chunk.getWorld().getName();
        long key = chunkKey(chunk.getX(), chunk.getZ());
        Set<Long> set = claimedChunksByWorld.get(world);
        if (set == null) return false;
        return set.remove(key);
    }

    public boolean isClaimed(Chunk chunk) {
        Set<Long> set = claimedChunksByWorld.get(chunk.getWorld().getName());
        if (set == null) return false;
        return set.contains(chunkKey(chunk.getX(), chunk.getZ()));
    }

    public int getTotalClaims() {
        int total = 0;
        for (Set<Long> s : claimedChunksByWorld.values()) total += s.size();
        return total;
    }

    public static long chunkKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xffffffffL);
    }

    public Location getSpawn() { return spawn; }
    public void setSpawn(Location spawn) { this.spawn = spawn; }

    public int getUnpaidTaxCount() { return unpaidTaxCount; }
    public void incrementUnpaidTax() { unpaidTaxCount++; }
    public void resetUnpaidTax() { unpaidTaxCount = 0; }

    public boolean isUnderAttack() { return underAttack; }
    public void setUnderAttack(boolean underAttack) { this.underAttack = underAttack; }

    public double getCaptureProgress() { return captureProgress; }
    public void setCaptureProgress(double captureProgress) { this.captureProgress = captureProgress; }
}
