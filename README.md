# SportFlow — Loja Desportiva Online

Aplicação web de e-commerce desportivo desenvolvida no âmbito da unidade curricular de **Sistemas Distribuídos** da Universidade da Beira Interior.

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Frontend | React 18 + TypeScript + Vite |
| Backend | Spring Boot 3.3 + Java 21 |
| Base de dados | MySQL 8.0 |
| Autenticação | JWT (jjwt 0.12) |
| Migrações BD | Flyway 10 |
| API Docs | OpenAPI 3 / Swagger UI |
| Estilos | CSS personalizado (inspirado Decathlon) |

---

## Funcionalidades

- Catálogo de produtos com pesquisa e filtro por categoria
- Registo e autenticação de utilizadores (JWT)
- Carrinho de compras persistente (localStorage)
- Checkout e geração automática de fatura
- Histórico de compras e visualização de faturas
- Painel de administração (produtos, categorias, vendas, estatísticas)
- Imagens dos produtos via Unsplash CDN
- Swagger UI para explorar a API REST

---

## Pré-requisitos

Instala as seguintes ferramentas antes de começar:

| Ferramenta | Versão mínima | Instalar (Ubuntu/WSL) |
|-----------|--------------|----------------------|
| Java (JDK) | 21 | `sudo apt install openjdk-21-jdk` |
| Node.js | 18 | `sudo apt install nodejs npm` |
| MySQL | 8.0 | `sudo apt install mysql-server` |

---

## Instalação e Configuração

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd SD2
```

### 2. Configurar o MySQL

#### 2.1 Arrancar o MySQL

```bash
sudo service mysql start
```

#### 2.2 Criar a base de dados e utilizador

```bash
mysql -u root -p < scripts/setup-mysql.sql
```

Este script cria:
- Base de dados: `sports_store`
- Utilizador: `sportflow` / `sportflow123`

> **Nota:** Se o teu MySQL root não tiver password, usa `mysql -u root < scripts/setup-mysql.sql` (sem `-p`).

#### 2.3 Verificar a ligação (opcional)

```bash
mysql -u sportflow -psportflow123 sports_store -e "SELECT 'Ligacao OK';"
```

---

## Executar o Projeto

### Opção A — Script automático (recomendado, WSL/Linux)

```bash
chmod +x start.sh
./start.sh
```

O script:
1. Verifica Java, Node e MySQL
2. Arranca o backend (Spring Boot) com perfil `dev`
3. Aguarda o backend estar pronto (até 5 minutos — primeira execução demora pois Maven descarrega dependências)
4. Arranca o frontend (Vite)

Abre o browser em: **http://localhost:5173**

---

### Opção B — Manual (dois terminais)

**Terminal 1 — Backend:**
```bash
cd backend
chmod +x mvnw
bash mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Terminal 2 — Frontend** (após o backend estar pronto):
```bash
cd frontend
npm install
npm run dev
```

---

### Opção C — PowerShell (Windows nativo)

**Terminal 1 — Backend:**
```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

**Terminal 2 — Frontend:**
```powershell
cd frontend
npm install
npm run dev
```

---

## Credenciais de Teste

| Utilizador | Email | Password | Papel |
|-----------|-------|----------|-------|
| Administrador | `admin@store.test` | `password` | ADMIN |

O administrador tem acesso ao painel em `/admin`.

---

## Endereços

| Serviço | URL |
|---------|-----|
| Loja (frontend) | http://localhost:5173 |
| API REST (backend) | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health check | http://localhost:8080/actuator/health |

---

## API REST — Resumo

### Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/register` | Criar conta | Não |
| POST | `/api/auth/login` | Iniciar sessão | Não |
| GET | `/api/auth/me` | Perfil do utilizador | Sim |

### Produtos

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/products` | Listar produtos | Não |
| GET | `/api/products/{id}` | Detalhe de produto | Não |
| POST | `/api/products` | Criar produto | Admin |
| PUT | `/api/products/{id}` | Editar produto | Admin |
| DELETE | `/api/products/{id}` | Eliminar produto | Admin |

**Parâmetros de pesquisa** (`GET /api/products`):
- `categoryId` — filtrar por categoria
- `activeOnly=true` — só produtos disponíveis
- `search` — pesquisa por nome/descrição

### Categorias

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/categories` | Listar categorias | Não |
| POST | `/api/categories` | Criar categoria | Admin |
| PUT | `/api/categories/{id}` | Editar categoria | Admin |

