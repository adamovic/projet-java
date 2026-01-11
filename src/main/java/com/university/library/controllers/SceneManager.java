package com.university.library.controllers;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;
    private Map<String, Scene> scenes = new HashMap<>();
    
    private SceneManager() {}
    
    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void showHomeView() {
        try {
            if (!scenes.containsKey("home")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/home.fxml"));
                scenes.put("home", new Scene(loader.load()));
            }
            primaryStage.setScene(scenes.get("home"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showEtudiantView() {
        try {
            if (!scenes.containsKey("etudiant")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/etudiant_view.fxml"));
                scenes.put("etudiant", new Scene(loader.load()));
            }
            primaryStage.setScene(scenes.get("etudiant"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showLivreView() {
        try {
            if (!scenes.containsKey("livre")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/livre_view.fxml"));
                scenes.put("livre", new Scene(loader.load()));
            }
            primaryStage.setScene(scenes.get("livre"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showEmpruntView() {
        try {
            if (!scenes.containsKey("emprunt")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/emprunt_view.fxml"));
                scenes.put("emprunt", new Scene(loader.load()));
            }
            primaryStage.setScene(scenes.get("emprunt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showRechercheView() {
        try {
            if (!scenes.containsKey("recherche")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/recherche_view.fxml"));
                scenes.put("recherche", new Scene(loader.load()));
            }
            primaryStage.setScene(scenes.get("recherche"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showStatistiquesView() {
        // À implémenter plus tard
        System.out.println("Vue de statistiques non encore implémentée");
    }
}
