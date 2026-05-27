# SportFlow — Loja Desportiva Online

Aplicação web full-stack para uma loja desportiva online, com autenticação JWT, gestão de encomendas, faturação e painel de administração.

---

## Ferramentas e Tecnologias Utilizadas

### Backend

| Ferramenta | Versão | Função 
|---|---|---|
| **Java** | 21 | Linguagem principal do backend |
| **Spring Boot** | 3.3.5 | Framework principal — simplifica a configuração e arranque da aplicação |
| **Spring Web** | — | Criação de endpoints REST (controllers, rotas HTTP) |
| **Spring Data JPA** | — | Abstração sobre a base de dados — mapeamento objeto-relacional (ORM) com Hibernate |
| **Spring Security** | — | Autenticação e autorização — controlo de acesso por roles (ADMIN / CLIENT) |
| **Spring Validation** | — | Validação automática dos dados recebidos nos pedidos (anotações `@Valid`, `@NotBlank`, etc.) |
| **Spring Actuator** | — | Endpoints de monitorização e saúde da aplicação (`/actuator/health`) |
| **Spring Mail** | — | Envio de e-mails (confirmações de encomenda, etc.) via SMTP |
| **JWT (JJWT 0.12.6)** | 0.12.6 | Geração e validação de tokens JWT para autenticação stateless |
| **MySQL** | 8.0 | Base de dados relacional principal em produção |
| **H2** | — | Base de dados em memória usada apenas nos testes automatizados |
| **Flyway** | — | Gestão de migrações da base de dados — garante que o schema está sempre atualizado |
| **Springdoc OpenAPI** | 2.6.0 | Geração automática da documentação da API (Swagger UI em `/swagger-ui.html`) |
| **BCrypt** | — | Algoritmo de hash para armazenar as passwords dos utilizadores de forma segura |
| **Maven** | — | Ferramenta de build e gestão de dependências do projeto Java |

### Frontend

| Ferramenta | Versão | Função |
|---|---|---|
| **React** | 18.3.1 | Biblioteca JavaScript para construção da interface (SPA) |
| **TypeScript** | 5.6.3 | Superset do JavaScript com tipagem estática — reduz erros em tempo de desenvolvimento |
| **Vite** | 5.4.10 | Bundler e servidor de desenvolvimento — build rápido do projeto frontend |
| **Axios** | — | Cliente HTTP para comunicação com a API REST do backend |

### Infraestrutura

| Ferramenta | Versão | Função |
|---|---|---|
| **Docker** | — | Containerização da aplicação — garante ambiente consistente em qualquer máquina |
| **Docker Compose** | — | Orquestração dos múltiplos containers (MySQL + Backend + Frontend) com um único ficheiro |
| **Nginx** | — | Servidor web que serve os ficheiros estáticos do frontend e faz proxy reverso (porta 3000) |

---

## Fluxo da Aplicação

```
┌─────────────────────────────────────────────────────────────────┐
│                        UTILIZADOR (Browser)                      │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP (porta 3000)
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Nginx (Frontend)                             │
│   Serve os ficheiros React compilados (HTML, CSS, JS)           │
│   Faz proxy reverso para o backend em /api/*                    │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP (porta 8080)
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Spring Boot (Backend)                           │
│                                                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              JwtAuthenticationFilter                       │  │
│  │   Lê o cookie HttpOnly, valida o JWT, injeta o utilizador │  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│  ┌───────────────────────▼───────────────────────────────────┐  │
│  │                   Spring Security                          │  │
│  │   Verifica se o utilizador tem permissão para o endpoint  │  │
│  │   PUBLIC / AUTHENTICATED / ADMIN                          │  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│  ┌───────────────────────▼───────────────────────────────────┐  │
│  │                    Controllers (REST)                      │  │
│  │   AuthController · ProductController · SaleController     │  │
│  │   ReviewController · WishlistController · StatsController │  │
│  │   CategoryController · InvoiceController · AdminController│  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│  ┌───────────────────────▼───────────────────────────────────┐  │
│  │                      Services                              │  │
│  │   Lógica de negócio: cálculo de totais, IVA, faturação,  │  │
│  │   stocks, geração de número de fatura, envio de e-mails  │  │
│  └───────────────────────┬───────────────────────────────────┘  │
│                          │                                       │
│  ┌───────────────────────▼───────────────────────────────────┐  │
│  │              Spring Data JPA (Repositories)                │  │
│  │   Abstração sobre o MySQL — queries automáticas e JPQL    │  │
│  └───────────────────────┬───────────────────────────────────┘  │
└──────────────────────────┼──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                     MySQL 8.0                                    │
│   Base de dados relacional gerida via Flyway (migrações)        │
└─────────────────────────────────────────────────────────────────┘
```

