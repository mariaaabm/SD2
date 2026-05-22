# SportFlow - Arranque local (Windows PowerShell)
# Requisitos: Java 21+, Node.js 18+, MySQL 8.0+ em execucao
#
# Configurar MySQL (apenas na primeira vez, em WSL ou MySQL Shell):
#   mysql -u root -p < scripts\setup-mysql.sql

$root = Split-Path -Parent $PSScriptRoot

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "  SportFlow - A arrancar..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Pre-requisito: MySQL deve estar a correr com o utilizador 'sportflow'." -ForegroundColor Yellow
Write-Host "Se ainda nao configuraste: mysql -u root -p < scripts\setup-mysql.sql" -ForegroundColor Yellow
Write-Host ""

# ── Backend ──────────────────────────────────────────────────────────────────
Write-Host "[1/2] Backend (porta 8080, perfil dev + MySQL)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
  "Set-Location '$root\backend'; .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=dev'" `
  -WindowStyle Normal

# Aguarda o backend ficar disponivel (max 5 min)
Write-Host "      A aguardar backend (max 5 minutos -- primeira execucao descarrega dependencias)..." -ForegroundColor Yellow
$ready = $false
for ($i = 0; $i -lt 150; $i++) {
    Start-Sleep -Seconds 2
    try {
        $r = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        if ($r.Content -match '"status":"UP"') {
            $ready = $true
            break
        }
    } catch {}
    if (($i + 1) % 15 -eq 0) {
        Write-Host "  Decorridos $([int](($i+1)*2))s..." -ForegroundColor DarkGray
    }
}

if (-not $ready) {
    Write-Warning "Backend nao respondeu em 5 minutos."
    Write-Host "Verifica a janela do backend para erros de ligacao ao MySQL." -ForegroundColor Red
    Write-Host "Tenta: mysql -u sportflow -psportflow123 sports_store -e 'SELECT 1;'" -ForegroundColor Red
}

# ── Frontend ─────────────────────────────────────────────────────────────────
Write-Host "[2/2] Frontend (porta 5173)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
  "Set-Location '$root\frontend'; npm install; npm run dev" `
  -WindowStyle Normal

Start-Sleep -Seconds 3

# ── Info ─────────────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "  SportFlow disponivel em:" -ForegroundColor Green
Write-Host "  http://localhost:5173" -ForegroundColor Green
Write-Host ""
Write-Host "  Admin:   admin@store.test / password" -ForegroundColor Green
Write-Host "  Swagger: http://localhost:8080/swagger-ui.html" -ForegroundColor Green
Write-Host "  Ctrl+C para parar" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Start-Process "http://localhost:5173"
