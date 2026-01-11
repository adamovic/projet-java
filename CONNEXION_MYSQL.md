# Connexion du Projet à MySQL

## ✅ État de la Connexion

Le projet est **déjà entièrement lié à MySQL**. Tous les DAO utilisent maintenant MySQL au lieu des fichiers CSV.

## 📋 Configuration Actuelle

### 1. Fichier de Configuration
**`src/main/resources/database.properties`**
```properties
db.url=jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=
```

### 2. Classe de Connexion
**`src/main/java/com/university/library/dao/DatabaseConnection.java`**
- Pattern Singleton pour une seule instance de connexion
- Charge automatiquement les paramètres depuis `database.properties`
- Gère la reconnexion automatique si la connexion est fermée

### 3. DAO Utilisant MySQL
- ✅ **EtudiantDAO** - Gère les étudiants dans MySQL
- ✅ **LivreDAO** - Gère les livres dans MySQL
- ✅ **EmpruntDAO** - Gère les emprunts dans MySQL

## 🔧 Vérification de la Connexion

### Option 1: Test Automatique
Exécutez le script de test :
```bash
test_connection.bat
```

Ou manuellement :
```bash
mvn exec:java -Dexec.mainClass="com.university.library.dao.TestConnection"
```

### Option 2: Vérification Manuelle
1. Assurez-vous que MySQL est démarré dans XAMPP
2. Vérifiez que la base `bibliotheque_db` existe dans phpMyAdmin
3. Lancez l'application - vous verrez un message de connexion dans la console

## 🚀 Démarrage de l'Application

L'application teste automatiquement la connexion MySQL au démarrage :

```bash
mvn javafx:run
```

Vous verrez dans la console :
- `✓ Connexion à la base de données MySQL réussie!`
- `✓ Application prête à utiliser MySQL!`

## 📊 Structure de la Base de Données

### Tables
1. **etudiants** - Informations des étudiants
2. **livres** - Catalogue des livres
3. **emprunts** - Historique des emprunts

### Relations
- `emprunts.id_etudiant` → `etudiants.id` (Foreign Key)
- `emprunts.isbn_livre` → `livres.isbn` (Foreign Key)

## ⚙️ Modification de la Configuration

Si vous devez changer les paramètres de connexion :

1. Éditez `src/main/resources/database.properties`
2. Modifiez les valeurs :
   ```properties
   db.url=jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC
   db.username=votre_utilisateur
   db.password=votre_mot_de_passe
   ```
3. Recompilez : `mvn clean compile`
4. Relancez l'application

## 🔍 Dépannage

### Erreur: "Driver MySQL non trouvé"
**Solution:** Vérifiez que la dépendance est dans `pom.xml` :
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```
Puis exécutez : `mvn dependency:resolve`

### Erreur: "Access denied for user"
**Solution:** Vérifiez les identifiants dans `database.properties`

### Erreur: "Unknown database 'bibliotheque_db'"
**Solution:** Créez la base de données :
```bash
# Dans phpMyAdmin, exécutez le script database.sql
# Ou via la ligne de commande :
mysql -u root -p < src/main/resources/database.sql
```

### Erreur: "Communications link failure"
**Solution:** Vérifiez que MySQL est démarré dans XAMPP

## 📝 Notes Importantes

- ✅ Les fichiers CSV ne sont **plus utilisés** par le code
- ✅ Toutes les opérations CRUD passent par MySQL
- ✅ La connexion est automatiquement réinitialisée si elle est fermée
- ✅ Les transactions sont gérées par MySQL

## 🎯 Prochaines Étapes

1. ✅ Base de données créée
2. ✅ Connexion configurée
3. ✅ DAO migrés vers MySQL
4. ✅ Application liée à MySQL

**Le projet est maintenant entièrement connecté à MySQL !** 🎉


