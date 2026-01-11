# Projet JavaFX : Gestion de Bibliothèque Universitaire

## Description
Application de gestion de bibliothèque universitaire développée en JavaFX avec architecture MVC.

## Fonctionnalités
- Gestion des étudiants et livres
- Système d'emprunt/retour automatisé
- Recherche multi-critères
- Suivi des disponibilités en temps réel
- Notifications de retard

## Architecture
- **Modèles** : Entités métier (Étudiant, Livre, Emprunt)
- **Vues** : Interfaces FXML
- **Contrôleurs** : Logique de gestion UI
- **Services** : Logique métier (à implémenter)
- **DAO** : Accès données MySQL via JDBC

## Structure
```
src/main/java/com/university/library/
├── Main.java
├── controllers/
├── models/
├── services/
└── dao/

src/main/resources/
├── views/
└── data/
```

## Prérequis
- Java 17 ou supérieur
- Maven
- MySQL Server

## Configuration de la Base de Données

1. Créer la base de données MySQL :
   ```bash
   mysql -u root -p < src/main/resources/database.sql
   ```

2. Configurer la connexion dans `src/main/resources/database.properties`

Voir `README_MYSQL.md` pour plus de détails.

## Compilation et Exécution
```bash
mvn clean compile
mvn javafx:run
```
# projet-java
