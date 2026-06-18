@echo off
echo ==========================================================
echo   CONVERTER: REMOVING ILLEGAL UTF-8 BOM CHARACTERS
echo ==========================================================
echo.
echo Java compiler does not accept UTF-8 BOM (Byte Order Mark).
echo This script will strip the BOM from all source files.
echo.

powershell -ExecutionPolicy Bypass -Command "$utf8NoBom = New-Object System.Text.UTF8Encoding($false); Get-ChildItem -Path . -Recurse -File -Include *.java,*.xml,*.properties,*.bat,*.fxml | ForEach-Object { $content = [System.IO.File]::ReadAllText($_.FullName); [System.IO.File]::WriteAllText($_.FullName, $content, $utf8NoBom); Write-Host 'Loai bo BOM thanh cong tu:' $_.Name }"

echo.
echo ==========================================================
echo   DA LOAI BO BOM - CO THE BIEN DICH DU AN NGAY!
echo ==========================================================
pause
