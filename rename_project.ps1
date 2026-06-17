# PowerShell script to rename packages and content from dean11 to dean12

$extensions = @(".java", ".xml", ".properties", ".bat", ".fxml", ".md", ".txt")

Get-ChildItem -Path . -Recurse -File | ForEach-Object {
    $file = $_
    $ext = $file.Extension
    if ($extensions -contains $ext) {
        try {
            $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::UTF8)
            if ($content.Contains("dean11")) {
                $content = $content.Replace("dean11", "dean12")
                [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.Encoding]::UTF8)
                Write-Host "Replaced dean11 in: $($file.FullName)"
            }
        } catch {
            try {
                $content = [System.IO.File]::ReadAllText($file.FullName, [System.Text.Encoding]::GetEncoding("ISO-8859-1"))
                if ($content.Contains("dean11")) {
                    $content = $content.Replace("dean11", "dean12")
                    [System.IO.File]::WriteAllText($file.FullName, $content, [System.Text.Encoding]::GetEncoding("ISO-8859-1"))
                    Write-Host "Replaced (latin1) in: $($file.FullName)"
                }
            } catch {
                Write-Host "Failed to process: $($file.FullName)"
            }
        }
    }
}

# Rename folders
$javaOld = "src/main/java/com/example/dean11"
if (Test-Path $javaOld) {
    Rename-Item -Path $javaOld -NewName "dean12"
    Write-Host "Renamed java directory to dean12"
}

$resOld = "src/main/resources/com/example/dean11"
if (Test-Path $resOld) {
    Rename-Item -Path $resOld -NewName "dean12"
    Write-Host "Renamed resources directory to dean12"
}
