@echo off
echo Compilation directe avec javac...

set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.14"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Java:
java -version

echo.
echo Compilation des fichiers sources...
mkdir target\classes 2>nul

REM Compilation des fichiers Java
"%JAVA_HOME%\bin\javac" -d target\classes -cp "lib\*" src\main\java\com\university\library\*.java src\main\java\com\university\library\controllers\*.java src\main\java\com\university\library\models\*.java src\main\java\com\university\library\dao\*.java src\main\java\com\university\library\services\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation réussie!
    echo.
    echo Lancement de l'application...
    "%JAVA_HOME%\bin\java" -cp "target\classes;lib\*" com.university.library.Main
) else (
    echo Erreur de compilation.
)

pause
