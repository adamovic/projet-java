@echo off
echo Compilation du projet JavaFX...

REM Création du répertoire de sortie
if not exist "target\classes" mkdir "target\classes"

REM Compilation des fichiers Java
echo Compilation des fichiers sources...
javac -d "target\classes" -cp "lib\*" src\main\java\com\university\library\*.java src\main\java\com\university\library\controllers\*.java src\main\java\com\university\library\models\*.java src\main\java\com\university\library\dao\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation réussie!
    echo.
    echo Pour exécuter l'application:
    echo java -cp "target\classes;lib\*" com.university.library.Main
) else (
    echo Erreur lors de la compilation.
)

pause
