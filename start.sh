#!/bin/bash
# SportFlow — Arranque automático (WSL / Linux)
# Pré-requisitos: Java 21+, Node 18+, MySQL 8.0+ em execução

set -uo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_LOG="/tmp/sportflow-backend.log"
BACKEND_PID_FILE="/tmp/sportflow-backend.pid"

# ── Cleanup ao sair (Ctrl+C) ─────────────────────────────────
cleanup() {
  echo ""
  echo "A parar SportFlow..."
  if [ -f "$BACKEND_PID_FILE" ]; then
    kill "$(cat "$BACKEND_PID_FILE")" 2>/dev/null || true
    rm -f "$BACKEND_PID_FILE"
  fi
  exit 0
}
trap cleanup INT TERM

echo ""
echo "=========================================="
echo "  SportFlow — A verificar requisitos..."
echo "=========================================="
echo ""

# ── Verificar Java ─────────────────────────────────────────────
if ! command -v java &>/dev/null; then
  echo "ERRO: Java nao encontrado. Instala Java 21+:"
  echo "  sudo apt install openjdk-21-jdk"
  exit 1
fi
echo "  Java: OK ($(java -version 2>&1 | head -1))"

# ── Verificar Node ─────────────────────────────────────────────
if ! command -v node &>/dev/null; then
  echo "ERRO: Node.js nao encontrado. Instala Node 18+:"
  echo "  sudo apt install nodejs npm"
  exit 1
fi
echo "  Node: OK ($(node --version))"

# ── Verificar MySQL ────────────────────────────────────────────
echo ""
if mysql -u sportflow -psportflow123 sports_store -e "SELECT 1;" &>/dev/null; then
  echo "  MySQL: OK (utilizador 'sportflow' autenticado)"
else
  echo ""
  echo "ERRO: Nao foi possivel ligar ao MySQL com o utilizador 'sportflow'."
  echo ""
  echo "  Corre estes comandos e tenta novamente:"
  echo ""
  echo "    sudo service mysql start"
  echo "    mysql -u root < scripts/setup-mysql.sql"
  echo ""
  echo "  (Se o root tiver password: mysql -u root -p < scripts/setup-mysql.sql)"
  exit 1
fi

# ── Libertar portas ────────────────────────────────────────────
kill_port() {
  local port=$1
  if command -v lsof &>/dev/null; then
    local pid
    pid=$(lsof -ti tcp:"$port" 2>/dev/null || true)
    [ -n "$pid" ] && kill "$pid" 2>/dev/null && sleep 1 || true
  elif command -v fuser &>/dev/null; then
    fuser -k "${port}/tcp" 2>/dev/null || true
  fi
}

kill_port 8080
kill_port 5173

echo ""
echo "=========================================="
echo "  SportFlow — A arrancar..."
echo "=========================================="
echo ""

# ── Backend ───────────────────────────────────────────────────
echo "[1/3] Backend (porta 8080)..."
cd "$ROOT/backend"
# Corrigir line endings Windows (CRLF -> LF) para funcionar no WSL
sed -i 's/\r//' mvnw
chmod +x mvnw
bash mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="-Xms256m -Xmx512m" \
  > "$BACKEND_LOG" 2>&1 &
echo $! > "$BACKEND_PID_FILE"

# ── Aguardar backend (máx 5 min) ─────────────────────────────
echo "[2/3] A aguardar backend (maximo 5 minutos)..."
echo "      (primeira execucao pode demorar — Maven descarrega dependencias)"
READY=false
for i in $(seq 1 150); do
  sleep 2
  if curl -sf http://localhost:8080/actuator/health 2>/dev/null | grep -q '"UP"'; then
    READY=true
    break
  fi
  if [ $((i % 15)) -eq 0 ]; then
    echo "  Decorridos $((i * 2))s..."
  fi
  # Parar se o processo morreu
  if [ -f "$BACKEND_PID_FILE" ] && ! kill -0 "$(cat "$BACKEND_PID_FILE")" 2>/dev/null; then
    break
  fi
done

if [ "$READY" = false ]; then
  echo ""
  echo "ERRO: Backend nao respondeu. Ultimas linhas do log:"
  echo "----------------------------------------------"
  tail -30 "$BACKEND_LOG" 2>/dev/null || true
  echo "----------------------------------------------"
  echo ""
  echo "Verifica:"
  echo "  1. MySQL esta a correr:  sudo service mysql start"
  echo "  2. BD configurada:       mysql -u root -p < scripts/setup-mysql.sql"
  echo "  3. Log completo:         cat $BACKEND_LOG"
  exit 1
fi

echo "  Backend pronto!"

# ── Frontend ──────────────────────────────────────────────────
echo "[3/3] Frontend (porta 5173)..."
cd "$ROOT/frontend"

if [ ! -d node_modules ]; then
  echo "  A instalar dependencias npm (primeira vez)..."
  npm install --silent
fi

echo ""
echo "=========================================="
echo "  SportFlow disponivel em:"
echo "  http://localhost:5173"
echo ""
echo "  Admin: admin@store.test / password"
echo "  Swagger: http://localhost:8080/swagger-ui.html"
echo "  Ctrl+C para parar tudo"
echo "=========================================="
echo ""

npm run dev