### Fluxo de Autenticação

```
Cliente                      Backend                        Base de Dados
  │                             │                                │
  │──── POST /api/auth/login ──►│                                │
  │     {email, password}       │──── SELECT customer ─────────►│
  │                             │◄─── customer row ─────────────│
  │                             │  verifica BCrypt hash          │
  │                             │  gera JWT (15 min)             │
  │                             │  gera RefreshToken (7 dias)   │
  │                             │──── INSERT refresh_tokens ───►│
  │◄─── Set-Cookie: jwt ───────│                                │
  │◄─── Set-Cookie: refresh ───│                                │
  │                             │                                │
  │ (pedidos seguintes)         │                                │
  │──── GET /api/sales ────────►│                                │
  │     Cookie: jwt=...         │  JwtAuthenticationFilter       │
  │                             │  valida JWT → extrai userId    │
  │                             │──── query BD ───────────────►│
  │◄─── 200 OK {sales} ────────│                                │
  │                             │                                │
  │ (token expirado)            │                                │
  │──── POST /api/auth/refresh ►│                                │
  │     Cookie: refresh=...     │──── SELECT refresh_token ────►│
  │                             │  valida → gera novo JWT        │
  │◄─── Set-Cookie: jwt (novo) │                                │
```

### Fluxo de Encomenda (Checkout)

```
1. Cliente adiciona produtos ao carrinho (frontend, sem BD)
2. POST /api/sales/checkout  →  SaleController
3. SaleService:
   a. Verifica stock de cada produto
   b. Calcula subtotal, IVA (23%) e total
   c. Cria registo Sale + SaleItems
   d. Desconta stock de cada Product
   e. Cria Invoice com número sequencial (ex: SP-2024-0001)
   f. Envia e-mail de confirmação ao cliente
4. Resposta: Sale com Invoice incluída
```

---

## Tabelas da Base de Dados

### Diagrama de Relações

```
┌─────────────┐       ┌──────────────┐       ┌──────────────┐
│  categories │       │   products   │       │   reviews    │
│─────────────│       │──────────────│       │──────────────│
│ id (PK)     │◄──────│ category_id  │◄──────│ product_id   │
│ name        │  N:1  │ id (PK)      │  1:N  │ id (PK)      │
│ description │       │ name         │       │ customer_id  │◄──┐
└─────────────┘       │ description  │       │ rating       │   │
                      │ price        │       │ comment      │   │
                      │ stock        │       │ created_at   │   │
                      │ active       │       └──────────────┘   │
                      │ image_url    │                           │
                      └──────┬───────┘       ┌──────────────┐   │
                             │               │ wishlist_    │   │
                             │           1:N │ items        │   │
                             │               │──────────────│   │
                             │               │ id (PK)      │   │
                             │               │ customer_id  │◄──┤
                             │               │ product_id   │   │
                             │               │ created_at   │   │
                             │               └──────────────┘   │
                             │                                   │
┌────────────────┐           │  ┌─────────────────────────────┐ │
│   customers    │           │  │           sales              │ │
│────────────────│           │  │─────────────────────────────│ │
│ id (PK)        │◄──────────┼──│ customer_id (FK)            │ │
│ name           │  1:N      │  │ id (PK)                     │ │
│ email (unique) │           │  │ created_at                  │ │
│ password_hash  │           │  │ total                       │ │
│ role           │           │  │ subtotal                    │ │
│ created_at     │           │  │ vat_amount                  │ │
└────────┬───────┘           │  │ vat_rate                    │ │
         │                   │  │ status                      │ │
         │ 1:N               │  │ shipping_name               │ │
         ▼                   │  │ shipping_address            │ │
┌────────────────┐           │  │ shipping_city               │ │
│ refresh_tokens │           │  │ shipping_zip                │ │
│────────────────│           │  │ shipping_country            │ │
│ id (PK)        │           │  │ payment_method              │ │
│ customer_id(FK)│           │  └──────────┬──────────────────┘ │
│ token (unique) │           │             │                     │
│ expires_at     │           │             │ 1:N                 │
│ created_at     │           │             ▼                     │
└────────────────┘           │  ┌──────────────────────────┐    │
                             │  │       sale_items          │    │
                             └──│──────────────────────────│    │
                             FK │ id (PK)                   │    │
                                │ sale_id (FK)              │    │
                                │ product_id (FK)           │    │
                                │ quantity                  │    │
                                │ unit_price                │    │
                                └──────────────────────────┘    │
                                                                 │
┌────────────────────────────────────┐                          │
│             invoices               │                          │
│────────────────────────────────────│                          │
│ id (PK)                            │                          │
│ sale_id (FK, unique)               │                          │
│ invoice_number (unique)            │                          │
│ series                             │                          │
│ issued_at                          │                          │
│ operation_date                     │                          │
└────────────────────────────────────┘                          │
         ▲                                                       │
         └── customer_id referenciado indiretamente via sale ───┘
```

