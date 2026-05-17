# Shopping Food Store

Aplicacao web para uma loja online de produtos alimentares, desenvolvida para a unidade curricular de Sistemas Distribuidos.

## Estrutura

```text
shopping-food-store/
├─ backend/      # Spring Boot REST API
├─ frontend/     # React web app
├─ infra/        # Docker Compose para desenvolvimento local
├─ docs/         # documentacao tecnica auxiliar
├─ report/       # relatorio em LaTeX
└─ slides/       # apresentacao
```

## Requisitos

- Java 21
- Maven 3.9+
- Node.js 20+
- Docker e Docker Compose

## Execucao local

### Com PostgreSQL

```bash
docker compose -f infra/docker-compose.yml up -d db
```

```bash
cd backend
./mvnw spring-boot:run
```

```bash
cd frontend
npm install
npm run dev
```

### Demo sem Docker

Para validar a aplicacao sem PostgreSQL/Docker, usa o perfil `demo` com H2 em memoria:

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Depois, noutro terminal:

```bash
cd frontend
npm run dev
```

Guiao completo: `docs/DEMO.md`.

Por omissao:

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:5173`
- Base de dados: `localhost:5432`

## Funcionalidades disponiveis

- Catalogo publico com filtro por categoria.
- Registo e login de clientes.
- Carrinho e checkout para clientes autenticados.
- Historico de compras e faturas.
- Gestao admin de produtos e categorias.
- Estatisticas admin de vendas, clientes e faturacao.

## Testes

Backend:

```bash
cd backend
./mvnw test
```

Frontend:

```bash
cd frontend
npm run build
```

## Execucao com Docker

Quando o Docker estiver disponivel:

```bash
docker compose -f infra/docker-compose.yml up --build
```

Verificacoes:

- Health check do backend: `http://localhost:8080/actuator/health`
- Swagger/OpenAPI: `http://localhost:8080/swagger-ui.html`
- Frontend: `http://localhost:5173`

Se estiveres em WSL e o comando `docker` nao existir, ativa a integracao no Docker Desktop:

```text
Docker Desktop -> Settings -> Resources -> WSL Integration -> ativar a distro usada
```

Depois reinicia o terminal WSL e confirma:

```bash
docker --version
docker compose version
```

Tambem podes correr a validacao automatica da stack:

```bash
./scripts/validate-stack.sh
```

Este script arranca os containers e confirma:

- health check do backend;
- OpenAPI em `/v3/api-docs`;
- frontend;
- login admin.
