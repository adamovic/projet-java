package com.university.library.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.university.library.models.Etudiant;
import com.university.library.dao.EtudiantDAO;

import java.util.ArrayList;
import java.util.List;

public class EtudiantController {
    
    @FXML private TextField idField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TableView<Etudiant> etudiantTable;
    @FXML private TableColumn<Etudiant, String> idColumn;
    @FXML private TableColumn<Etudiant, String> nomColumn;
    @FXML private TableColumn<Etudiant, String> prenomColumn;
    @FXML private TableColumn<Etudiant, String> emailColumn;
    @FXML private TableColumn<Etudiant, String> telephoneColumn;
    @FXML private TableColumn<Etudiant, String> empruntsColumn;
    
    private ObservableList<Etudiant> etudiants = FXCollections.observableArrayList();
    private EtudiantDAO etudiantDAO = new EtudiantDAO();
    
    @FXML
    public void initialize() {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        empruntsColumn.setCellValueFactory(new PropertyValueFactory<>("nombreEmpruntsActifs"));
        
        // Charger les données depuis MySQL
        chargerDonnees();
        
        // Liaison du tableau avec les données
        etudiantTable.setItems(etudiants);
        
        // Gestion de la sélection
        etudiantTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> afficherEtudiantSelectionne(newSelection));
    }
    
    private void chargerDonnees() {
        try {
            List<Etudiant> etudiantsList = etudiantDAO.findAll();
            etudiants.clear();
            etudiants.addAll(etudiantsList);
            System.out.println("✓ " + etudiants.size() + " étudiant(s) chargé(s) depuis MySQL");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des étudiants: " + e.getMessage());
            // Ne pas afficher d'alerte au chargement initial pour éviter de spammer l'utilisateur
            // L'erreur sera visible dans la console
        }
    }
    
    private void afficherEtudiantSelectionne(Etudiant etudiant) {
        if (etudiant != null) {
            idField.setText(etudiant.getId());
            nomField.setText(etudiant.getNom());
            prenomField.setText(etudiant.getPrenom());
            emailField.setText(etudiant.getEmail());
            telephoneField.setText(etudiant.getTelephone());
        } else {
            viderChamps();
        }
    }
    
    private void viderChamps() {
        idField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        telephoneField.clear();
    }
    
    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            String id = idField.getText().trim();
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String telephone = telephoneField.getText().trim();
            
            if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs obligatoires");
                return;
            }
            
            // Vérifier si l'ID existe déjà dans MySQL
            if (etudiantDAO.exists(id)) {
                afficherAlerte("Un étudiant avec cet ID existe déjà");
                return;
            }
            
            // Créer et sauvegarder dans MySQL
            Etudiant nouvelEtudiant = new Etudiant(id, nom, prenom, email, telephone);
            etudiantDAO.save(nouvelEtudiant);
            
            // Recharger les données depuis MySQL
            chargerDonnees();
            viderChamps();
            afficherInformation("Étudiant ajouté avec succès dans la base de données");
            
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur SQL lors de l'ajout: " + e.getMessage());
            String message = "Erreur de connexion à la base de données.\n\n";
            message += "Vérifiez que:\n";
            message += "1. MySQL est démarré dans XAMPP\n";
            message += "2. La base de données 'bibliotheque_db' existe\n";
            message += "3. Les paramètres dans database.properties sont corrects\n\n";
            message += "Détails: " + e.getMessage();
            afficherAlerte(message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            afficherAlerte("Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleModifier(ActionEvent event) {
        Etudiant etudiantSelectionne = etudiantTable.getSelectionModel().getSelectedItem();
        if (etudiantSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un étudiant à modifier");
            return;
        }
        
        try {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            String email = emailField.getText().trim();
            String telephone = telephoneField.getText().trim();
            
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                afficherAlerte("Veuillez remplir tous les champs obligatoires");
                return;
            }
            
            // Mettre à jour l'objet
            etudiantSelectionne.setNom(nom);
            etudiantSelectionne.setPrenom(prenom);
            etudiantSelectionne.setEmail(email);
            etudiantSelectionne.setTelephone(telephone);
            
            // Sauvegarder dans MySQL
            etudiantDAO.update(etudiantSelectionne);
            
            // Recharger les données
            chargerDonnees();
            afficherInformation("Étudiant modifié avec succès dans la base de données");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la modification: " + e.getMessage());
            afficherAlerte("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimer(ActionEvent event) {
        Etudiant etudiantSelectionne = etudiantTable.getSelectionModel().getSelectedItem();
        if (etudiantSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un étudiant à supprimer");
            return;
        }
        
        if (etudiantSelectionne.getNombreEmpruntsActifs() > 0) {
            afficherAlerte("Impossible de supprimer un étudiant ayant des emprunts en cours");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer l'étudiant");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + etudiantSelectionne.getNomComplet() + " ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                // Supprimer de MySQL
                etudiantDAO.delete(etudiantSelectionne.getId());
                
                // Recharger les données
                chargerDonnees();
                viderChamps();
                afficherInformation("Étudiant supprimé avec succès de la base de données");
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
