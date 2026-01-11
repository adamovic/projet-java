package com.university.library.dao;

import com.university.library.models.Livre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {
    private final DatabaseConnection dbConnection;
    
    public LivreDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Livre> findAll() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT isbn, titre, auteur, categorie, annee_publication, " +
                     "nombre_exemplaires_total, nombre_exemplaires_disponibles " +
                     "FROM livres ORDER BY titre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Livre livre = new Livre(
                    rs.getString("isbn"),
                    rs.getString("titre"),
                    rs.getString("auteur"),
                    rs.getString("categorie"),
                    rs.getInt("annee_publication"),
                    rs.getInt("nombre_exemplaires_total")
                );
                
                // Ajuster le nombre d'exemplaires disponibles
                int disponibles = rs.getInt("nombre_exemplaires_disponibles");
                int empruntes = livre.getNombreExemplairesTotal() - disponibles;
                for (int i = 0; i < empruntes; i++) {
                    livre.emprunterExemplaire();
                }
                
                livres.add(livre);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livres: " + e.getMessage());
        }
        
        return livres;
    }
    
    public Livre findByIsbn(String isbn) {
        String sql = "SELECT isbn, titre, auteur, categorie, annee_publication, " +
                     "nombre_exemplaires_total, nombre_exemplaires_disponibles " +
                     "FROM livres WHERE isbn = ?";
        Livre livre = null;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    livre = new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("annee_publication"),
                        rs.getInt("nombre_exemplaires_total")
                    );
                    
                    // Ajuster le nombre d'exemplaires disponibles
                    int disponibles = rs.getInt("nombre_exemplaires_disponibles");
                    int empruntes = livre.getNombreExemplairesTotal() - disponibles;
                    for (int i = 0; i < empruntes; i++) {
                        livre.emprunterExemplaire();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du livre: " + e.getMessage());
        }
        
        return livre;
    }
    
    public void save(Livre livre) throws SQLException {
        String sql = "INSERT INTO livres (isbn, titre, auteur, categorie, annee_publication, " +
                     "nombre_exemplaires_total, nombre_exemplaires_disponibles) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = dbConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Connexion à la base de données impossible. Vérifiez que MySQL est démarré.");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setString(3, livre.getAuteur());
            stmt.setString(4, livre.getCategorie());
            stmt.setInt(5, livre.getAnneePublication());
            stmt.setInt(6, livre.getNombreExemplairesTotal());
            stmt.setInt(7, livre.getNombreExemplairesDisponibles());
            
            stmt.executeUpdate();
            System.out.println("Livre ajouté avec succès: " + livre.getIsbn());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du livre: " + e.getMessage());
        }
    }
    
    public void update(Livre livre) {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, categorie = ?, " +
                     "annee_publication = ?, nombre_exemplaires_total = ?, " +
                     "nombre_exemplaires_disponibles = ? WHERE isbn = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setString(3, livre.getCategorie());
            stmt.setInt(4, livre.getAnneePublication());
            stmt.setInt(5, livre.getNombreExemplairesTotal());
            stmt.setInt(6, livre.getNombreExemplairesDisponibles());
            stmt.setString(7, livre.getIsbn());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Livre mis à jour avec succès: " + livre.getIsbn());
            } else {
                System.out.println("Aucun livre trouvé avec l'ISBN: " + livre.getIsbn());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du livre: " + e.getMessage());
        }
    }
    
    public void delete(String isbn) {
        String sql = "DELETE FROM livres WHERE isbn = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Livre supprimé avec succès: " + isbn);
            } else {
                System.out.println("Aucun livre trouvé avec l'ISBN: " + isbn);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du livre: " + e.getMessage());
        }
    }
    
    public boolean exists(String isbn) {
        String sql = "SELECT COUNT(*) FROM livres WHERE isbn = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence du livre: " + e.getMessage());
        }
        
        return false;
    }
    
    public List<Livre> findByCategorie(String categorie) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT isbn, titre, auteur, categorie, annee_publication, " +
                     "nombre_exemplaires_total, nombre_exemplaires_disponibles " +
                     "FROM livres WHERE categorie = ? ORDER BY titre";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Livre livre = new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("annee_publication"),
                        rs.getInt("nombre_exemplaires_total")
                    );
                    
                    int disponibles = rs.getInt("nombre_exemplaires_disponibles");
                    int empruntes = livre.getNombreExemplairesTotal() - disponibles;
                    for (int i = 0; i < empruntes; i++) {
                        livre.emprunterExemplaire();
                    }
                    
                    livres.add(livre);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par catégorie: " + e.getMessage());
        }
        
        return livres;
    }
    
    public List<Livre> searchByTitre(String titre) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT isbn, titre, auteur, categorie, annee_publication, " +
                     "nombre_exemplaires_total, nombre_exemplaires_disponibles " +
                     "FROM livres WHERE titre LIKE ? ORDER BY titre";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            if (conn == null) {
                return livres;
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + titre + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Livre livre = new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("annee_publication"),
                        rs.getInt("nombre_exemplaires_total")
                    );
                    
                    int disponibles = rs.getInt("nombre_exemplaires_disponibles");
                    int empruntes = livre.getNombreExemplairesTotal() - disponibles;
                    for (int i = 0; i < empruntes; i++) {
                        livre.emprunterExemplaire();
                    }
                    
                    livres.add(livre);
                }
            }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'exécution de la requête: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion lors de la recherche par titre: " + e.getMessage());
        }
        
        return livres;
    }
    
    public List<Livre> searchByAuteur(String auteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT isbn, titre, auteur, categorie, annee_publication, " +
                     "nombre_exemplaires_total, nombre_exemplaires_disponibles " +
                     "FROM livres WHERE auteur LIKE ? ORDER BY titre";
        
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            if (conn == null) {
                return livres;
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + auteur + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Livre livre = new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("annee_publication"),
                        rs.getInt("nombre_exemplaires_total")
                    );
                    
                    int disponibles = rs.getInt("nombre_exemplaires_disponibles");
                    int empruntes = livre.getNombreExemplairesTotal() - disponibles;
                    for (int i = 0; i < empruntes; i++) {
                        livre.emprunterExemplaire();
                    }
                    
                    livres.add(livre);
                }
            }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'exécution de la requête: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion lors de la recherche par auteur: " + e.getMessage());
        }
        
        return livres;
    }
}
