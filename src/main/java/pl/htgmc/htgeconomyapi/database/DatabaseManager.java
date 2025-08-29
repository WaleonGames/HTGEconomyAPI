package pl.htgmc.htgeconomyapi.database;

import java.util.UUID;

public interface DatabaseManager {
    void connect();
    void disconnect();
    void setupTables();

    double getBalance(UUID uuid);
    void setBalance(UUID uuid, double amount);
    void addBalance(UUID uuid, double amount);
    void removeBalance(UUID uuid, double amount);
}
