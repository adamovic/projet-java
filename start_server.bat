@echo off
echo Démarrage du serveur web pour la Bibliothèque Universitaire...
echo.
echo Le serveur va démarrer sur http://localhost:3000
echo Ouvrez votre navigateur et allez sur cette adresse.
echo.
echo Appuyez sur Ctrl+C pour arrêter le serveur.
echo.

REM Vérifier si Node.js est installé
node --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Node.js n'est pas installé. Veuillez installer Node.js depuis https://nodejs.org/
    pause
    exit /b 1
)

REM Démarrer le serveur
node server.js

pause
