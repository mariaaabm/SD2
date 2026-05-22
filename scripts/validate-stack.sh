#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT_DIR"

if ! docker --version >/dev/null 2>&1; then
  echo "ERRO: docker nao esta disponivel."
  echo "Se estiveres em WSL, ativa Docker Desktop -> Settings -> Resources -> WSL Integration."
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "ERRO: docker compose nao esta disponivel."
  exit 1
fi

echo "A arrancar stack Docker..."
docker compose -f infra/docker-compose.yml up --build -d

echo "A aguardar backend..."
for attempt in {1..40}; do
  if curl -fsS http://localhost:8080/actuator/health >/tmp/sports-store-health.json; then
    cat /tmp/sports-store-health.json
    echo
    break
  fi

  if [ "$attempt" -eq 40 ]; then
    echo "ERRO: backend nao ficou saudavel a tempo."
    docker compose -f infra/docker-compose.yml logs backend
    exit 1
  fi

  sleep 2
done

echo "A validar Swagger..."
curl -fsS http://localhost:8080/v3/api-docs >/dev/null

echo "A validar frontend..."
curl -fsS http://localhost:5173 >/dev/null

echo "A validar login admin..."
curl -fsS \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@store.test","password":"password"}' \
  http://localhost:8080/api/auth/login >/tmp/sports-store-login.json

echo "Stack validada com sucesso."
echo "Frontend: http://localhost:5173"
echo "Backend health: http://localhost:8080/actuator/health"
echo "Swagger: http://localhost:8080/swagger-ui.html"
