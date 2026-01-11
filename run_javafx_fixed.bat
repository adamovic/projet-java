@echo off
echo Setting up environment for JavaFX application...

REM Set JAVA_HOME to Java 17
set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.14"
set "MAVEN_HOME=%~dp0apache-maven-3.9.11"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo JAVA_HOME: %JAVA_HOME%
echo.
echo Cleaning and compiling...
"%MAVEN_HOME%\bin\mvn" clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo Running JavaFX application...
    "%MAVEN_HOME%\bin\mvn" javafx:run
) else (
    echo Compilation failed.
)

pause
