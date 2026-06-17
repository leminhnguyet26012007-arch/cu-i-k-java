@echo off
echo ==========================================
echo   KIEM TRA DU LIEU SUPABASE
echo ==========================================
set JAVA_HOME=C:\Program Files\Java\jdk-24
call mvnw -Pverify-data compile exec:java
pause
