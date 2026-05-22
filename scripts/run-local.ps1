# SportFlow - Arranque local sem Docker
# Requisitos: Java 21+, Node.js 20+
# Backend usa H2 em memoria (perfil demo) -- nao precisa de PostgreSQL

$root = Split-Path -Parent $PSScriptRoot

# ── Backend ──────────────────────────────────────────────────────────────────
Write-Host "A arrancar backend (perfil demo, porta 8080)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
  "Set-Location '$root\backend'; .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=demo" `
  -WindowStyle Normal

# Aguarda o backend ficar disponivel (max 60 s)
Write-Host "A aguardar o backend ficar pronto..." -ForegroundColor Yellow
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 2
    try {
        $r = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        if ($r.Content -match '"status":"UP"') {
            $ready = $true
            break
        }
    } catch {}
    Write-Host "  ($([int](($i+1)*2))s)..." -ForegroundColor DarkGray
}

if (-not $ready) {
    Write-Warning "Backend nao respondeu em 60 s. Verifica a janela do backend."
    Write-Host "Podes continuar na mesma -- o frontend vai tentar ligar quando o backend arrancar."
}

# ── Frontend ─────────────────────────────────────────────────────────────────
Write-Host "A instalar dependencias e arrancar frontend (porta 5173)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
  "Set-Location '$root\frontend'; npm install; npm run dev" `
  -WindowStyle Normal

Start-Sleep -Seconds 3

# ── Abrir browser ────────────────────────────────────────────────────────────
Write-Host ""
Write-Host "SportFlow disponivel em http://localhost:5173" -ForegroundColor Green
Write-Host "  Admin:   admin@store.test / password" -ForegroundColor Green
Write-Host "  Swagger: http://localhost:8080/swagger-ui.html" -ForegroundColor Green
Write-Host ""
Start-Process "http://localhost:5173"
