# Script PowerShell pour démarrer le serveur web

Write-Host "Démarrage du serveur web pour la Bibliothèque Universitaire..." -ForegroundColor Green
Write-Host ""
Write-Host "Le serveur va démarrer sur http://localhost:3000" -ForegroundColor Yellow
Write-Host "Ouvrez votre navigateur et allez sur cette adresse." -ForegroundColor Yellow
Write-Host ""
Write-Host "Appuyez sur Ctrl+C pour arrêter le serveur." -ForegroundColor Yellow
Write-Host ""

# Vérifier si Node.js est installé
try {
    $nodeVersion = node --version
    Write-Host "Node.js version: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "Node.js n'est pas installé. Veuillez installer Node.js depuis https://nodejs.org/" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour quitter"
    exit 1
}

# Démarrer le serveur
try {
    Write-Host "Démarrage du serveur..." -ForegroundColor Blue
    node server.js
} catch {
    Write-Host "Erreur lors du démarrage du serveur: $_" -ForegroundColor Red
    Read-Host "Appuyez sur Entrée pour quitter"
}