### Descrição Detalhada das Tabelas

#### `customers` — Utilizadores
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `name` | VARCHAR | Nome completo |
| `email` | VARCHAR UNIQUE | Email (usado no login) |
| `password_hash` | VARCHAR | Password encriptada com BCrypt |
| `role` | ENUM | `ADMIN` ou `CLIENT` |
| `created_at` | DATETIME | Data de registo |

---

#### `categories` — Categorias de Produto
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `name` | VARCHAR UNIQUE | Nome da categoria (ex: Futebol) |
| `description` | TEXT | Descrição da categoria |

---

#### `products` — Catálogo de Produtos
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `category_id` | BIGINT FK | Referência à categoria |
| `name` | VARCHAR | Nome do produto |
| `description` | TEXT | Descrição detalhada |
| `price` | DECIMAL | Preço unitário (sem IVA) |
| `stock` | INT | Quantidade disponível em armazém |
| `active` | BOOLEAN | Se o produto está visível na loja |
| `image_url` | VARCHAR | URL da imagem do produto |

---

#### `sales` — Encomendas
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `customer_id` | BIGINT FK | Cliente que fez a encomenda |
| `created_at` | DATETIME | Data e hora da encomenda |
| `subtotal` | DECIMAL | Total sem IVA |
| `vat_rate` | DECIMAL | Taxa de IVA aplicada (23%) |
| `vat_amount` | DECIMAL | Valor do IVA |
| `total` | DECIMAL | Total com IVA |
| `status` | ENUM | `CONFIRMED` `PROCESSING` `SHIPPED` `DELIVERED` `CANCELLED` |
| `shipping_name` | VARCHAR | Nome do destinatário |
| `shipping_address` | VARCHAR | Morada de entrega |
| `shipping_city` | VARCHAR | Cidade |
| `shipping_zip` | VARCHAR | Código postal |
| `shipping_country` | VARCHAR | País |
| `payment_method` | VARCHAR | Método de pagamento utilizado |

---

#### `sale_items` — Linhas de Encomenda
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `sale_id` | BIGINT FK | Encomenda associada |
| `product_id` | BIGINT FK | Produto encomendado |
| `quantity` | INT | Quantidade encomendada |
| `unit_price` | DECIMAL | Preço unitário no momento da compra (fixo) |

---

#### `invoices` — Faturas
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `sale_id` | BIGINT FK UNIQUE | Encomenda faturada (1 fatura por encomenda) |
| `invoice_number` | VARCHAR UNIQUE | Número único da fatura (ex: `SP-2024-0001`) |
| `series` | VARCHAR | Série da fatura (ex: `SP`) |
| `issued_at` | DATETIME | Data de emissão |
| `operation_date` | DATE | Data da operação |

---

#### `reviews` — Avaliações de Produto
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `customer_id` | BIGINT FK | Cliente que avaliou |
| `product_id` | BIGINT FK | Produto avaliado |
| `rating` | TINYINT | Nota de 1 a 5 |
| `comment` | TEXT | Comentário escrito |
| `created_at` | DATETIME | Data da avaliação |
| — | UNIQUE | `(customer_id, product_id)` — 1 avaliação por cliente por produto |

---

#### `wishlist_items` — Lista de Desejos
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `customer_id` | BIGINT FK | Cliente dono da lista |
| `product_id` | BIGINT FK | Produto guardado |
| `created_at` | DATETIME | Data em que foi adicionado |
| — | UNIQUE | `(customer_id, product_id)` — sem duplicados |

---

#### `refresh_tokens` — Tokens de Sessão
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | BIGINT PK | Identificador único |
| `customer_id` | BIGINT FK | Utilizador da sessão |
| `token` | VARCHAR(512) UNIQUE | Token aleatório seguro |
| `expires_at` | DATETIME | Data de expiração (7 dias por defeito) |
| `created_at` | DATETIME | Data de criação |

---

## Arranque do Projeto

```bash
# Com Docker (recomendado)
docker compose up --build

# Sem Docker
# 1. Iniciar MySQL e criar base de dados 'sports_store'
# 2. Backend
cd backend && mvn spring-boot:run

# 3. Frontend
cd frontend && npm install && npm run dev
```

| Serviço | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Actuator Health | http://localhost:8080/actuator/health |
