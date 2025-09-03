package pl.htgmc.htgeconomyapi.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteDatabase implements DatabaseManager {
    private Connection connection;
    private final Logger logger = Logger.getLogger("HTGEconomyAPI");

    @Override
    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/HTGEconomyAPI/data.db");
            setupTables();
            logger.info("[SQLite] ✅ Połączono z lokalną bazą danych (data.db).");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[SQLite] ❌ Błąd połączenia z bazą danych!", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("[SQLite] 🔌 Połączenie z bazą danych zostało zamknięte.");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "[SQLite] ⚠️ Błąd przy zamykaniu połączenia!", e);
        }
    }

    @Override
    public void setupTables() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS coins (" +
                    "uuid TEXT PRIMARY KEY," +
                    "balance REAL NOT NULL DEFAULT 0" +
                    ")");
            logger.info("[SQLite] 📂 Tabela 'coins' została utworzona lub już istnieje.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[SQLite] ❌ Błąd przy tworzeniu tabeli 'coins'!", e);
        }
    }

    @Override
    public double getBalance(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT balance FROM coins WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                logger.fine("[SQLite] 🔎 getBalance(" + uuid + ") = " + balance);
                return balance;
            }
            logger.fine("[SQLite] 🔎 getBalance(" + uuid + ") = brak wpisu → 0.0");
            return 0.0;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "[SQLite] ❌ Błąd podczas pobierania balansu gracza: " + uuid, e);
            return 0.0;
        }
    }

    @Override
    public void setBalance(UUID uuid, double amount) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO coins (uuid, balance) VALUES (?, ?) " +
                        "ON CONFLICT(uuid) DO UPDATE SET balance=?")) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, amount);
            ps.setDouble(3, amount);
            int updated = ps.executeUpdate();
            logger.info("[SQLite] 💾 setBalance(" + uuid + ", " + amount + ") → zapisano (" + updated + " rekord).");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "[SQLite] ❌ Błąd podczas ustawiania balansu gracza: " + uuid, e);
        }
    }

    @Override
    public void addBalance(UUID uuid, double amount) {
        double oldBalance = getBalance(uuid);
        double newBalance = oldBalance + amount;
        setBalance(uuid, newBalance);
        logger.info("[SQLite] ➕ addBalance(" + uuid + ", " + amount + ") | stary=" + oldBalance + " → nowy=" + newBalance);
    }

    @Override
    public void removeBalance(UUID uuid, double amount) {
        double oldBalance = getBalance(uuid);
        double newBalance = oldBalance - amount;
        if (newBalance < 0) newBalance = 0.0;
        setBalance(uuid, newBalance);
        logger.info("[SQLite] ➖ removeBalance(" + uuid + ", " + amount + ") | stary=" + oldBalance + " → nowy=" + newBalance);
    }

    @Override
    public Map<UUID, Double> getAllBalances() {
        Map<UUID, Double> balances = new HashMap<>();
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT uuid, balance FROM coins");
            int count = 0;
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double balance = rs.getDouble("balance");
                    balances.put(uuid, balance);
                    count++;
                } catch (IllegalArgumentException ex) {
                    logger.warning("[SQLite] ⚠️ Błędny UUID w bazie: " + rs.getString("uuid"));
                }
            }
            logger.info("[SQLite] 📊 Załadowano wszystkie salda (" + count + " graczy).");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[SQLite] ❌ Błąd podczas pobierania wszystkich balansów!", e);
        }
        return balances;
    }
}
