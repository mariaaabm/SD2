# SportFlow

Aplicacao web para uma loja online de artigos desportivos, desenvolvida para a unidade curricular de Sistemas Distribuidos.

## Estrutura



olá mundo

```text
SD2/
├─ backend/      # Spring Boot REST API
├─ frontend/     # React web app (Vite + TypeScript)
├─ infra/        # Docker Compose
├─ scripts/      # Scripts de arranque e validacao
├─ docs/         # Documentacao tecnica auxiliar
├─ report/       # Relatorio em LaTeX
└─ slides/       # Apresentacao
```

## Requisitos

| Ferramenta | Versao minima |
|---|---|
| Java | 21 |
| Node.js | 20 |
| Docker + Docker Compose | qualquer versao recente (opcional) |

---

## Execucao sem Docker (recomendado para desenvolvimento)

### Arranque automatico (Windows)

```powershell
.\scripts\run-local.ps1
```

O script abre duas janelas PowerShell (backend + frontend) e o browser em `http://localhost:5173`.

### Arranque manual

**Terminal 1 - Backend** (usa H2 em memoria, sem necessidade de PostgreSQL):

```powershell
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=demo
```

**Terminal 2 - Frontend**:

```powershell
cd frontend
npm install
npm run dev
```

Aceder em: `http://localhost:5173`

**Credenciais de demo:**

| Campo | Valor |
|---|---|
| Email | admin@store.test |
| Password | password |

---

## Execucao com Docker

```powershell
docker compose -f infra/docker-compose.yml up --build
```

| Servico | URL |
|---|---|
| Frontend | http://localhost:5173 |
| Backend health | http://localhost:8080/actuator/health |
| Swagger | http://localhost:8080/swagger-ui.html |

> Em WSL, activa a integracao no Docker Desktop: Settings > Resources > WSL Integration

---

## Funcionalidades

- Catalogo publico com filtro por categoria desportiva
- Registo e login de clientes
- Carrinho e checkout
- Historico de compras e faturas
- Gestao admin de produtos, categorias e vendas
- Estatisticas admin (faturacao, produtos, clientes)

## Testes

```powershell
cd backend
.\mvnw.cmd test
```

## Validacao da stack Docker

```bash
./scripts/validate-stack.sh
./scripts/validate-demo-api.sh
```
