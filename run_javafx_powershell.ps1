# Script PowerShell pour exécuter l'application JavaFX
Write-Host "Configuration de l'environnement JavaFX..." -ForegroundColor Green

# Configuration des variables d'environnement
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.14"
$env:MAVEN_HOME = ".\apache-maven-3.9.11"

# Forcer JAVA_HOME dans les variables système
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $env:JAVA_HOME, "Process")

# Ajouter au PATH
$env:PATH = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:PATH"

Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "MAVEN_HOME: $env:MAVEN_HOME"

# Vérification de Java
Write-Host "`nVérification de Java:" -ForegroundColor Yellow
java -version

# Compilation avec Maven
Write-Host "`nCompilation du projet..." -ForegroundColor Yellow
try {
    mvn clean compile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation réussie!" -ForegroundColor Green
        Write-Host "`nLancement de l'application JavaFX..." -ForegroundColor Yellow
        mvn javafx:run
    } else {
        Write-Host "Échec de la compilation." -ForegroundColor Red
    }
} catch {
    Write-Host "Erreur lors de la compilation: $_" -ForegroundColor Red
}

Read-Host "Appuyez sur Entrée pour quitter"
