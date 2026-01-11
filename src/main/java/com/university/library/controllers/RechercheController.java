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

public class RechercheController {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private CheckBox disponiblesOnlyCheck;
    @FXML private TableView<Livre> livreTable;
    @FXML private TableColumn<Livre, String> isbnColumn;
    @FXML private TableColumn<Livre, String> titreColumn;
    @FXML private TableColumn<Livre, String> auteurColumn;
    @FXML private TableColumn<Livre, String> categorieColumn;
    @FXML private TableColumn<Livre, Integer> anneeColumn;
    @FXML private TableColumn<Livre, Integer> disponiblesColumn;
    @FXML private TableColumn<Livre, Integer> totalColumn;
    @FXML private Label resultLabel;
    
    private ObservableList<Livre> livres = FXCollections.observableArrayList();
    private LivreDAO livreDAO = new LivreDAO();
    private final String[] CATEGORIES = {"Informatique", "Mathématiques", "Physique", "Chimie", "Biologie", 
                                        "Économie", "Droit", "Littérature", "Histoire", "Roman", 
                                        "Science-fiction", "Philosophie", "Autre"};
    
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
        categorieCombo.getItems().add(0, ""); // Option "Toutes les catégories"
        categorieCombo.setValue("");
        
        // Liaison du tableau avec les données
        livreTable.setItems(livres);
        
        // Recherche automatique lors de la saisie
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2 || newValue.isEmpty()) {
                performSearch();
            }
        });
        
        // Recherche lors du changement de catégorie
        categorieCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            performSearch();
        });
        
        // Recherche lors du changement de la checkbox
        disponiblesOnlyCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            performSearch();
        });
        
        // Charger tous les livres au démarrage
        chargerTousLesLivres();
    }
    
    @FXML
    private void handleRechercher(ActionEvent event) {
        performSearch();
    }
    
    @FXML
    private void handleReinitialiser(ActionEvent event) {
        searchField.clear();
        categorieCombo.setValue("");
        disponiblesOnlyCheck.setSelected(false);
        chargerTousLesLivres();
    }
    
    @FXML
    private void handleRetour(ActionEvent event) {
        SceneManager.getInstance().showHomeView();
    }
    
    private void performSearch() {
        try {
            String searchTerm = searchField.getText().trim();
            String categorie = categorieCombo.getValue();
            boolean disponiblesOnly = disponiblesOnlyCheck.isSelected();
            
            List<Livre> resultats;
            
            // Si un terme de recherche est fourni, rechercher par titre, auteur ou ISBN
            if (!searchTerm.isEmpty()) {
                // Recherche par titre
                List<Livre> parTitre = livreDAO.searchByTitre(searchTerm);
                
                // Recherche par auteur
                List<Livre> parAuteur = livreDAO.searchByAuteur(searchTerm);
                
                // Recherche par ISBN
                Livre parIsbn = livreDAO.findByIsbn(searchTerm);
                
                // Combiner les résultats (éviter les doublons par ISBN)
                java.util.Set<String> isbnsVus = new java.util.HashSet<>();
                resultats = new java.util.ArrayList<>();
                
                // Ajouter les résultats par titre
                for (Livre livre : parTitre) {
                    if (!isbnsVus.contains(livre.getIsbn())) {
                        resultats.add(livre);
                        isbnsVus.add(livre.getIsbn());
                    }
                }
                
                // Ajouter les résultats par auteur
                for (Livre livre : parAuteur) {
                    if (!isbnsVus.contains(livre.getIsbn())) {
                        resultats.add(livre);
                        isbnsVus.add(livre.getIsbn());
                    }
                }
                
                // Ajouter le résultat par ISBN si trouvé
                if (parIsbn != null && !isbnsVus.contains(parIsbn.getIsbn())) {
                    resultats.add(parIsbn);
                    isbnsVus.add(parIsbn.getIsbn());
                }
            } else {
                // Pas de terme de recherche, charger tous les livres
                resultats = livreDAO.findAll();
            }
            
            // Filtrer par catégorie si sélectionnée
            if (categorie != null && !categorie.isEmpty()) {
                resultats = resultats.stream()
                    .filter(l -> l.getCategorie().equals(categorie))
                    .toList();
            }
            
            // Filtrer par disponibilité si demandé
            if (disponiblesOnly) {
                resultats = resultats.stream()
                    .filter(Livre::estDisponible)
                    .toList();
            }
            
            // Mettre à jour le tableau
            livres.clear();
            livres.addAll(resultats);
            
            // Mettre à jour le label de résultats
            if (resultats.isEmpty()) {
                resultLabel.setText("Aucun livre trouvé");
                resultLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                resultLabel.setText(resultats.size() + " livre(s) trouvé(s)");
                resultLabel.setStyle("-fx-text-fill: #27ae60;");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche: " + e.getMessage());
            afficherAlerte("Erreur lors de la recherche: " + e.getMessage());
        }
    }
    
    private void chargerTousLesLivres() {
        try {
            List<Livre> tousLesLivres = livreDAO.findAll();
            livres.clear();
            livres.addAll(tousLesLivres);
            resultLabel.setText(tousLesLivres.size() + " livre(s) au total");
            resultLabel.setStyle("-fx-text-fill: #2c3e50;");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des livres: " + e.getMessage());
            resultLabel.setText("Erreur lors du chargement");
            resultLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }
    
    private void afficherAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

