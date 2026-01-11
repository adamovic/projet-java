@echo off
echo Configuration de l'environnement Maven...

REM Configuration des variables d'environnement
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set MAVEN_HOME=C:\tools\apache-maven-3.9.11
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

REM Affichage de la configuration
echo JAVA_HOME=%JAVA_HOME%
echo MAVEN_HOME=%MAVEN_HOME%
echo.

REM Test de Maven
echo Test de Maven:
mvn --version

echo.
echo Configuration terminee!
echo Pour rendre cette configuration permanente, ajoutez ces variables dans:
echo - Panneau de configuration > System > Advanced system settings > Environment Variables
pause
