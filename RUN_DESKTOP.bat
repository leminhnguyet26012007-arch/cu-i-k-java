@echo off
echo ==========================================
echo   QLSV CLIENT - JAVAFX DESKTOP
echo ==========================================
echo.
echo Dang mo JavaFX client...
echo.
set JAVA_HOME=C:\Program Files\Java\jdk-24
call mvnw -Pclient compile exec:java
pause
