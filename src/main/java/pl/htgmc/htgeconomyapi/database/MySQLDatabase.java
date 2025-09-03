package pl.htgmc.htgeconomyapi.database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLDatabase implements DatabaseManager {
    private Connection connection;
    private final String host, database, username, password;
    private final int port;
    private final Logger logger = Logger.getLogger("HTGEconomyAPI");

    public MySQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;

            // 👉 używamy mariadb:// ale nadal działa z MySQL
            String url = "jdbc:mariadb://" + host + ":" + port + "/" + database +
                    "?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true";

            connection = DriverManager.getConnection(url, username, password);
            setupTables();
            logger.info("Połączono z bazą danych: " + database + "@" + host + ":" + port);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Błąd połączenia z bazą danych!", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Połączenie z bazą danych zostało zamknięte.");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Błąd przy zamykaniu połączenia z bazą danych!", e);
        }
    }

    @Override
    public void setupTables() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS coins (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "balance DOUBLE NOT NULL DEFAULT 0" +
                    ")");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Błąd przy tworzeniu tabeli 'coins'!", e);
        }
    }

    @Override
    public double getBalance(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT balance FROM coins WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Błąd podczas pobierania balansu gracza: " + uuid, e);
        }
        return 0.0;
    }

    @Override
    public Map<UUID, Double> getAllBalances() {
        Map<UUID, Double> balances = new HashMap<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT uuid, balance FROM coins")) {
            int count = 0;
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double balance = rs.getDouble("balance");
                    balances.put(uuid, balance);
                    count++;
                } catch (IllegalArgumentException ex) {
                    logger.warning("[MySQL] ⚠️ Błędny UUID w bazie: " + rs.getString("uuid"));
                }
            }
            logger.info("[MySQL] 📊 Załadowano wszystkie salda (" + count + " graczy).");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "[MySQL] ❌ Błąd podczas pobierania wszystkich balansów!", e);
        }
        return balances;
    }

    @Override
    public void setBalance(UUID uuid, double amount) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO coins (uuid, balance) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE balance=?")) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, amount);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Błąd podczas ustawiania balansu gracza: " + uuid, e);
        }
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
