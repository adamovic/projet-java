@echo off
echo Configuration temporaire de l'environnement...

REM Configuration des variables d'environnement
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set MAVEN_HOME=C:\tools\apache-maven-3.9.11
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo.
echo Compilation avec Maven...
cd /d "c:\Users\PROBOOK2\Desktop\projet_JAVA"

REM Nettoyage et compilation
mvn clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation reussie!
    echo Pour lancer l'application:
    echo mvn exec:java -Dexec.mainClass="com.university.library.ConsoleApp"
) else (
    echo Erreur lors de la compilation.
)

pause
