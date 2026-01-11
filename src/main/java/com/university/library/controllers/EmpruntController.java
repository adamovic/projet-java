package com.university.library.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.university.library.models.Emprunt;
import com.university.library.models.Etudiant;
import com.university.library.models.Livre;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class EmpruntController {
    
    @FXML private ComboBox<Etudiant> etudiantCombo;
    @FXML private ComboBox<Livre> livreCombo;
    @FXML private ComboBox<Emprunt> empruntCombo;
    @FXML private TableView<Emprunt> empruntTable;
    @FXML private TableColumn<Emprunt, String> idColumn;
    @FXML private TableColumn<Emprunt, String> etudiantColumn;
    @FXML private TableColumn<Emprunt, String> livreColumn;
    @FXML private TableColumn<Emprunt, String> dateEmpruntColumn;
    @FXML private TableColumn<Emprunt, String> dateRetourPrevueColumn;
    @FXML private TableColumn<Emprunt, String> dateRetourColumn;
    @FXML private TableColumn<Emprunt, String> statutColumn;
    @FXML private Label infoLabel;
    @FXML private Label retardLabel;
    
    private ObservableList<Emprunt> emprunts = FXCollections.observableArrayList();
    private ObservableList<Etudiant> etudiants = FXCollections.observableArrayList();
    private ObservableList<Livre> livres = FXCollections.observableArrayList();
    private ObservableList<Emprunt> empruntsActifs = FXCollections.observableArrayList();
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @FXML
    public void initialize() {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        etudiantColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEtudiant().getNomComplet()));
        livreColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLivre().getTitre()));
        dateEmpruntColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(formatDate(cellData.getValue().getDateEmprunt())));
        dateRetourPrevueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(formatDate(cellData.getValue().getDateRetourPrevue())));
        dateRetourColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(formatDate(cellData.getValue().getDateRetour())));
        statutColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));
        
        // Données de test
        chargerDonneesTest();
        
        // Configuration des combos
        etudiantCombo.setItems(etudiants);
        livreCombo.setItems(livres);
        empruntCombo.setItems(empruntsActifs);
        
        // Liaison du tableau avec les données
        empruntTable.setItems(emprunts);
        
        // Mise à jour des informations
        majInformations();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(dateFormatter);
    }

    private void marquerEnRetard(Emprunt emprunt, int joursRetard) {
        Date nouvelleDate = Date.from(
            emprunt.getDateRetourPrevue()
                   .toInstant()
                   .minus(joursRetard, ChronoUnit.DAYS));
        emprunt.setDateRetourPrevue(nouvelleDate);
    }
    
    private void chargerDonneesTest() {
        // Étudiants de test
        etudiants.add(new Etudiant("E001", "Dupont", "Jean", "jean.dupont@univ.fr", "0612345678"));
        etudiants.add(new Etudiant("E002", "Martin", "Sophie", "sophie.martin@univ.fr", "0623456789"));
        etudiants.add(new Etudiant("E003", "Bernard", "Pierre", "pierre.bernard@univ.fr", "0634567890"));
        
        // Livres de test
        livres.add(new Livre("978-2-212-09561-0", "Java Programming", "John Smith", "Informatique", 2022, 5));
        livres.add(new Livre("978-2-212-09562-7", "Data Structures", "Alice Johnson", "Informatique", 2021, 3));
        livres.add(new Livre("978-2-212-09563-4", "Algorithm Design", "Bob Wilson", "Informatique", 2023, 4));
        
        // Emprunts de test
        Emprunt emprunt1 = new Emprunt("EMP001", etudiants.get(0), livres.get(0));
        Emprunt emprunt2 = new Emprunt("EMP002", etudiants.get(1), livres.get(1));
        
        // Simuler un emprunt en retard
        Emprunt emprunt3 = new Emprunt("EMP003", etudiants.get(2), livres.get(2));
        marquerEnRetard(emprunt3, 5);
        
        livres.get(0).emprunterExemplaire();
        livres.get(1).emprunterExemplaire();
        livres.get(2).emprunterExemplaire();
        
        etudiants.get(0).ajouterEmprunt(emprunt1);
        etudiants.get(1).ajouterEmprunt(emprunt2);
        etudiants.get(2).ajouterEmprunt(emprunt3);
        
        emprunts.add(emprunt1);
        emprunts.add(emprunt2);
        emprunts.add(emprunt3);
        
        majEmpruntsActifs();
    }
    
    private void majEmpruntsActifs() {
        empruntsActifs.clear();
        empruntsActifs.addAll(emprunts.stream().filter(Emprunt::estActif).toList());
    }
    
    private void majInformations() {
        int totalEmprunts = emprunts.size();
        int empruntsActifs = (int) emprunts.stream().filter(Emprunt::estActif).count();
        int empruntsEnRetard = (int) emprunts.stream().filter(Emprunt::estEnRetard).count();
        
        infoLabel.setText("Total: " + totalEmprunts + " | Actifs: " + empruntsActifs);
        
        if (empruntsEnRetard > 0) {
            retardLabel.setText("⚠ " + empruntsEnRetard + " emprunt(s) en retard");
        } else {
            retardLabel.setText("");
        }
    }
    
    @FXML
    private void handleEmprunter(ActionEvent event) {
        Etudiant etudiantSelectionne = etudiantCombo.getValue();
        Livre livreSelectionne = livreCombo.getValue();
        
        if (etudiantSelectionne == null || livreSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un étudiant et un livre");
            return;
        }
        
        if (!livreSelectionne.estDisponible()) {
            afficherAlerte("Ce livre n'est pas disponible");
            return;
        }
        
        if (etudiantSelectionne.getNombreEmpruntsActifs() >= 5) {
            afficherAlerte("L'étudiant a déjà atteint le nombre maximum d'emprunts (5)");
            return;
        }
        
        if (etudiantSelectionne.aDesEmpruntsEnRetard()) {
            afficherAlerte("L'étudiant a des emprunts en retard. Veuillez les retourner d'abord");
            return;
        }
        
        try {
            String id = "EMP" + String.format("%03d", emprunts.size() + 1);
            Emprunt nouvelEmprunt = new Emprunt(id, etudiantSelectionne, livreSelectionne);
            
            livreSelectionne.emprunterExemplaire();
            etudiantSelectionne.ajouterEmprunt(nouvelEmprunt);
            livreSelectionne.ajouterEmprunt(nouvelEmprunt);
            
            emprunts.add(nouvelEmprunt);
            majEmpruntsActifs();
            majInformations();
            
            // Réinitialiser la sélection
            etudiantCombo.setValue(null);
            livreCombo.setValue(null);
            
            afficherInformation("Emprunt enregistré avec succès");
            
        } catch (Exception e) {
            afficherAlerte("Erreur lors de l'emprunt: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRetourner(ActionEvent event) {
        Emprunt empruntSelectionne = empruntCombo.getValue();
        
        if (empruntSelectionne == null) {
            afficherAlerte("Veuillez sélectionner un emprunt à retourner");
            return;
        }
        
        try {
            if (empruntSelectionne.retourner()) {
                empruntSelectionne.getLivre().retournerExemplaire();
                majEmpruntsActifs();
                majInformations();
                empruntTable.refresh();
                
                empruntCombo.setValue(null);
                
                String message = "Livre retourné avec succès";
                if (empruntSelectionne.estEnRetard()) {
                    message += " (Attention: " + empruntSelectionne.getNombreJoursRetard() + " jours de retard)";
                }
                afficherInformation(message);
            } else {
                afficherAlerte("Cet emprunt a déjà été retourné");
            }
            
        } catch (Exception e) {
            afficherAlerte("Erreur lors du retour: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRafraichir(ActionEvent event) {
        majEmpruntsActifs();
        majInformations();
        empruntTable.refresh();
        afficherInformation("Liste rafraîchie");
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
