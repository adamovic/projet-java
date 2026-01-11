# Script PowerShell pour Maven
Write-Host "Configuration Maven pour PowerShell..." -ForegroundColor Green

# Configuration des variables d'environnement
$env:JAVA_HOME = "C:\Program Files\Java\jdk1.8.0_202"
$env:MAVEN_HOME = "C:\tools\apache-maven-3.9.11"
$env:PATH = "$env:JAVA_HOME\bin;$env:MAVEN_HOME\bin;$env:PATH"

Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "MAVEN_HOME: $env:MAVEN_HOME"
Write-Host ""

# Test de Maven
Write-Host "Test de Maven:" -ForegroundColor Yellow
& mvn --version

Write-Host ""
Write-Host "Navigation vers le projet..." -ForegroundColor Yellow
Set-Location "c:\Users\PROBOOK2\Desktop\projet_JAVA"

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Vous pouvez maintenant utiliser Maven:" -ForegroundColor Green
Write-Host "mvn --version" -ForegroundColor White
Write-Host "mvn clean compile" -ForegroundColor White
Write-Host "mvn exec:java -Dexec.mainClass=`"com.university.library.ConsoleApp`"" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Green
