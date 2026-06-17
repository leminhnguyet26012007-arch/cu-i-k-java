@echo off
echo ==========================================
echo   QLSV SERVER - TCP + SUPABASE
echo ==========================================
echo.
echo Server se tao schema va nap du lieu mau len Supabase neu thieu.
echo TCP port: 9000
echo Web port: 8081
echo.
set JAVA_HOME=C:\Program Files\Java\jdk-24
call mvnw spring-boot:run
pause
