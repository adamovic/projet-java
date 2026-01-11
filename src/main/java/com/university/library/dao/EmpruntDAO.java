package com.university.library.dao;

import com.university.library.models.Emprunt;
import com.university.library.models.Etudiant;
import com.university.library.models.Livre;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {
    private final DatabaseConnection dbConnection;
    private final EtudiantDAO etudiantDAO;
    private final LivreDAO livreDAO;
    
    public EmpruntDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.etudiantDAO = new EtudiantDAO();
        this.livreDAO = new LivreDAO();
    }
    
    public List<Emprunt> findAll() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour " +
                     "FROM emprunts ORDER BY date_emprunt DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Etudiant etudiant = etudiantDAO.findById(rs.getString("id_etudiant"));
                Livre livre = livreDAO.findByIsbn(rs.getString("isbn_livre"));
                
                if (etudiant != null && livre != null) {
                    Emprunt emprunt = new Emprunt(
                        rs.getString("id"),
                        etudiant,
                        livre
                    );
                    
                    // Définir les dates
                    Date dateEmprunt = rs.getDate("date_emprunt");
                    if (dateEmprunt != null) {
                        emprunt.setDateEmprunt(new java.util.Date(dateEmprunt.getTime()));
                    }
                    
                    Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                    if (dateRetourPrevue != null) {
                        emprunt.setDateRetourPrevue(new java.util.Date(dateRetourPrevue.getTime()));
                    }
                    
                    Date dateRetour = rs.getDate("date_retour");
                    if (dateRetour != null) {
                        emprunt.setDateRetour(new java.util.Date(dateRetour.getTime()));
                    }
                    
                    emprunts.add(emprunt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public Emprunt findById(String id) {
        String sql = "SELECT id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour " +
                     "FROM emprunts WHERE id = ?";
        Emprunt emprunt = null;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Etudiant etudiant = etudiantDAO.findById(rs.getString("id_etudiant"));
                    Livre livre = livreDAO.findByIsbn(rs.getString("isbn_livre"));
                    
                    if (etudiant != null && livre != null) {
                        emprunt = new Emprunt(
                            rs.getString("id"),
                            etudiant,
                            livre
                        );
                        
                        Date dateEmprunt = rs.getDate("date_emprunt");
                        if (dateEmprunt != null) {
                            emprunt.setDateEmprunt(new java.util.Date(dateEmprunt.getTime()));
                        }
                        
                        Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                        if (dateRetourPrevue != null) {
                            emprunt.setDateRetourPrevue(new java.util.Date(dateRetourPrevue.getTime()));
                        }
                        
                        Date dateRetour = rs.getDate("date_retour");
                        if (dateRetour != null) {
                            emprunt.setDateRetour(new java.util.Date(dateRetour.getTime()));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'emprunt: " + e.getMessage());
        }
        
        return emprunt;
    }
    
    public void save(Emprunt emprunt) {
        String sql = "INSERT INTO emprunts (id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, emprunt.getId());
            stmt.setString(2, emprunt.getEtudiant().getId());
            stmt.setString(3, emprunt.getLivre().getIsbn());
            stmt.setDate(4, new Date(emprunt.getDateEmprunt().getTime()));
            stmt.setDate(5, new Date(emprunt.getDateRetourPrevue().getTime()));
            
            if (emprunt.getDateRetour() != null) {
                stmt.setDate(6, new Date(emprunt.getDateRetour().getTime()));
            } else {
                stmt.setDate(6, null);
            }
            
            stmt.executeUpdate();
            
            // Mettre à jour le nombre d'exemplaires disponibles
            Livre livre = emprunt.getLivre();
            livre.emprunterExemplaire();
            livreDAO.update(livre);
            
            System.out.println("Emprunt ajouté avec succès: " + emprunt.getId());
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'emprunt: " + e.getMessage());
        }
    }
    
    public void update(Emprunt emprunt) {
        String sql = "UPDATE emprunts SET id_etudiant = ?, isbn_livre = ?, date_emprunt = ?, " +
                     "date_retour_prevue = ?, date_retour = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, emprunt.getEtudiant().getId());
            stmt.setString(2, emprunt.getLivre().getIsbn());
            stmt.setDate(3, new Date(emprunt.getDateEmprunt().getTime()));
            stmt.setDate(4, new Date(emprunt.getDateRetourPrevue().getTime()));
            
            if (emprunt.getDateRetour() != null) {
                stmt.setDate(5, new Date(emprunt.getDateRetour().getTime()));
            } else {
                stmt.setDate(5, null);
            }
            
            stmt.setString(6, emprunt.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Emprunt mis à jour avec succès: " + emprunt.getId());
            } else {
                System.out.println("Aucun emprunt trouvé avec l'ID: " + emprunt.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'emprunt: " + e.getMessage());
        }
    }
    
    public void delete(String id) {
        // Récupérer l'emprunt avant suppression pour mettre à jour le livre
        Emprunt emprunt = findById(id);
        
        String sql = "DELETE FROM emprunts WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Remettre le livre disponible si l'emprunt était actif
                if (emprunt != null && emprunt.estActif()) {
                    Livre livre = emprunt.getLivre();
                    livre.retournerExemplaire();
                    livreDAO.update(livre);
                }
                System.out.println("Emprunt supprimé avec succès: " + id);
            } else {
                System.out.println("Aucun emprunt trouvé avec l'ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'emprunt: " + e.getMessage());
        }
    }
    
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM emprunts WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'existence de l'emprunt: " + e.getMessage());
        }
        
        return false;
    }
    
    public List<Emprunt> findByEtudiant(String idEtudiant) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour " +
                     "FROM emprunts WHERE id_etudiant = ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idEtudiant);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Etudiant etudiant = etudiantDAO.findById(rs.getString("id_etudiant"));
                    Livre livre = livreDAO.findByIsbn(rs.getString("isbn_livre"));
                    
                    if (etudiant != null && livre != null) {
                        Emprunt emprunt = new Emprunt(
                            rs.getString("id"),
                            etudiant,
                            livre
                        );
                        
                        Date dateEmprunt = rs.getDate("date_emprunt");
                        if (dateEmprunt != null) {
                            emprunt.setDateEmprunt(new java.util.Date(dateEmprunt.getTime()));
                        }
                        
                        Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                        if (dateRetourPrevue != null) {
                            emprunt.setDateRetourPrevue(new java.util.Date(dateRetourPrevue.getTime()));
                        }
                        
                        Date dateRetour = rs.getDate("date_retour");
                        if (dateRetour != null) {
                            emprunt.setDateRetour(new java.util.Date(dateRetour.getTime()));
                        }
                        
                        emprunts.add(emprunt);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par étudiant: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public List<Emprunt> findByLivre(String isbnLivre) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour " +
                     "FROM emprunts WHERE isbn_livre = ? ORDER BY date_emprunt DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbnLivre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Etudiant etudiant = etudiantDAO.findById(rs.getString("id_etudiant"));
                    Livre livre = livreDAO.findByIsbn(rs.getString("isbn_livre"));
                    
                    if (etudiant != null && livre != null) {
                        Emprunt emprunt = new Emprunt(
                            rs.getString("id"),
                            etudiant,
                            livre
                        );
                        
                        Date dateEmprunt = rs.getDate("date_emprunt");
                        if (dateEmprunt != null) {
                            emprunt.setDateEmprunt(new java.util.Date(dateEmprunt.getTime()));
                        }
                        
                        Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                        if (dateRetourPrevue != null) {
                            emprunt.setDateRetourPrevue(new java.util.Date(dateRetourPrevue.getTime()));
                        }
                        
                        Date dateRetour = rs.getDate("date_retour");
                        if (dateRetour != null) {
                            emprunt.setDateRetour(new java.util.Date(dateRetour.getTime()));
                        }
                        
                        emprunts.add(emprunt);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par livre: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public List<Emprunt> findActifs() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour " +
                     "FROM emprunts WHERE date_retour IS NULL ORDER BY date_emprunt DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Etudiant etudiant = etudiantDAO.findById(rs.getString("id_etudiant"));
                Livre livre = livreDAO.findByIsbn(rs.getString("isbn_livre"));
                
                if (etudiant != null && livre != null) {
                    Emprunt emprunt = new Emprunt(
                        rs.getString("id"),
                        etudiant,
                        livre
                    );
                    
                    Date dateEmprunt = rs.getDate("date_emprunt");
                    if (dateEmprunt != null) {
                        emprunt.setDateEmprunt(new java.util.Date(dateEmprunt.getTime()));
                    }
                    
                    Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                    if (dateRetourPrevue != null) {
                        emprunt.setDateRetourPrevue(new java.util.Date(dateRetourPrevue.getTime()));
                    }
                    
                    emprunts.add(emprunt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des emprunts actifs: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public List<Emprunt> findEnRetard() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour " +
                     "FROM emprunts WHERE date_retour IS NULL AND date_retour_prevue < CURDATE() " +
                     "ORDER BY date_retour_prevue ASC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Etudiant etudiant = etudiantDAO.findById(rs.getString("id_etudiant"));
                Livre livre = livreDAO.findByIsbn(rs.getString("isbn_livre"));
                
                if (etudiant != null && livre != null) {
                    Emprunt emprunt = new Emprunt(
                        rs.getString("id"),
                        etudiant,
                        livre
                    );
                    
                    Date dateEmprunt = rs.getDate("date_emprunt");
                    if (dateEmprunt != null) {
                        emprunt.setDateEmprunt(new java.util.Date(dateEmprunt.getTime()));
                    }
                    
                    Date dateRetourPrevue = rs.getDate("date_retour_prevue");
                    if (dateRetourPrevue != null) {
                        emprunt.setDateRetourPrevue(new java.util.Date(dateRetourPrevue.getTime()));
                    }
                    
                    emprunts.add(emprunt);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des emprunts en retard: " + e.getMessage());
        }
        
        return emprunts;
    }
    
    public String generateNextId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(id, 4) AS UNSIGNED)) FROM emprunts WHERE id LIKE 'EMP%'";
        int maxId = 0;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next() && rs.getObject(1) != null) {
                maxId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la génération de l'ID: " + e.getMessage());
        }
        
        return "EMP" + String.format("%03d", maxId + 1);
    }
}
