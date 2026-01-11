# Guide de Compilation et d'Exécution

## Prérequis
1. **Java JDK 17+** requis pour JavaFX 17
2. **JavaFX SDK** à télécharger depuis https://gluonhq.com/products/javafx/

## Étapes d'installation

### 1. Installation de Java 17
```bash
# Télécharger JDK 17 depuis Oracle ou OpenJDK
# Installer et configurer JAVA_HOME
```

### 2. Téléchargement de JavaFX
1. Télécharger JavaFX SDK 17 depuis https://gluonhq.com/products/javafx/
2. Extraire dans un dossier `javafx-sdk-17` à la racine du projet

### 3. Configuration du projet
Créer un dossier `lib` et copier les JARs JavaFX :
```bash
mkdir lib
copy javafx-sdk-17\lib\*.jar lib\
```

### 4. Compilation
```bash
# Windows
compile.bat

# Ou manuellement :
javac -d target/classes -cp "lib/*" src/main/java/com/university/library/*.java src/main/java/com/university/library/controllers/*.java src/main/java/com/university/library/models/*.java src/main/java/com/university/library/dao/*.java
```

### 5. Exécution
```bash
# Windows
java -cp "target/classes;lib/*" --module-path lib --add-modules javafx.controls,javafx.fxml com.university.library.Main

# Ou avec le script run.bat
run.bat
```

## Alternative avec Maven
Si Maven est installé :
```bash
mvn clean compile
mvn javafx:run
```

## Structure attendue après compilation
```
projet_JAVA/
├── target/
│   └── classes/
│       └── com/university/library/
│           ├── Main.class
│           ├── controllers/
│           ├── models/
│           └── dao/
├── lib/
│   ├── javafx-controls.jar
│   ├── javafx-fxml.jar
│   ├── javafx-graphics.jar
│   └── javafx-base.jar
└── src/main/resources/
    └── views/
        ├── home.fxml
        ├── etudiant_view.fxml
        ├── livre_view.fxml
        └── emprunt_view.fxml
```

## Dépannage
- **Erreur "javac: command not found"**: Vérifier l'installation de JDK et le PATH
- **Erreur JavaFX**: S'assurer que les JARs JavaFX sont dans le dossier `lib`
- **Erreur FXML**: Vérifier que les fichiers FXML sont dans `src/main/resources/views/`
