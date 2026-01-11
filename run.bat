@echo off
echo Lancement de l'application Bibliothèque Universitaire...

REM Vérification des classes compilées
if not exist "target\classes\com\university\library\Main.class" (
    echo Les classes ne sont pas compilées. Lancement de la compilation...
    call compile.bat
)

REM Lancement de l'application
echo Démarrage de l'application...
java -cp "target\classes;lib\*" --module-path lib --add-modules javafx.controls,javafx.fxml com.university.library.Main

if %ERRORLEVEL% NEQ 0 (
    echo Erreur lors de l'exécution.
    echo Vérifiez que:
    echo 1. Java 17+ est installé
    echo 2. Les JARs JavaFX sont dans le dossier lib/
    echo 3. Les fichiers FXML sont dans src/main/resources/views/
)

pause
