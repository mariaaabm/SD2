#!/bin/bash
# SportFlow — arranque automatico (WSL/Linux)

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_LOG="/tmp/sportflow-backend.log"
BACKEND_PID_FILE="/tmp/sportflow-backend.pid"

# ── Limpeza ao sair (Ctrl+C) ─────────────────────────────────
cleanup() {
  echo ""
  echo "A parar SportFlow..."
  if [ -f "$BACKEND_PID_FILE" ]; then
    kill "$(cat "$BACKEND_PID_FILE")" 2>/dev/null
    rm -f "$BACKEND_PID_FILE"
  fi
  exit 0
}
trap cleanup INT TERM

# ── Matar processos antigos nas portas ────────────────────────
kill_port() {
  local port=$1
  local pid
  pid=$(lsof -ti tcp:"$port" 2>/dev/null)
  if [ -n "$pid" ]; then
    echo "A libertar porta $port (PID $pid)..."
    kill "$pid" 2>/dev/null
    sleep 1
  fi
}

kill_port 8080
kill_port 5173

echo "========================================"
echo "  SportFlow — Iniciando..."
echo "========================================"
echo ""

# ── Backend ───────────────────────────────────────────────────
echo "[1/3] A arrancar backend (porta 8080)..."
cd "$ROOT/backend"
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo > "$BACKEND_LOG" 2>&1 &
echo $! > "$BACKEND_PID_FILE"

# ── Aguardar backend ──────────────────────────────────────────
echo "[2/3] A aguardar backend ficar pronto..."
READY=false
for i in $(seq 1 45); do
  sleep 2
  if curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q '"UP"'; then
    READY=true
    break
  fi
  printf "  %ds...\r" $((i * 2))
done

if [ "$READY" = false ]; then
  echo ""
  echo "AVISO: Backend nao respondeu em 90s."
  echo "Verifica os logs: tail -f $BACKEND_LOG"
else
  echo ""
  echo "  Backend OK!"
fi

# ── Frontend ──────────────────────────────────────────────────
echo "[3/3] A arrancar frontend (porta 5173)..."
cd "$ROOT/frontend"

if [ ! -d node_modules ]; then
  echo "  A instalar dependencias npm..."
  npm install --silent
fi

echo ""
echo "========================================"
echo "  SportFlow disponivel em:"
echo "  http://localhost:5173"
echo ""
echo "  Admin: admin@store.test / password"
echo "  Swagger: http://localhost:8080/swagger-ui.html"
echo "  Backend logs: tail -f $BACKEND_LOG"
echo "  Ctrl+C para parar tudo"
echo "========================================"
echo ""

npm run dev
