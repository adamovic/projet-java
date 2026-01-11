@echo off
echo Setting up environment with Java 17...

REM Find Java 17 installation
for /f "delims=" %%i in ('dir /b /s "C:\Program Files\Java\jdk-17*" 2^>nul') do set JAVA_HOME=%%i
if "%JAVA_HOME%"=="" (
    for /f "delims=" %%i in ('dir /b /s "C:\Program Files\Eclipse Adoptium\jdk-17*" 2^>nul') do set JAVA_HOME=%%i
)

if "%JAVA_HOME%"=="" (
    echo Java 17 not found. Please install JDK 17.
    pause
    exit /b 1
)

echo JAVA_HOME: %JAVA_HOME%
set MAVEN_HOME=%~dp0apache-maven-3.9.11
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

echo.
echo Running Maven clean compile...
mvn clean compile

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilation successful!
    echo Running the application...
    mvn javafx:run
) else (
    echo Compilation failed.
)

pause
