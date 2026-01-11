package com.university.library;

import com.university.library.models.Etudiant;
import com.university.library.models.Livre;
import com.university.library.models.Emprunt;
import com.university.library.dao.EtudiantDAO;
import com.university.library.dao.LivreDAO;
import com.university.library.dao.EmpruntDAO;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static EtudiantDAO etudiantDAO = new EtudiantDAO();
    private static LivreDAO livreDAO = new LivreDAO();
    private static EmpruntDAO empruntDAO = new EmpruntDAO();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("  GESTION DE BIBLIOTHEQUE UNIVERSITAIRE");
        System.out.println("=====================================");
        
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            int choix = lireEntier("Votre choix: ");
            
            switch (choix) {
                case 1:
                    gererEtudiants();
                    break;
                case 2:
                    gererLivres();
                    break;
                case 3:
                    gererEmprunts();
                    break;
                case 4:
                    rechercherLivres();
                    break;
                case 5:
                    afficherStatistiques();
                    break;
                case 0:
                    continuer = false;
                    System.out.println("Au revoir!");
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
        scanner.close();
    }
    
    private static void afficherMenu() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Gérer les étudiants");
        System.out.println("2. Gérer les livres");
        System.out.println("3. Gérer les emprunts");
        System.out.println("4. Rechercher des livres");
        System.out.println("5. Afficher les statistiques");
        System.out.println("0. Quitter");
    }
    
    private static void gererEtudiants() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- GESTION DES ETUDIANTS ---");
            System.out.println("1. Lister les étudiants");
            System.out.println("2. Ajouter un étudiant");
            System.out.println("3. Modifier un étudiant");
            System.out.println("4. Supprimer un étudiant");
            System.out.println("0. Retour");
            
            int choix = lireEntier("Votre choix: ");
            
            switch (choix) {
                case 1:
                    listerEtudiants();
                    break;
                case 2:
                    ajouterEtudiant();
                    break;
                case 3:
                    modifierEtudiant();
                    break;
                case 4:
                    supprimerEtudiant();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }
    
    private static void listerEtudiants() {
        System.out.println("\n--- LISTE DES ETUDIANTS ---");
        List<Etudiant> etudiants = etudiantDAO.findAll();
        
        if (etudiants.isEmpty()) {
            System.out.println("Aucun étudiant enregistré.");
        } else {
            System.out.printf("%-8s %-20s %-20s %-30s %-15s %-10s\n", 
                "ID", "Nom", "Prénom", "Email", "Téléphone", "Emprunts");
            System.out.println("----------------------------------------------------------------------------------------");
            
            for (Etudiant e : etudiants) {
                System.out.printf("%-8s %-20s %-20s %-30s %-15s %-10d\n",
                    e.getId(), e.getNom(), e.getPrenom(), e.getEmail(), 
                    e.getTelephone(), e.getNombreEmpruntsActifs());
            }
        }
    }
    
    private static void ajouterEtudiant() {
        System.out.println("\n--- AJOUTER UN ETUDIANT ---");
        String id = lireTexte("ID étudiant: ");
        
        if (etudiantDAO.exists(id)) {
            System.out.println("Cet ID existe déjà!");
            return;
        }
        
        String nom = lireTexte("Nom: ");
        String prenom = lireTexte("Prénom: ");
        String email = lireTexte("Email: ");
        String telephone = lireTexte("Téléphone: ");
        
        Etudiant etudiant = new Etudiant(id, nom, prenom, email, telephone);
        try {
            etudiantDAO.save(etudiant);
            System.out.println("Étudiant ajouté avec succès!");
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    private static void modifierEtudiant() {
        System.out.println("\n--- MODIFIER UN ETUDIANT ---");
        String id = lireTexte("ID de l'étudiant à modifier: ");
        
        Etudiant etudiant = etudiantDAO.findById(id);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé!");
            return;
        }
        
        System.out.println("Étudiant actuel: " + etudiant.getNomComplet());
        String nom = lireTexte("Nouveau nom (laisser vide pour conserver): ");
        String prenom = lireTexte("Nouveau prénom (laisser vide pour conserver): ");
        String email = lireTexte("Nouvel email (laisser vide pour conserver): ");
        String telephone = lireTexte("Nouveau téléphone (laisser vide pour conserver): ");
        
        if (!nom.isEmpty()) etudiant.setNom(nom);
        if (!prenom.isEmpty()) etudiant.setPrenom(prenom);
        if (!email.isEmpty()) etudiant.setEmail(email);
        if (!telephone.isEmpty()) etudiant.setTelephone(telephone);
        
        etudiantDAO.update(etudiant);
        System.out.println("Étudiant modifié avec succès!");
    }
    
    private static void supprimerEtudiant() {
        System.out.println("\n--- SUPPRIMER UN ETUDIANT ---");
        String id = lireTexte("ID de l'étudiant à supprimer: ");
        
        Etudiant etudiant = etudiantDAO.findById(id);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé!");
            return;
        }
        
        if (etudiant.getNombreEmpruntsActifs() > 0) {
            System.out.println("Impossible de supprimer: l'étudiant a des emprunts en cours!");
            return;
        }
        
        System.out.println("Étudiant à supprimer: " + etudiant.getNomComplet());
        String confirmation = lireTexte("Confirmer la suppression (oui/non): ");
        
        if (confirmation.equalsIgnoreCase("oui")) {
            etudiantDAO.delete(id);
            System.out.println("Étudiant supprimé avec succès!");
        } else {
            System.out.println("Suppression annulée.");
        }
    }
    
    private static void gererLivres() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- GESTION DES LIVRES ---");
            System.out.println("1. Lister les livres");
            System.out.println("2. Ajouter un livre");
            System.out.println("3. Modifier un livre");
            System.out.println("4. Supprimer un livre");
            System.out.println("0. Retour");
            
            int choix = lireEntier("Votre choix: ");
            
            switch (choix) {
                case 1:
                    listerLivres();
                    break;
                case 2:
                    ajouterLivre();
                    break;
                case 3:
                    modifierLivre();
                    break;
                case 4:
                    supprimerLivre();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }
    
    private static void listerLivres() {
        System.out.println("\n--- LISTE DES LIVRES ---");
        List<Livre> livres = livreDAO.findAll();
        
        if (livres.isEmpty()) {
            System.out.println("Aucun livre enregistré.");
        } else {
            System.out.printf("%-15s %-30s %-20s %-15s %-8s %-12s %-8s\n", 
                "ISBN", "Titre", "Auteur", "Catégorie", "Année", "Disponibles", "Total");
            System.out.println("----------------------------------------------------------------------------------------");
            
            for (Livre l : livres) {
                System.out.printf("%-15s %-30s %-20s %-15s %-8d %-12d %-8d\n",
                    l.getIsbn(), l.getTitre(), l.getAuteur(), l.getCategorie(),
                    l.getAnneePublication(), l.getNombreExemplairesDisponibles(), 
                    l.getNombreExemplairesTotal());
            }
        }
    }
    
    private static void ajouterLivre() {
        System.out.println("\n--- AJOUTER UN LIVRE ---");
        String isbn = lireTexte("ISBN: ");
        
        if (livreDAO.exists(isbn)) {
            System.out.println("Cet ISBN existe déjà!");
            return;
        }
        
        String titre = lireTexte("Titre: ");
        String auteur = lireTexte("Auteur: ");
        String categorie = lireTexte("Catégorie: ");
        int annee = lireEntier("Année de publication: ");
        int exemplaires = lireEntier("Nombre d'exemplaires: ");
        
        Livre livre = new Livre(isbn, titre, auteur, categorie, annee, exemplaires);
        try {
            livreDAO.save(livre);
            System.out.println("Livre ajouté avec succès!");
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    private static void modifierLivre() {
        System.out.println("\n--- MODIFIER UN LIVRE ---");
        String isbn = lireTexte("ISBN du livre à modifier: ");
        
        Livre livre = livreDAO.findByIsbn(isbn);
        if (livre == null) {
            System.out.println("Livre non trouvé!");
            return;
        }
        
        System.out.println("Livre actuel: " + livre.getTitre());
        String titre = lireTexte("Nouveau titre (laisser vide pour conserver): ");
        String auteur = lireTexte("Nouvel auteur (laisser vide pour conserver): ");
        String categorie = lireTexte("Nouvelle catégorie (laisser vide pour conserver): ");
        
        if (!titre.isEmpty()) livre.setTitre(titre);
        if (!auteur.isEmpty()) livre.setAuteur(auteur);
        if (!categorie.isEmpty()) livre.setCategorie(categorie);
        
        livreDAO.update(livre);
        System.out.println("Livre modifié avec succès!");
    }
    
    private static void supprimerLivre() {
        System.out.println("\n--- SUPPRIMER UN LIVRE ---");
        String isbn = lireTexte("ISBN du livre à supprimer: ");
        
        Livre livre = livreDAO.findByIsbn(isbn);
        if (livre == null) {
            System.out.println("Livre non trouvé!");
            return;
        }
        
        if (livre.getNombreEmpruntsActifs() > 0) {
            System.out.println("Impossible de supprimer: le livre a des emprunts en cours!");
            return;
        }
        
        System.out.println("Livre à supprimer: " + livre.getTitre());
        String confirmation = lireTexte("Confirmer la suppression (oui/non): ");
        
        if (confirmation.equalsIgnoreCase("oui")) {
            livreDAO.delete(isbn);
            System.out.println("Livre supprimé avec succès!");
        } else {
            System.out.println("Suppression annulée.");
        }
    }
    
    private static void gererEmprunts() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n--- GESTION DES EMPRUNTS ---");
            System.out.println("1. Lister les emprunts");
            System.out.println("2. Emprunter un livre");
            System.out.println("3. Retourner un livre");
            System.out.println("4. Emprunts en retard");
            System.out.println("0. Retour");
            
            int choix = lireEntier("Votre choix: ");
            
            switch (choix) {
                case 1:
                    listerEmprunts();
                    break;
                case 2:
                    emprunterLivre();
                    break;
                case 3:
                    retournerLivre();
                    break;
                case 4:
                    empruntsEnRetard();
                    break;
                case 0:
                    continuer = false;
                    break;
                default:
                    System.out.println("Choix invalide!");
            }
        }
    }
    
    private static void listerEmprunts() {
        System.out.println("\n--- LISTE DES EMPRUNTS ---");
        List<Emprunt> emprunts = empruntDAO.findAll();
        
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt enregistré.");
        } else {
            System.out.printf("%-8s %-20s %-30s %-12s %-12s %-12s %-20s\n", 
                "ID", "Étudiant", "Livre", "Emprunt", "Retour prévu", "Retour", "Statut");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            
            for (Emprunt e : emprunts) {
                System.out.printf("%-8s %-20s %-30s %-12s %-12s %-12s %-20s\n",
                    e.getId(), e.getEtudiant().getNomComplet(), e.getLivre().getTitre(),
                    e.getDateEmprunt(), e.getDateRetourPrevue(), 
                    e.getDateRetour() != null ? e.getDateRetour().toString() : "Non retourné",
                    e.getStatut());
            }
        }
    }
    
    private static void emprunterLivre() {
        System.out.println("\n--- EMPRUNTER UN LIVRE ---");
        String idEtudiant = lireTexte("ID de l'étudiant: ");
        String isbnLivre = lireTexte("ISBN du livre: ");
        
        Etudiant etudiant = etudiantDAO.findById(idEtudiant);
        Livre livre = livreDAO.findByIsbn(isbnLivre);
        
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé!");
            return;
        }
        
        if (livre == null) {
            System.out.println("Livre non trouvé!");
            return;
        }
        
        if (!livre.estDisponible()) {
            System.out.println("Livre non disponible!");
            return;
        }
        
        if (etudiant.getNombreEmpruntsActifs() >= 5) {
            System.out.println("L'étudiant a déjà atteint le nombre maximum d'emprunts!");
            return;
        }
        
        if (etudiant.aDesEmpruntsEnRetard()) {
            System.out.println("L'étudiant a des emprunts en retard!");
            return;
        }
        
        String id = empruntDAO.generateNextId();
        Emprunt emprunt = new Emprunt(id, etudiant, livre);
        
        livre.emprunterExemplaire();
        etudiant.ajouterEmprunt(emprunt);
        livre.ajouterEmprunt(emprunt);
        
        empruntDAO.save(emprunt);
        System.out.println("Emprunt enregistré avec succès! ID: " + id);
    }
    
    private static void retournerLivre() {
        System.out.println("\n--- RETOURNER UN LIVRE ---");
        String idEmprunt = lireTexte("ID de l'emprunt: ");
        
        Emprunt emprunt = empruntDAO.findById(idEmprunt);
        if (emprunt == null) {
            System.out.println("Emprunt non trouvé!");
            return;
        }
        
        if (!emprunt.estActif()) {
            System.out.println("Cet emprunt a déjà été retourné!");
            return;
        }
        
        if (emprunt.retourner()) {
            emprunt.getLivre().retournerExemplaire();
            empruntDAO.update(emprunt);
            
            String message = "Livre retourné avec succès!";
            if (emprunt.estEnRetard()) {
                message += " (Attention: " + emprunt.getNombreJoursRetard() + " jours de retard)";
            }
            System.out.println(message);
        }
    }
    
    private static void empruntsEnRetard() {
        System.out.println("\n--- EMPRUNTS EN RETARD ---");
        List<Emprunt> emprunts = empruntDAO.findEnRetard();
        
        if (emprunts.isEmpty()) {
            System.out.println("Aucun emprunt en retard.");
        } else {
            System.out.printf("%-8s %-20s %-30s %-12s %-10s\n", 
                "ID", "Étudiant", "Livre", "Retour prévu", "Jours retard");
            System.out.println("------------------------------------------------------------------------");
            
            for (Emprunt e : emprunts) {
                System.out.printf("%-8s %-20s %-30s %-12s %-10d\n",
                    e.getId(), e.getEtudiant().getNomComplet(), e.getLivre().getTitre(),
                    e.getDateRetourPrevue(), e.getNombreJoursRetard());
            }
        }
    }
    
    private static void rechercherLivres() {
        System.out.println("\n--- RECHERCHER DES LIVRES ---");
        System.out.println("1. Par titre");
        System.out.println("2. Par auteur");
        System.out.println("3. Par catégorie");
        System.out.println("4. Livres disponibles");
        
        int choix = lireEntier("Type de recherche: ");
        
        switch (choix) {
            case 1:
                rechercherParTitre();
                break;
            case 2:
                rechercherParAuteur();
                break;
            case 3:
                rechercherParCategorie();
                break;
            case 4:
                listerLivresDisponibles();
                break;
            default:
                System.out.println("Choix invalide!");
        }
    }
    
    private static void rechercherParTitre() {
        String titre = lireTexte("Titre à rechercher: ");
        List<Livre> livres = livreDAO.searchByTitre(titre);
        
        System.out.println("\n--- RÉSULTATS DE RECHERCHE ---");
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé pour: " + titre);
        } else {
            for (Livre l : livres) {
                System.out.println(l.getTitre() + " - " + l.getAuteur() + " (" + l.getIsbn() + ")");
            }
        }
    }
    
    private static void rechercherParAuteur() {
        String auteur = lireTexte("Auteur à rechercher: ");
        List<Livre> livres = livreDAO.searchByAuteur(auteur);
        
        System.out.println("\n--- RÉSULTATS DE RECHERCHE ---");
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé pour: " + auteur);
        } else {
            for (Livre l : livres) {
                System.out.println(l.getTitre() + " - " + l.getAuteur() + " (" + l.getIsbn() + ")");
            }
        }
    }
    
    private static void rechercherParCategorie() {
        String categorie = lireTexte("Catégorie à rechercher: ");
        List<Livre> livres = livreDAO.findByCategorie(categorie);
        
        System.out.println("\n--- RÉSULTATS DE RECHERCHE ---");
        if (livres.isEmpty()) {
            System.out.println("Aucun livre trouvé pour la catégorie: " + categorie);
        } else {
            for (Livre l : livres) {
                System.out.println(l.getTitre() + " - " + l.getAuteur() + " (" + l.getIsbn() + ")");
            }
        }
    }
    
    private static void listerLivresDisponibles() {
        System.out.println("\n--- LIVRES DISPONIBLES ---");
        List<Livre> tousLivres = livreDAO.findAll();
        
        System.out.printf("%-15s %-30s %-20s %-15s\n", 
            "ISBN", "Titre", "Auteur", "Disponibles");
        System.out.println("--------------------------------------------------------");
        
        for (Livre l : tousLivres) {
            if (l.estDisponible()) {
                System.out.printf("%-15s %-30s %-20s %-15d\n",
                    l.getIsbn(), l.getTitre(), l.getAuteur(), 
                    l.getNombreExemplairesDisponibles());
            }
        }
    }
    
    private static void afficherStatistiques() {
        System.out.println("\n--- STATISTIQUES ---");
        
        List<Etudiant> etudiants = etudiantDAO.findAll();
        List<Livre> livres = livreDAO.findAll();
        List<Emprunt> emprunts = empruntDAO.findAll();
        List<Emprunt> empruntsActifs = empruntDAO.findActifs();
        List<Emprunt> empruntsEnRetard = empruntDAO.findEnRetard();
        
        System.out.println("Nombre total d'étudiants: " + etudiants.size());
        System.out.println("Nombre total de livres: " + livres.size());
        System.out.println("Nombre total d'exemplaires: " + 
            livres.stream().mapToInt(Livre::getNombreExemplairesTotal).sum());
        System.out.println("Nombre total d'emprunts: " + emprunts.size());
        System.out.println("Emprunts actifs: " + empruntsActifs.size());
        System.out.println("Emprunts en retard: " + empruntsEnRetard.size());
        
        double tauxDisponibilite = livres.stream()
            .mapToDouble(Livre::getTauxDisponibilite)
            .average()
            .orElse(0);
        System.out.printf("Taux de disponibilité moyen: %.1f%%\n", tauxDisponibilite);
        
        if (!empruntsEnRetard.isEmpty()) {
            System.out.println("\n--- Étudiants avec retards ---");
            for (Emprunt e : empruntsEnRetard) {
                System.out.println("- " + e.getEtudiant().getNomComplet() + 
                    " (" + e.getNombreJoursRetard() + " jours de retard)");
            }
        }
    }
    
    private static String lireTexte(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }
    
    private static int lireEntier(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }
}
