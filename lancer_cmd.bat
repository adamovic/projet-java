@echo off
echo Ouverture de cmd.exe avec configuration Maven...

REM Configuration des variables d'environnement
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_202
set MAVEN_HOME=C:\tools\apache-maven-3.9.11
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo Variables configurees:
echo JAVA_HOME=%JAVA_HOME%
echo MAVEN_HOME=%MAVEN_HOME%
echo.

echo Navigation vers le projet...
cd /d "c:\Users\PROBOOK2\Desktop\projet_JAVA"

echo.
echo ========================================
echo Vous pouvez maintenant utiliser Maven:
echo mvn --version
echo mvn clean compile
echo mvn exec:java -Dexec.mainClass="com.university.library.ConsoleApp"
echo ========================================
echo.

cmd /k
