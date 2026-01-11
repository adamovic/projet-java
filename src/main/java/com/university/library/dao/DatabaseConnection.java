package com.university.library.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    
    private DatabaseConnection() {
        loadProperties();
        connect();
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                // Valeurs par défaut si le fichier n'existe pas
                url = "jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC";
                username = "root";
                password = "";
                return;
            }
            
            Properties prop = new Properties();
            prop.load(input);
            
            url = prop.getProperty("db.url", "jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC");
            username = prop.getProperty("db.username", "root");
            password = prop.getProperty("db.password", "");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de database.properties: " + e.getMessage());
            // Valeurs par défaut
            url = "jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC";
            username = "root";
            password = "";
        }
    }
    
    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Connexion à la base de données MySQL réussie!");
            System.out.println("  Base de données: " + connection.getCatalog());
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL non trouvé: " + e.getMessage());
            System.err.println("  Vérifiez que la dépendance mysql-connector-j est dans pom.xml");
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à la base de données: " + e.getMessage());
            System.err.println("  URL: " + url);
            System.err.println("  Vérifiez que:");
            System.err.println("    1. MySQL est démarré dans XAMPP");
            System.err.println("    2. La base de données 'bibliotheque_db' existe");
            System.err.println("    3. Les paramètres dans database.properties sont corrects");
        }
    }
    
    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion: " + e.getMessage());
            connect();
        }
        
        if (connection == null) {
            throw new SQLException("Impossible d'établir une connexion à la base de données MySQL. " +
                    "Vérifiez que MySQL est démarré dans XAMPP et que la base de données 'bibliotheque_db' existe.");
        }
        
        return connection;
    }
    
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}

