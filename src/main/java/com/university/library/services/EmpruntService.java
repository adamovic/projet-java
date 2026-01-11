package com.university.library.services;

import com.university.library.dao.EmpruntDAO;
import com.university.library.dao.EtudiantDAO;
import com.university.library.dao.LivreDAO;
import com.university.library.models.Emprunt;
import com.university.library.models.Etudiant;
import com.university.library.models.Livre;

import java.util.List;

public class EmpruntService {

    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final EtudiantDAO etudiantDAO = new EtudiantDAO();
    private final LivreDAO livreDAO = new LivreDAO();

    /**
     * Récupère tous les emprunts
     */
    public List<Emprunt> getAllEmprunts() {
        return empruntDAO.findAll();
    }

    /**
     * Récupère un emprunt par son ID
     */
    public Emprunt trouverParId(String id) {
        return empruntDAO.findById(id);
    }

    /**
     * Crée un nouvel emprunt avec validation métier
     */
    public void creerEmprunt(Emprunt emprunt) {
        // Vérifier que l'étudiant existe
        if (!etudiantDAO.exists(emprunt.getEtudiant().getId())) {
            throw new IllegalStateException("L'étudiant n'existe pas");
        }

        // Vérifier que le livre existe
        if (!livreDAO.exists(emprunt.getLivre().getIsbn())) {
            throw new IllegalStateException("Le livre n'existe pas");
        }

        // Vérifier que le livre est disponible
        Livre livre = livreDAO.findByIsbn(emprunt.getLivre().getIsbn());
        if (!livre.estDisponible()) {
            throw new IllegalStateException("Le livre n'est pas disponible");
        }

        // Vérifier que l'étudiant n'a pas trop d'emprunts actifs
        Etudiant etudiant = etudiantDAO.findById(emprunt.getEtudiant().getId());
        List<Emprunt> empruntsActifs = empruntDAO.findByEtudiant(etudiant.getId());
        long nombreEmpruntsActifs = empruntsActifs.stream()
                .filter(Emprunt::estActif)
                .count();

        if (nombreEmpruntsActifs >= 5) {
            throw new IllegalStateException("L'étudiant a déjà atteint le nombre maximum d'emprunts (5)");
        }

        // Vérifier que l'étudiant n'a pas d'emprunts en retard
        boolean aDesRetards = empruntsActifs.stream()
                .anyMatch(Emprunt::estEnRetard);

        if (aDesRetards) {
            throw new IllegalStateException("L'étudiant a des emprunts en retard. Veuillez les retourner d'abord");
        }

        // Générer un ID si nécessaire
        if (emprunt.getId() == null || emprunt.getId().isEmpty()) {
            emprunt = new Emprunt(empruntDAO.generateNextId(), emprunt.getEtudiant(), emprunt.getLivre());
        }

        // Sauvegarder l'emprunt
        empruntDAO.save(emprunt);
    }

    /**
     * Met à jour un emprunt existant
     */
    public void modifierEmprunt(Emprunt emprunt) {
        if (!empruntDAO.exists(emprunt.getId())) {
            throw new IllegalStateException("Emprunt introuvable pour la mise à jour");
        }
        empruntDAO.update(emprunt);
    }

    /**
     * Supprime un emprunt
     */
    public void supprimerEmprunt(String id) {
        if (!empruntDAO.exists(id)) {
            throw new IllegalStateException("Emprunt introuvable pour la suppression");
        }
        empruntDAO.delete(id);
    }

    /**
     * Retourne un livre emprunté
     */
    public void retournerLivre(String idEmprunt) {
        Emprunt emprunt = empruntDAO.findById(idEmprunt);
        
        if (emprunt == null) {
            throw new IllegalStateException("Emprunt introuvable");
        }

        if (!emprunt.estActif()) {
            throw new IllegalStateException("Cet emprunt a déjà été retourné");
        }

        // Marquer comme retourné
        emprunt.retourner();
        
        // Mettre à jour dans la base de données
        empruntDAO.update(emprunt);
    }

    /**
     * Récupère tous les emprunts d'un étudiant
     */
    public List<Emprunt> getEmpruntsParEtudiant(String idEtudiant) {
        if (!etudiantDAO.exists(idEtudiant)) {
            throw new IllegalStateException("Étudiant introuvable");
        }
        return empruntDAO.findByEtudiant(idEtudiant);
    }

    /**
     * Récupère tous les emprunts d'un livre
     */
    public List<Emprunt> getEmpruntsParLivre(String isbn) {
        if (!livreDAO.exists(isbn)) {
            throw new IllegalStateException("Livre introuvable");
        }
        return empruntDAO.findByLivre(isbn);
    }

    /**
     * Récupère tous les emprunts actifs
     */
    public List<Emprunt> getEmpruntsActifs() {
        return empruntDAO.findActifs();
    }

    /**
     * Récupère tous les emprunts en retard
     */
    public List<Emprunt> getEmpruntsEnRetard() {
        return empruntDAO.findEnRetard();
    }

    /**
     * Vérifie si un emprunt existe
     */
    public boolean existe(String id) {
        return empruntDAO.exists(id);
    }

    /**
     * Génère un nouvel ID pour un emprunt
     */
    public String genererNouvelId() {
        return empruntDAO.generateNextId();
    }

    /**
     * Récupère le nombre d'emprunts actifs d'un étudiant
     */
    public int getNombreEmpruntsActifs(String idEtudiant) {
        List<Emprunt> emprunts = getEmpruntsParEtudiant(idEtudiant);
        return (int) emprunts.stream()
                .filter(Emprunt::estActif)
                .count();
    }

    /**
     * Vérifie si un étudiant a des emprunts en retard
     */
    public boolean aDesEmpruntsEnRetard(String idEtudiant) {
        List<Emprunt> emprunts = getEmpruntsParEtudiant(idEtudiant);
        return emprunts.stream()
                .anyMatch(Emprunt::estEnRetard);
    }
}

