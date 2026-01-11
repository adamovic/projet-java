@echo off
echo Compilation et execution de l'application JavaFX...

REM Configuration manuelle de l'environnement
set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.14"
set "MAVEN_HOME=%~dp0apache-maven-3.9.11"

echo Utilisation de Java: %JAVA_HOME%
echo Utilisation de Maven: %MAVEN_HOME%

REM Compilation avec Maven
"%MAVEN_HOME%\bin\mvn" -Djava.home="%JAVA_HOME%" clean compile

if %ERRORLEVEL% EQU 0 (
    echo Compilation reussie!
    echo.
    echo Lancement de l'application...
    "%MAVEN_HOME%\bin\mvn" -Djava.home="%JAVA_HOME%" javafx:run
) else (
    echo Erreur de compilation.
)

pause
