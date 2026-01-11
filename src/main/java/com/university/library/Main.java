package com.university.library;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.university.library.controllers.SceneManager;
import com.university.library.dao.DatabaseConnection;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialiser la connexion à la base de données MySQL
        System.out.println("Initialisation de la connexion MySQL...");
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        try {
            if (dbConnection.getConnection() != null) {
                System.out.println("✓ Application prête à utiliser MySQL!");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("⚠ Attention: Problème de connexion à MySQL. Vérifiez votre configuration.");
            System.err.println("  Détails: " + e.getMessage());
        }
        
        SceneManager.getInstance().setPrimaryStage(primaryStage);
        SceneManager.getInstance().showHomeView();
        
        primaryStage.setTitle("Gestion de Bibliothèque Universitaire");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
