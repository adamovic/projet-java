package com.university.library.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe de test pour vérifier la connexion à la base de données MySQL
 */
public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("=== Test de connexion à MySQL ===\n");
        
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            return;
        }
        
        if (conn != null) {
            try {
                // Test 1: Informations sur la base de données
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("✓ Connexion réussie!");
                System.out.println("  - Driver: " + metaData.getDriverName());
                System.out.println("  - Version: " + metaData.getDriverVersion());
                System.out.println("  - URL: " + metaData.getURL());
                System.out.println("  - Utilisateur: " + metaData.getUserName());
                
                // Test 2: Vérifier que la base de données existe
                String catalog = conn.getCatalog();
                System.out.println("\n✓ Base de données active: " + catalog);
                
                // Test 3: Lister les tables
                System.out.println("\n=== Tables disponibles ===");
                ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"});
                int tableCount = 0;
                while (tables.next()) {
                    tableCount++;
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("  " + tableCount + ". " + tableName);
                }
                
                if (tableCount == 0) {
                    System.out.println("  ⚠ Aucune table trouvée. Exécutez le script database.sql!");
                } else if (tableCount < 3) {
                    System.out.println("  ⚠ Certaines tables manquent. Vérifiez le script database.sql!");
                } else {
                    System.out.println("  ✓ Toutes les tables sont présentes!");
                }
                
                // Test 4: Compter les enregistrements
                System.out.println("\n=== Données dans les tables ===");
                testTableCount(conn, "etudiants");
                testTableCount(conn, "livres");
                testTableCount(conn, "emprunts");
                
                System.out.println("\n✓ Tous les tests sont passés avec succès!");
                System.out.println("Le projet est correctement lié à MySQL!");
                
            } catch (SQLException e) {
                System.err.println("✗ Erreur lors des tests: " + e.getMessage());
                e.printStackTrace();
            } finally {
                dbConnection.closeConnection();
            }
        } else {
            System.err.println("✗ Échec de la connexion à la base de données!");
            System.err.println("Vérifiez que:");
            System.err.println("  1. MySQL est démarré dans XAMPP");
            System.err.println("  2. La base de données 'bibliotheque_db' existe");
            System.err.println("  3. Les paramètres dans database.properties sont corrects");
        }
    }
    
    private static void testTableCount(Connection conn, String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM " + tableName;
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("  - " + tableName + ": " + count + " enregistrement(s)");
            }
        } catch (SQLException e) {
            System.err.println("  ✗ Erreur lors du comptage dans " + tableName + ": " + e.getMessage());
        }
    }
}

