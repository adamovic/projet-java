@echo off
echo Test de connexion a MySQL...
echo.
mvn exec:java -Dexec.mainClass="com.university.library.dao.TestConnection"
pause

