@echo off
echo ==========================================
echo   NAP DU LIEU MAU LEN SUPABASE
echo ==========================================
set JAVA_HOME=C:\Program Files\Java\jdk-24
call mvnw -Pseed compile exec:java
pause
