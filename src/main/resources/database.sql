-- Script SQL pour créer la base de données et les tables
-- Exécutez ce script dans MySQL avant de lancer l'application

CREATE DATABASE IF NOT EXISTS bibliotheque_db;
USE bibliotheque_db;

-- Table des étudiants
CREATE TABLE IF NOT EXISTS etudiants (
    id VARCHAR(20) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des livres
CREATE TABLE IF NOT EXISTS livres (
    isbn VARCHAR(50) PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    auteur VARCHAR(255) NOT NULL,
    categorie VARCHAR(100),
    annee_publication INT,
    nombre_exemplaires_total INT NOT NULL DEFAULT 0,
    nombre_exemplaires_disponibles INT NOT NULL DEFAULT 0,
    INDEX idx_titre (titre),
    INDEX idx_auteur (auteur),
    INDEX idx_categorie (categorie)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des emprunts
CREATE TABLE IF NOT EXISTS emprunts (
    id VARCHAR(20) PRIMARY KEY,
    id_etudiant VARCHAR(20) NOT NULL,
    isbn_livre VARCHAR(50) NOT NULL,
    date_emprunt DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour DATE NULL,
    FOREIGN KEY (id_etudiant) REFERENCES etudiants(id) ON DELETE CASCADE,
    FOREIGN KEY (isbn_livre) REFERENCES livres(isbn) ON DELETE CASCADE,
    INDEX idx_etudiant (id_etudiant),
    INDEX idx_livre (isbn_livre),
    INDEX idx_date_retour_prevue (date_retour_prevue)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Données d'exemple (optionnel)
INSERT INTO etudiants (id, nom, prenom, email, telephone) VALUES
('ETU001', 'Dupont', 'Jean', 'jean.dupont@univ.fr', '0123456789'),
('ETU002', 'Martin', 'Marie', 'marie.martin@univ.fr', '0987654321'),
('ETU003', 'Bernard', 'Pierre', 'pierre.bernard@univ.fr', '0612345678')
ON DUPLICATE KEY UPDATE nom=nom;

INSERT INTO livres (isbn, titre, auteur, categorie, annee_publication, nombre_exemplaires_total, nombre_exemplaires_disponibles) VALUES
('978-2-07-041464-4', 'Le Petit Prince', 'Antoine de Saint-Exupéry', 'Roman', 1943, 5, 3),
('978-2-253-05656-5', '1984', 'George Orwell', 'Science-fiction', 1949, 3, 2),
('978-2-07-036002-4', 'L''Étranger', 'Albert Camus', 'Roman', 1942, 4, 4)
ON DUPLICATE KEY UPDATE titre=titre;

INSERT INTO emprunts (id, id_etudiant, isbn_livre, date_emprunt, date_retour_prevue, date_retour) VALUES
('EMP001', 'ETU001', '978-2-07-041464-4', '2024-01-15', '2024-02-15', NULL),
('EMP002', 'ETU002', '978-2-253-05656-5', '2024-01-20', '2024-02-20', NULL)
ON DUPLICATE KEY UPDATE id=id;

