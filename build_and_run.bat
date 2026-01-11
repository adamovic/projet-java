@echo off
echo Build and Run avec Maven...

REM Configuration des variables d'environnement
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set MAVEN_HOME=C:\tools\apache-maven-3.9.11
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

REM Navigation vers le projet
cd /d "c:\Users\PROBOOK2\Desktop\projet_JAVA"

echo.
echo 1. Nettoyage et compilation...
mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Erreur de compilation!
    pause
    exit /b 1
)

echo.
echo 2. Lancement de l'application...
mvn exec:java -Dexec.mainClass="com.university.library.ConsoleApp"

pause
