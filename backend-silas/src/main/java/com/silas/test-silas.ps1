# test-silas.ps1
Write-Host "=== TEST SILAS API ===" -ForegroundColor Cyan

# 1. Verificar servicios
Write-Host "`n1. Servicios disponibles:" -ForegroundColor Yellow
curl.exe -s http://localhost:3000/api/services

# 2. Crear archivo JSON temporal
$jsonFile = "D:\projects_vue\temp-turno.json"
@"
{
    "serviceId": 1,
    "customerName": "Juan Pérez",
    "customerPhone": "3001234567",
    "customerEmail": "juan@email.com",
    "priority": "NORMAL"
}
"@ | Out-File -FilePath $jsonFile -Encoding UTF8 -Force

# 3. Crear turno
Write-Host "`n`n2. Creando turno..." -ForegroundColor Yellow
$response = curl.exe -s -X POST http://localhost:3000/api/turns `
  -H "Content-Type: application/json" `
  -d @$jsonFile
Write-Host $response -ForegroundColor Green

# 4. Ver turnos activos
Write-Host "`n3. Turnos activos:" -ForegroundColor Yellow
curl.exe -s http://localhost:3000/api/turns/active

# 5. Limpiar
Remove-Item $jsonFile -Force

Write-Host "`n=== Prueba completada ===" -ForegroundColor Cyan