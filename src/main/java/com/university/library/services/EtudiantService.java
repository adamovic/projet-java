package com.university.library.services;

import com.university.library.dao.EtudiantDAO;
import com.university.library.models.Etudiant;

import java.sql.SQLException;
import java.util.List;

public class EtudiantService {

    private final EtudiantDAO etudiantDAO = new EtudiantDAO();

    public List<Etudiant> getAllEtudiants() {
        return etudiantDAO.findAll();
    }

    public void ajouterEtudiant(Etudiant etudiant) throws SQLException {
        if (etudiantDAO.exists(etudiant.getId())) {
            throw new IllegalStateException("Un étudiant avec cet identifiant existe déjà");
        }
        etudiantDAO.save(etudiant);
    }

    public void modifierEtudiant(Etudiant etudiant) throws SQLException {
        if (!etudiantDAO.exists(etudiant.getId())) {
            throw new IllegalStateException("Étudiant introuvable pour la mise à jour");
        }
        etudiantDAO.update(etudiant);
    }

    public void supprimerEtudiant(String id) {
        if (!etudiantDAO.exists(id)) {
            throw new IllegalStateException("Étudiant introuvable pour la suppression");
        }
        etudiantDAO.delete(id);
    }

    public Etudiant trouverParId(String id) {
        return etudiantDAO.findById(id);
    }

    public boolean existe(String id) {
        return etudiantDAO.exists(id);
    }
}
