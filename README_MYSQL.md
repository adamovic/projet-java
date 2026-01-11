# Configuration MySQL pour le Projet de Gestion de Bibliothèque

## Prérequis

1. **MySQL Server** installé et en cours d'exécution
2. **Java 17** ou supérieur
3. **Maven** installé

## Installation et Configuration

### 1. Créer la base de données

Exécutez le script SQL fourni pour créer la base de données et les tables :

```bash
mysql -u root -p < src/main/resources/database.sql
```

Ou connectez-vous à MySQL et exécutez le contenu du fichier `src/main/resources/database.sql` :

```sql
mysql -u root -p
source src/main/resources/database.sql
```

### 2. Configurer la connexion

Modifiez le fichier `src/main/resources/database.properties` avec vos paramètres de connexion :

```properties
db.url=jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=votre_mot_de_passe
```

**Paramètres par défaut :**
- **URL** : `jdbc:mysql://localhost:3306/bibliotheque_db`
- **Utilisateur** : `root`
- **Mot de passe** : (vide par défaut)

### 3. Compiler le projet

```bash
mvn clean compile
```

### 4. Lancer l'application

```bash
mvn javafx:run
```

## Structure de la Base de Données

### Table `etudiants`
- `id` (VARCHAR) - Clé primaire
- `nom` (VARCHAR)
- `prenom` (VARCHAR)
- `email` (VARCHAR) - Unique
- `telephone` (VARCHAR)

### Table `livres`
- `isbn` (VARCHAR) - Clé primaire
- `titre` (VARCHAR)
- `auteur` (VARCHAR)
- `categorie` (VARCHAR)
- `annee_publication` (INT)
- `nombre_exemplaires_total` (INT)
- `nombre_exemplaires_disponibles` (INT)

### Table `emprunts`
- `id` (VARCHAR) - Clé primaire
- `id_etudiant` (VARCHAR) - Clé étrangère vers `etudiants`
- `isbn_livre` (VARCHAR) - Clé étrangère vers `livres`
- `date_emprunt` (DATE)
- `date_retour_prevue` (DATE)
- `date_retour` (DATE) - NULL si emprunt actif

## Migration depuis CSV

Les fichiers CSV dans `src/main/resources/data/` ne sont plus utilisés. Les données sont maintenant stockées dans MySQL.

**Note :** Si vous avez des données existantes dans les fichiers CSV, vous devrez les importer manuellement dans MySQL ou créer un script de migration.

## Dépannage

### Erreur de connexion

1. Vérifiez que MySQL est en cours d'exécution :
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl status mysql
   ```

2. Vérifiez les paramètres dans `database.properties`

3. Vérifiez que la base de données `bibliotheque_db` existe :
   ```sql
   SHOW DATABASES;
   ```

### Erreur "Driver not found"

Assurez-vous que la dépendance MySQL est bien téléchargée :
```bash
mvn dependency:resolve
```

### Erreur de permissions

Assurez-vous que l'utilisateur MySQL a les permissions nécessaires :
```sql
GRANT ALL PRIVILEGES ON bibliotheque_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

## Architecture

- **DatabaseConnection** : Singleton pour gérer la connexion MySQL
- **EtudiantDAO** : Accès aux données des étudiants
- **LivreDAO** : Accès aux données des livres
- **EmpruntDAO** : Accès aux données des emprunts

Tous les DAO utilisent maintenant MySQL au lieu des fichiers CSV.

