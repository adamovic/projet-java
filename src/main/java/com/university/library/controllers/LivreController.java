package com.university.library.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.university.library.models.Livre;
import com.university.library.dao.LivreDAO;
import java.util.List;

public class LivreController {
    
    @FXML private TextField isbnField;
    @FXML private TextField titreField;
    @FXML private TextField auteurField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private TextField anneeField;
    @FXML private TextField exemplairesField;
    @FXML private TableView<Livre> livreTable;
    @FXML private TableColumn<Livre, String> isbnColumn;
    @FXML private TableColumn<Livre, String> titreColumn;
    @FXML private TableColumn<Livre, String> auteurColumn;
    @FXML private TableColumn<Livre, String> categorieColumn;
    @FXML private TableColumn<Livre, Integer> anneeColumn;
    @FXML private TableColumn<Livre, Integer> disponiblesColumn;
    @FXML private TableColumn<Livre, Integer> totalColumn;
    
    private ObservableList<Livre> livres = FXCollections.observableArrayList();
    private LivreDAO livreDAO = new LivreDAO();
    private final String[] CATEGORIES = {"Informatique", "Mathématiques", "Physique", "Chimie", "Biologie", "Économie", "Droit", "Littérature", "Histoire", "Roman", "Science-fiction", "Autre"};
    
    @FXML
    public void initialize() {
        // Configuration des colonnes
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        anneeColumn.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));
        disponiblesColumn.setCellValueFactory(new PropertyValueFactory<>("nombreExemplairesDisponibles"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("nombreExemplairesTotal"));
        
        // Configuration du combo des catégories
        categorieCombo.getItems().addAll(CATEGORIES);
        
        // Charger les données depuis MySQL
        chargerDonnees();
        
        // Liaison du tableau avec les données
        livreTable.setItems(livres);
        
        // Gestion de la sélection
        livreTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> afficherLivreSelectionne(newSelection));
    }
    
    private void chargerDonnees() {
        try {
            List<Livre> livresList = livreDAO.findAll();
            livres.clear();
            livres.addAll(livresList);
            System.out.println("✓ " + livres.size() + " livre(s) chargé(s) depuis MySQL");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des livres: " + e.getMessage());
            afficherAlerte("Erreur lors du chargement des données depuis la base de données");
        }
    }
    
    private void afficherLivreSelectionne(Livre livre) {
        if (livre != null) {
            isbnField.setText(livre.getIsbn());
            titreField.setText(livre.getTitre());
            auteurField.setText(livre.getAuteur());
            categorieCombo.setValue(livre.getCategorie());
            anneeField.setText(String.valueOf(livre.getAnneePublication()));
            exemplairesField.setText(String.valueOf(livre.getNombreExemplairesTotal()));
        } else {
            viderChamps();
        }
    }
    
    private void viderChamps() {
        isbnField.clear();
        titreField.clear();
        auteurField.clear();
        categorieCombo.setValue(null);
        anneeField.clear();
        exemplairesField.clear();
    }
    
    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            String isbn = isbnField.getText().trim();
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();
            String categorie = categorieCombo.getValue();
            String anneeStr = anneeField.getText().trim();
            String exemplairesStr = exemplairesField.getText().trim();
            
            if (isbn.isEmpty() || titre.isEmpty() || auteur.isEmpty() || categorie == null || anneeStr.isEmpty() || exemplairesStr.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs");
                return;
            }
            
            // Vérifier si l'ISBN existe déjà dans MySQL
            if (livreDAO.exists(isbn)) {
                afficherAlerte("Un livre avec cet ISBN existe déjà");
                return;
            }
            
            int annee = Integer.parseInt(anneeStr);
            int exemplaires = Integer.parseInt(exemplairesStr);
            
            if (exemplaires <= 0) {
                afficherAlerte("Le nombre d'exemplaires doit être positif");
                return;
            }
            
            // Créer et sauvegarder dans MySQL
            Livre nouveauLivre = new Livre(isbn, titre, auteur, categorie, annee, exemplaires);
            livreDAO.save(nouveauLivre);
            
            // Recharger les données depuis MySQL
            chargerDonnees();
            viderChamps();
            afficherInformation("Livre ajouté avec succès dans la base de données");
            
        } catch (NumberFormatException e) {
            afficherAlerte("L'année et le nombre d'exemplaires doivent être des nombres valides");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
            afficherAlerte("Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleModifier(ActionEvent event) {
        Livre livreSelectionne = livreTable.getSelectionModel().getSelectedItem();
        if (livreSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un livre à modifier");
            return;
        }
        
        try {
            String titre = titreField.getText().trim();
            String auteur = auteurField.getText().trim();
            String categorie = categorieCombo.getValue();
            String anneeStr = anneeField.getText().trim();
            String exemplairesStr = exemplairesField.getText().trim();
            
            if (titre.isEmpty() || auteur.isEmpty() || categorie == null || anneeStr.isEmpty() || exemplairesStr.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs");
                return;
            }
            
            int annee = Integer.parseInt(anneeStr);
            int exemplairesTotal = Integer.parseInt(exemplairesStr);
            
            if (exemplairesTotal < livreSelectionne.getNombreExemplairesTotal() - livreSelectionne.getNombreExemplairesDisponibles()) {
                afficherAlerte("Impossible de réduire le nombre total d'exemplaires en dessous du nombre actuellement emprunté");
                return;
            }
            
            // Mettre à jour l'objet
            livreSelectionne.setTitre(titre);
            livreSelectionne.setAuteur(auteur);
            livreSelectionne.setCategorie(categorie);
            livreSelectionne.setAnneePublication(annee);
            livreSelectionne.setNombreExemplairesTotal(exemplairesTotal);
            
            // Sauvegarder dans MySQL
            livreDAO.update(livreSelectionne);
            
            // Recharger les données
            chargerDonnees();
            afficherInformation("Livre modifié avec succès dans la base de données");
            
        } catch (NumberFormatException e) {
            afficherAlerte("L'année et le nombre d'exemplaires doivent être des nombres valides");
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
            afficherAlerte("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer(ActionEvent event) {
        Livre livreSelectionne = livreTable.getSelectionModel().getSelectedItem();
        if (livreSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un livre à supprimer");
            return;
        }
        
        if (livreSelectionne.getNombreEmpruntsActifs() > 0) {
            afficherAlerte("Impossible de supprimer un livre ayant des emprunts en cours");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le livre");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer \"" + livreSelectionne.getTitre() + "\" ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                // Supprimer de MySQL
                livreDAO.delete(livreSelectionne.getIsbn());
                
                // Recharger les données
                chargerDonnees();
                viderChamps();
                afficherInformation("Livre supprimé avec succès de la base de données");
            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression: " + e.getMessage());
                afficherAlerte("Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRetour(ActionEvent event) {
        SceneManager.getInstance().showHomeView();
    }
    
    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void afficherInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
