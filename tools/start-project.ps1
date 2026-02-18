# Configurar Java 17
$env:JAVA_HOME = "D:\java_files\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Configurar Maven
$env:MAVEN_HOME = "D:\tools\apache-maven-3.9.12"
$env:PATH = "$env:MAVEN_HOME\bin;$env:PATH"

Write-Host "✅ Java 17 configurado: $env:JAVA_HOME" -ForegroundColor Green
Write-Host "✅ Maven configurado: $env:MAVEN_HOME" -ForegroundColor Green
Write-Host ""
Write-Host "Comandos disponibles:" -ForegroundColor Yellow
Write-Host "  mvn spring-boot:run  - Ejecutar backend" -ForegroundColor Cyan
Write-Host "  cd D:\projects_vue\frontend-silas" -ForegroundColor Cyan
Write-Host "  npm run dev          - Ejecutar frontend" -ForegroundColor Cyan


# Ahora sí, desde cualquier ubicación USAR ESTE COMANDO PARA ARRANCAR EL BACKEND:
#. D:\tools\start-project.ps1

#USAR ESTE:
#mvn spring-boot:run