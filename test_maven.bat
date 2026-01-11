@echo off
echo Test de configuration Maven...

REM Configuration des variables d'environnement pour cette session
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set MAVEN_HOME=C:\tools\apache-maven-3.9.11
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo JAVA_HOME: %JAVA_HOME%
echo MAVEN_HOME: %MAVEN_HOME%
echo.

echo Test de Maven:
mvn --version

echo.
echo Test de compilation du projet:
cd /d "c:\Users\PROBOOK2\Desktop\projet_JAVA"
mvn clean compile

pause
