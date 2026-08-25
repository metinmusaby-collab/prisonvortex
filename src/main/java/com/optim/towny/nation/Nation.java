package com.optim.towny.nation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Nation {

    private String name;
    private String capitalTownName;
    private double balance;
    private final Set<String> townNames = new LinkedHashSet<>();

    public Nation(String name, String capitalTownName) {
        this.name = name;
        this.capitalTownName = capitalTownName;
        this.townNames.add(capitalTownName);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCapitalTownName() { return capitalTownName; }
    public void setCapitalTownName(String capitalTownName) { this.capitalTownName = capitalTownName; }

    public double getBalance() { return balance; }
    public void deposit(double amount) { balance += amount; }
    public boolean withdraw(double amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    public Set<String> getTownNames() { return townNames; }
    public boolean addTown(String townName) { return townNames.add(townName); }
    public boolean removeTown(String townName) { return townNames.remove(townName); }
}
