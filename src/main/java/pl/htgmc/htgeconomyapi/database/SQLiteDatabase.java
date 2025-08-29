package pl.htgmc.htgeconomyapi.database;

import java.sql.*;
import java.util.UUID;

public class SQLiteDatabase implements DatabaseManager {
    private Connection connection;

    @Override
    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/HTGEconomyAPI/data.db");
            setupTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
    }

    @Override
    public void setupTables() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS coins (" +
                    "uuid TEXT PRIMARY KEY," +
                    "balance REAL NOT NULL DEFAULT 0" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getBalance(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT balance FROM coins WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
            return 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public void setBalance(UUID uuid, double amount) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO coins (uuid, balance) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET balance=?")) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, amount);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    @Override
    public void removeBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) - amount);
    }
}