### Vendas

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/sales` | Criar venda (checkout) | Sim |
| GET | `/api/sales/my` | Minhas compras | Sim |
| GET | `/api/sales/{id}` | Detalhe de venda | Sim |
| GET | `/api/sales/{id}/invoice` | Fatura da venda | Sim |

### Administração

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/sales` | Todas as vendas | Admin |
| GET | `/api/stats/summary` | Estatísticas gerais | Admin |

---

## Estrutura do Projeto

```
SD2/
├── backend/                        # Spring Boot
│   ├── src/main/java/pt/ubi/gruposd/loja/
│   │   ├── config/                 # Security, OpenAPI
│   │   ├── controller/             # REST controllers
│   │   ├── dto/                    # Request/Response DTOs
│   │   ├── exception/              # Exceções + handler global
│   │   ├── model/                  # Entidades JPA
│   │   ├── repository/             # Spring Data JPA
│   │   ├── security/               # JWT filter + UserDetails
│   │   └── service/                # Lógica de negócio
│   ├── src/main/resources/
│   │   ├── db/migration/           # Migrações Flyway (V1–V4)
│   │   ├── application.yml         # Configuração base (MySQL)
│   │   └── application-dev.yml     # Configuração local dev
│   └── src/test/resources/
│       └── application-test.yml    # Testes com H2 (MySQL mode)
│
├── frontend/                       # React + TypeScript + Vite
│   └── src/
│       ├── api/                    # Axios client
│       ├── components/             # Header, Footer, ProductCard, ...
│       ├── contexts/               # AuthContext, CartContext
│       ├── pages/                  # HomePage, CatalogPage, ...
│       ├── services/               # product, auth, sale services
│       ├── styles/                 # global.css
│       └── utils/                  # categoryUtils
│
├── scripts/
│   ├── setup-mysql.sql             # Criar BD e utilizador MySQL
│   └── run-local.ps1               # Script PowerShell (Windows)
│
├── start.sh                        # Script de arranque (WSL/Linux)
└── README.md
```

---

## Base de Dados

### Esquema

```
customers ──< sales ──< sale_items >── products >── categories
                 └──── invoices
```

### Migrações Flyway

| Versão | Ficheiro | Descrição |
|--------|----------|-----------|
| V1 | `V1__init.sql` | Schema completo (6 tabelas) |
| V2 | `V2__seed.sql` | 4 categorias base + admin + 4 produtos |
| V3 | `V3__demo_data.sql` | +3 categorias + 23 produtos desportivos |
| V4 | `V4__add_image_url.sql` | Coluna `image_url` + URLs Unsplash |

---

## Resolução de Problemas

### Backend não arranca

```bash
# Ver logs completos
cat /tmp/sportflow-backend.log

# Verificar MySQL
sudo service mysql status
sudo service mysql start

# Verificar utilizador MySQL
mysql -u sportflow -psportflow123 sports_store -e "SELECT 1;"

# Recriar utilizador se necessário
mysql -u root -p < scripts/setup-mysql.sql
```

### Erro "Unknown database" ou "Access denied"

```bash
mysql -u root -p < scripts/setup-mysql.sql
```

### Porta 8080 ou 5173 ocupada

```bash
# Ver o que está na porta
lsof -ti tcp:8080 | xargs kill -9
lsof -ti tcp:5173 | xargs kill -9
```

### Primeira execução demora muito

Na primeira vez, Maven descarrega todas as dependências (~200 MB). Aguarda os 5 minutos do script ou acompanha com:

```bash
tail -f /tmp/sportflow-backend.log
```

---

## Desenvolvimento

### Testes do backend

```bash
cd backend
bash mvnw test
```

Os testes usam H2 em modo MySQL (sem precisar de MySQL externo).

### Build de produção do frontend

```bash
cd frontend
npm run build
```

---

*Universidade da Beira Interior — Sistemas Distribuídos*
