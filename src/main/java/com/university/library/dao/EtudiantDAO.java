package com.university.library.dao;

import com.university.library.models.Etudiant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EtudiantDAO {
    private final DatabaseConnection dbConnection;
    
    public EtudiantDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, telephone FROM etudiants ORDER BY nom, prenom";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            if (conn == null) {
                System.err.println("Erreur: Connexion à la base de données impossible");
                return etudiants;
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Etudiant etudiant = new Etudiant(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getString("telephone")
                );
                etudiants.add(etudiant);
            }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'exécution de la requête: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            System.err.println("Vérifiez que MySQL est démarré dans XAMPP et que la base 'bibliotheque_db' existe");
        }
        
        return etudiants;
    }
    
    public Etudiant findById(String id) {
        String sql = "SELECT id, nom, prenom, email, telephone FROM etudiants WHERE id = ?";
        Etudiant etudiant = null;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    etudiant = new Etudiant(
                        rs.getString("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'étudiant: " + e.getMessage());
        }
        
        return etudiant;
    }
    
    public void save(Etudiant etudiant) throws SQLException {
        String sql = "INSERT INTO etudiants (id, nom, prenom, email, telephone) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = dbConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Connexion à la base de données impossible. Vérifiez que MySQL est démarré.");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, etudiant.getId());
            stmt.setString(2, etudiant.getNom());
            stmt.setString(3, etudiant.getPrenom());
            stmt.setString(4, etudiant.getEmail());
            stmt.setString(5, etudiant.getTelephone());
            
            stmt.executeUpdate();
            System.out.println("Étudiant ajouté avec succès: " + etudiant.getId());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'étudiant: " + e.getMessage());
            throw e; // Propager l'exception pour que le contrôleur puisse l'afficher
        }
    }
    
    public void update(Etudiant etudiant) {
        String sql = "UPDATE etudiants SET nom = ?, prenom = ?, email = ?, telephone = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, etudiant.getNom());
            stmt.setString(2, etudiant.getPrenom());
            stmt.setString(3, etudiant.getEmail());
            stmt.setString(4, etudiant.getTelephone());
            stmt.setString(5, etudiant.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Étudiant mis à jour avec succès: " + etudiant.getId());
            } else {
                System.out.println("Aucun étudiant trouvé avec l'ID: " + etudiant.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'étudiant: " + e.getMessage());
        }
    }
    
    public void delete(String id) {
        String sql = "DELETE FROM etudiants WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Étudiant supprimé avec succès: " + id);
            } else {
                System.out.println("Aucun étudiant trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'étudiant: " + e.getMessage());
        }
    }
    
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM etudiants WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'étudiant: " + e.getMessage());
        }
        
        return false;
    }
}
