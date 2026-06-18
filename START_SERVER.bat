@echo off
echo ==========================================
echo   QLSV SERVER - TCP + SUPABASE
echo ==========================================
echo.
echo Server se tao schema va nap du lieu mau len Supabase neu thieu.
echo TCP port: 9000
echo Day la TCP backend cho JavaFX desktop.
echo.
set JAVA_HOME=C:\Program Files\Java\jdk-24
call mvnw -Pserver compile exec:java
pause
