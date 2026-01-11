@echo off
echo Creation de la base de donnees bibliotheque_db...
echo.

REM Ajustez le chemin selon votre installation XAMPP
set MYSQL_PATH=C:\xampp\mysql\bin\mysql.exe

if not exist "%MYSQL_PATH%" (
    echo Erreur: MySQL non trouve dans %MYSQL_PATH%
    echo Veuillez ajuster le chemin dans ce fichier.
    pause
    exit /b 1
)

"%MYSQL_PATH%" -u root -p < src\main\resources\database.sql

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Base de donnees creee avec succes!
) else (
    echo.
    echo Erreur lors de la creation de la base de donnees.
)

pause

