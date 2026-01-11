package com.university.library.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class HomeController {
    
    @FXML
    private void handleGestionEtudiants(ActionEvent event) {
        SceneManager.getInstance().showEtudiantView();
    }
    
    @FXML
    private void handleGestionLivres(ActionEvent event) {
        SceneManager.getInstance().showLivreView();
    }
    
    @FXML
    private void handleGestionEmprunts(ActionEvent event) {
        SceneManager.getInstance().showEmpruntView();
    }
    
    @FXML
    private void handleRechercheLivres(ActionEvent event) {
        SceneManager.getInstance().showRechercheView();
    }
    
    @FXML
    private void handleStatistiques(ActionEvent event) {
        SceneManager.getInstance().showStatistiquesView();
    }
}
