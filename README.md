# SportFlow — Loja Desportiva Online

Aplicação web de e-commerce desportivo desenvolvida no âmbito da unidade curricular de **Sistemas Distribuídos** da Universidade da Beira Interior (UBI). Implementa uma loja online completa com catálogo de produtos, autenticação segura, carrinho de compras, checkout, sistema de avaliações, lista de desejos e painel de administração com estatísticas.

---

## Índice

1. [Visão Geral](#visão-geral)
2. [Stack Tecnológico](#stack-tecnológico)
3. [Arquitetura do Sistema](#arquitetura-do-sistema)
4. [Funcionalidades](#funcionalidades)
5. [Base de Dados](#base-de-dados)
6. [API REST](#api-rest)
7. [Frontend](#frontend)
8. [Segurança e Autenticação](#segurança-e-autenticação)
9. [Execução do Projeto](#execução-do-projeto)
10. [Docker](#docker)
11. [Testes](#testes)
12. [CI/CD — GitHub Actions](#cicd--github-actions)
13. [Estrutura de Ficheiros](#estrutura-de-ficheiros)
14. [Resolução de Problemas](#resolução-de-problemas)

---

## Visão Geral

O **SportFlow** é uma aplicação distribuída composta por três serviços independentes:

| Serviço | Tecnologia | Porta |
|---------|-----------|-------|
| **Frontend** | React 18 + TypeScript + Vite | 5173 (dev) / 3000 (Docker) |
| **Backend** | Spring Boot 3.3.5 + Java 21 | 8080 |
| **Base de Dados** | MySQL 8.0 | 3306 |

O frontend comunica exclusivamente com o backend via API REST. O backend gere toda a lógica de negócio e comunica com a base de dados MySQL. As migrações de esquema são geridas automaticamente pelo Flyway.

---

## Stack Tecnológico

### Backend

| Tecnologia | Versão | Função no Projeto |
|-----------|--------|-------------------|
| **Java** | 21 | Linguagem principal do backend |
| **Spring Boot** | 3.3.5 | Framework que auto-configura o servidor, segurança, JPA, etc. Evita configuração manual de XML |
| **Spring MVC** | (incluído) | Transforma classes Java em endpoints REST com anotações (`@RestController`, `@GetMapping`, etc.) |
| **Spring Data JPA** | (incluído) | Abstração sobre o Hibernate; permite criar queries com métodos como `findByEmail()` sem escrever SQL |
| **Hibernate** | 6.5.3 | ORM (Object-Relational Mapping) — mapeia entidades Java para tabelas MySQL |
| **Spring Security** | 6.3.4 | Gestão de autenticação e autorização. Protege endpoints com roles (ADMIN/CLIENT) |
| **jjwt** | 0.12.6 | Biblioteca para gerar e validar JSON Web Tokens (JWT) |
| **Flyway** | 10 | Ferramenta de migração de base de dados — aplica scripts SQL por ordem e garante que o schema está sempre atualizado |
| **MySQL Connector/J** | (incluído) | Driver JDBC para ligar o Spring ao MySQL |
| **Spring Mail** | (incluído) | Envio de emails via SMTP (Gmail) — usado para confirmação de encomenda |
| **SpringDoc OpenAPI** | 2.6.0 | Gera automaticamente documentação Swagger UI a partir do código |
| **Spring Actuator** | (incluído) | Endpoints de monitorização (`/actuator/health`) usados pelo Docker para health checks |
| **Maven** | 3.9.9 | Gestor de dependências e build do backend |
| **Lombok** | — | Não usado; getters/setters escritos manualmente |

### Frontend

| Tecnologia | Versão | Função no Projeto |
|-----------|--------|-------------------|
| **React** | 18.3.1 | Biblioteca UI — constrói a interface com componentes reutilizáveis |
| **TypeScript** | 5.6.3 | Tipagem estática sobre JavaScript — previne erros em tempo de compilação |
| **Vite** | 5.4.10 | Build tool ultra-rápido; substitui o Create React App. Em dev, serve com HMR (hot reload) |
| **Axios** | 1.7.7 | Cliente HTTP — faz os pedidos à API REST do backend com suporte a interceptors |
| **Context API** | (nativo React) | Gestão de estado global (carrinho, autenticação, lista de desejos) sem biblioteca externa |
| **CSS Personalizado** | — | Estilização inspirada no design Decathlon; sem framework CSS externo |

### Infraestrutura e DevOps

| Tecnologia | Função no Projeto |
|-----------|-------------------|
| **Docker** | Empacota cada serviço num container isolado com todas as dependências |
| **Docker Compose** | Orquestra os 3 containers (db, backend, frontend) com dependências e health checks |
| **GitHub Actions** | CI/CD automático — corre testes e build a cada push para o repositório |
| **H2 Database** | Base de dados em memória usada exclusivamente nos testes automatizados (não precisa de MySQL externo) |
| **Mockito** | Framework de mocking para testes unitários — simula dependências sem aceder à BD real |

---

## Arquitetura do Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                        BROWSER                                   │
│  React 18 + TypeScript + Vite                                   │
│  ┌──────────┐  ┌─────────────┐  ┌──────────────────────────┐   │
│  │  Páginas │  │  Contextos  │  │  Serviços (Axios)        │   │
│  │  (12+)   │  │  Auth/Cart/ │  │  auth, product, sale,    │   │
│  │          │  │  Wishlist   │  │  review, wishlist, stats  │   │
│  └──────────┘  └─────────────┘  └──────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────┘
                            │ HTTP/REST (JSON) + Cookies HttpOnly
                            │ /api/*
┌───────────────────────────▼─────────────────────────────────────┐
│                    BACKEND (Spring Boot 8080)                    │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────────┐    │
│  │ Controllers │→ │  Services   │→ │    Repositories      │    │
│  │  (REST API) │  │  (Negócio)  │  │  (Spring Data JPA)   │    │
│  └─────────────┘  └─────────────┘  └──────────┬───────────┘    │
│  ┌─────────────────────────────────────────┐   │                │
│  │  Spring Security + JwtAuthFilter        │   │                │
│  │  (valida JWT em cada pedido)            │   │                │
│  └─────────────────────────────────────────┘   │                │
└───────────────────────────────────────────────┬─────────────────┘
                                                │ JDBC
┌───────────────────────────────────────────────▼─────────────────┐
│                    MySQL 8.0 (porta 3306)                        │
│  9 tabelas | Flyway V1→V8 | sports_store database               │
└─────────────────────────────────────────────────────────────────┘
```

### Padrão de Camadas (Backend)

O backend segue a arquitetura em camadas típica de Spring Boot:

```
HTTP Request
    ↓
Controller       — Recebe o pedido, valida input, chama o service
    ↓
Service          — Lógica de negócio, regras, orquestração
    ↓
Repository       — Acesso à BD (Spring Data JPA / Hibernate)
    ↓
MySQL Database
```

---

## Funcionalidades

### Área do Cliente

| Funcionalidade | Descrição |
|---------------|-----------|
| **Catálogo** | Listagem paginada de produtos com filtro por categoria e pesquisa por texto |
| **Detalhe do Produto** | Imagem, descrição, preço, stock, avaliações com média de estrelas |
| **Carrinho** | Adicionar/remover produtos, alterar quantidades; persiste em `localStorage` |
| **Lista de Desejos** | Guardar produtos favoritos (♡) com sincronização no backend |
| **Avaliações** | Classificar produtos de 1 a 5 estrelas com comentário (uma avaliação por cliente por produto) |
| **Checkout** | Morada de envio completa, método de pagamento, confirmação de encomenda |
| **Faturas** | Geração automática de fatura após checkout; visualização e impressão |
| **Histórico** | Ver todas as encomendas passadas com estado atual |
| **Perfil** | Alterar nome e password |
| **Registo/Login** | Autenticação com email e password; sessão por cookie seguro |

### Área de Administração

| Funcionalidade | Descrição |
|---------------|-----------|
| **Gestão de Produtos** | Criar, editar, ativar/desativar e eliminar produtos |
| **Gestão de Categorias** | Criar, editar e eliminar categorias |
| **Gestão de Vendas** | Ver todas as vendas; atualizar estado (CONFIRMADO → EM PROCESSAMENTO → ENVIADO → ENTREGUE) |
| **Clientes** | Listar todos os clientes registados |
| **Estatísticas** | Produtos mais vendidos, menos vendidos, melhores clientes, receita por período |

### Funcionalidades Técnicas

| Funcionalidade | Descrição |
|---------------|-----------|
| **JWT + Refresh Token** | Access token (15 min) + refresh token (7 dias) com rotação automática |
| **Cookies HttpOnly** | Tokens armazenados em cookies seguros, inacessíveis ao JavaScript |
| **Controlo de Stock** | Dedução automática no checkout; erro se stock insuficiente |
| **Paginação** | `PageResponse<T>` com `page`, `size`, `totalElements`, `totalPages` |
| **Migrações Automáticas** | Flyway aplica V1→V8 automaticamente ao arrancar |
| **Email de Confirmação** | Email HTML enviado após checkout (desativado por defeito; ativa com `MAIL_ENABLED=true`) |
| **Swagger UI** | Documentação interativa da API em `/swagger-ui.html` |
| **Health Check** | `/actuator/health/liveness` usado pelo Docker para verificar se o backend está pronto |

---

## Base de Dados

### Esquema Completo

```
customers ──< sales ──< sale_items >── products >── categories
    │           └──── invoices
    ├── wishlist_items >── products
    ├── reviews >── products
    └── refresh_tokens
```

### Tabelas

#### `customers` — Utilizadores
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador único |
| name | VARCHAR | Nome completo |
| email | VARCHAR UNIQUE | Email (usado como username) |
| password_hash | VARCHAR | Password encriptada com BCrypt |
| role | ENUM | `ADMIN` ou `CLIENT` |
| created_at | TIMESTAMP | Data de registo |

#### `categories` — Categorias de Produtos
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| name | VARCHAR UNIQUE | Nome da categoria |
| description | TEXT | Descrição opcional |

#### `products` — Catálogo de Produtos
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| name | VARCHAR | Nome do produto |
| description | TEXT | Descrição |
| price | DECIMAL(10,2) | Preço (≥ 0) |
| stock | INT | Unidades em stock (≥ 0) |
| active | BOOLEAN | Se está disponível na loja |
| image_url | VARCHAR | URL da imagem (Unsplash CDN) |
| category_id | BIGINT FK | Categoria a que pertence |

#### `sales` — Encomendas
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| customer_id | BIGINT FK | Cliente que fez a encomenda |
| total | DECIMAL(10,2) | Valor total |
| status | ENUM | `CONFIRMED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED` |
| created_at | TIMESTAMP | Data da encomenda |
| name | VARCHAR | Nome do destinatário |
| phone | VARCHAR | Telefone |
| address | VARCHAR | Morada linha 1 |
| address2 | VARCHAR | Morada linha 2 (opcional) |
| postal_code | VARCHAR | Código postal |
| city | VARCHAR | Cidade |
| region | VARCHAR | Região |
| country | VARCHAR | País |
| payment_method | VARCHAR | Método de pagamento |

#### `sale_items` — Linhas de Encomenda
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| sale_id | BIGINT FK | Encomenda a que pertence |
| product_id | BIGINT FK | Produto |
| quantity | INT | Quantidade (> 0) |
| unit_price | DECIMAL(10,2) | Preço unitário no momento da compra |

#### `invoices` — Faturas
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| sale_id | BIGINT FK UNIQUE | Encomenda (1:1) |
| invoice_number | VARCHAR UNIQUE | Número de fatura (ex: `INV-00042`) |
| issued_at | TIMESTAMP | Data de emissão |

#### `wishlist_items` — Lista de Desejos
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| customer_id | BIGINT FK | Cliente |
| product_id | BIGINT FK | Produto guardado |
| created_at | DATETIME | Quando foi adicionado |
| — | UNIQUE(customer_id, product_id) | Um produto por cliente |

#### `reviews` — Avaliações
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| customer_id | BIGINT FK | Cliente que avaliou |
| product_id | BIGINT FK | Produto avaliado |
| rating | TINYINT | Classificação de 1 a 5 |
| comment | TEXT | Comentário opcional |
| created_at | DATETIME | Data da avaliação |
| — | UNIQUE(customer_id, product_id) | Uma avaliação por cliente por produto |

#### `refresh_tokens` — Tokens de Sessão
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT PK | Identificador |
| customer_id | BIGINT FK | Cliente dono do token |
| token | VARCHAR(512) UNIQUE | UUID aleatório |
| expires_at | TIMESTAMP | Expira ao fim de 7 dias |
| created_at | TIMESTAMP | Quando foi criado |

### Migrações Flyway

O Flyway aplica os scripts SQL por ordem numérica ao arrancar o backend. Nunca edita um script já aplicado.

| Versão | Ficheiro | O que faz |
|--------|----------|-----------|
| V1 | `V1__init.sql` | Cria as 6 tabelas base: customers, categories, products, sales, sale_items, invoices |
| V2 | `V2__seed.sql` | Insere 4 categorias iniciais, o utilizador admin e 4 produtos exemplo |
| V3 | `V3__demo_data.sql` | Adiciona mais 3 categorias e 23 produtos desportivos com dados reais |
| V4 | `V4__add_image_url.sql` | Adiciona coluna `image_url` aos produtos e preenche com URLs do Unsplash |
| V5 | `V5__add_shipping_address.sql` | Adiciona campos de morada e pagamento à tabela `sales`; adiciona coluna `status` |
| V6 | `V6__expand_catalog.sql` | Expande o catálogo com mais produtos |
| V7 | `V7__wishlist_and_reviews.sql` | Cria tabelas `wishlist_items` e `reviews` |
| V8 | `V8__refresh_tokens.sql` | Cria tabela `refresh_tokens` para o sistema de refresh JWT |

---

## API REST

A API segue as convenções REST. Todos os endpoints retornam JSON. Erros retornam um objeto `ApiError` com `status`, `message` e `timestamp`.

### Autenticação — `/api/auth`

| Método | Endpoint | Corpo | Resposta | Auth |
|--------|----------|-------|----------|------|
| POST | `/register` | `{name, email, password}` | `AuthResponse` + cookies | Não |
| POST | `/login` | `{email, password}` | `AuthResponse` + cookies | Não |
| POST | `/refresh` | — | `CustomerResponse` + novos cookies | Cookie refresh_token |
| POST | `/logout` | — | 200 OK + limpa cookies | Não |
| GET | `/me` | — | `CustomerResponse` | Sim |
| PATCH | `/profile` | `{name?, currentPassword?, newPassword?}` | `CustomerResponse` | Sim |

O login e registo definem dois cookies HttpOnly:
- `jwt` — access token (15 minutos)
- `refresh_token` — refresh token (7 dias, path `/api/auth`)

### Produtos — `/api/products`

| Método | Endpoint | Parâmetros | Resposta | Auth |
|--------|----------|-----------|----------|------|
| GET | `/` | `categoryId`, `activeOnly`, `search`, `page`, `size` | `PageResponse<ProductResponse>` | Não |
| GET | `/{id}` | — | `ProductResponse` | Não |
| POST | `/` | `ProductRequest` | `ProductResponse` | Admin |
| PUT | `/{id}` | `ProductRequest` | `ProductResponse` | Admin |
| DELETE | `/{id}` | — | 204 No Content | Admin |

**Paginação:** `GET /api/products?page=0&size=12&search=bola&categoryId=2`

Resposta paginada:
```json
{
  "content": [...],
  "page": 0,
  "size": 12,
  "totalElements": 47,
  "totalPages": 4
}
```

### Categorias — `/api/categories`

| Método | Endpoint | Resposta | Auth |
|--------|----------|----------|------|
| GET | `/` | `List<CategoryResponse>` | Não |
| GET | `/{id}` | `CategoryResponse` | Não |
| POST | `/` | `CategoryResponse` | Admin |
| PUT | `/{id}` | `CategoryResponse` | Admin |
| DELETE | `/{id}` | 204 No Content | Admin |

### Vendas — `/api/sales`

| Método | Endpoint | Corpo | Resposta | Auth |
|--------|----------|-------|----------|------|
| POST | `/checkout` | `CheckoutRequest` | `SaleResponse` | Sim |
| GET | `/` | — | `List<SaleResponse>` | Sim |
| GET | `/{id}` | — | `SaleResponse` | Sim |
| GET | `/{id}/invoice` | — | `InvoiceResponse` | Sim |

`CheckoutRequest` inclui: lista de itens `[{productId, quantity}]`, nome, telefone, morada completa e método de pagamento.

### Administração de Vendas — `/api/admin/sales`

| Método | Endpoint | Corpo | Resposta | Auth |
|--------|----------|-------|----------|------|
| GET | `/` | — | `List<SaleResponse>` | Admin |
| PATCH | `/{id}/status` | `{status}` | `SaleResponse` | Admin |

Estados possíveis: `CONFIRMED` → `PROCESSING` → `SHIPPED` → `DELIVERED` / `CANCELLED`

### Lista de Desejos — `/api/wishlist`

| Método | Endpoint | Resposta | Auth |
|--------|----------|----------|------|
| GET | `/` | `List<WishlistResponse>` | Sim |
| GET | `/ids` | `List<Long>` | Sim |
| POST | `/{productId}` | `WishlistResponse` | Sim |
| DELETE | `/{productId}` | 204 No Content | Sim |

### Avaliações — `/api/products/{productId}/reviews`

| Método | Endpoint | Corpo | Resposta | Auth |
|--------|----------|-------|----------|------|
| GET | `/` | — | `ProductRatingResponse` | Não |
| PUT | `/` | `{rating: 1-5, comment?}` | `ReviewResponse` | Sim |

`ProductRatingResponse` inclui: `average` (média), `count` (total de avaliações) e `reviews` (lista detalhada).

### Estatísticas — `/api/stats` (Admin)

| Método | Endpoint | Parâmetros | Resposta |
|--------|----------|-----------|----------|
| GET | `/products/top-selling` | — | `List<StatsProductResponse>` |
| GET | `/products/least-selling` | — | `List<StatsProductResponse>` |
| GET | `/customers/best` | — | `List<StatsCustomerResponse>` |
| GET | `/revenue` | `period=day\|week\|month` | `StatsRevenueResponse` |

### Administração de Clientes — `/api/admin/customers`

| Método | Endpoint | Resposta | Auth |
|--------|----------|----------|------|
| GET | `/` | `List<CustomerResponse>` | Admin |

---

## Frontend

### Estrutura de Páginas

#### Páginas Públicas (sem login)

| Rota | Componente | O que faz |
|------|-----------|-----------|
| `/` | `HomePage` | Página inicial com produtos em destaque e call-to-action |
| `/catalog` | `CatalogPage` | Catálogo com filtros por categoria, pesquisa e paginação |
| `/products/{id}` | `ProductPage` | Detalhe do produto com avaliações e botão de wishlist |
| `/cart` | `CartPage` | Carrinho de compras com resumo e botão de checkout |
| `/login` | `LoginPage` | Formulário de autenticação |
| `/register` | `RegisterPage` | Formulário de registo |

#### Páginas Autenticadas (requerem login)

| Rota | Componente | O que faz |
|------|-----------|-----------|
| `/checkout` | `CheckoutPage` | Morada de envio, método de pagamento e confirmação |
| `/orders` | `OrdersPage` | Histórico de todas as encomendas do cliente |
| `/orders/{id}/invoice` | `InvoicePage` | Visualização e impressão da fatura |
| `/profile` | `ProfilePage` | Edição de nome e password |
| `/wishlist` | `WishlistPage` | Produtos guardados como favoritos |

#### Páginas de Administração (requerem role ADMIN)

| Rota | Componente | O que faz |
|------|-----------|-----------|
| `/admin/products` | `AdminProductsPage` | CRUD de produtos |
| `/admin/categories` | `AdminCategoriesPage` | CRUD de categorias |
| `/admin/sales` | `AdminSalesPage` | Ver e atualizar estado de todas as vendas |
| `/admin/customers` | `AdminCustomersPage` | Listar todos os clientes |
| `/admin/stats` | `AdminStatsPage` | Dashboard com estatísticas e gráficos |

### Componentes Reutilizáveis

| Componente | Localização | O que faz |
|-----------|-------------|-----------|
| `Header` | `components/Header/` | Barra de navegação com logo, pesquisa, links de auth, carrinho e wishlist |
| `Footer` | `components/Footer/` | Rodapé com informação da loja |
| `Logo` | `components/Logo/` | SVG do logótipo SportFlow (escudo azul + raio laranja) |
| `ProductCard` | `components/ProductCard/` | Cartão de produto com imagem, preço, stock, botão de adicionar e botão ♡ |

### Contextos (Estado Global)

#### `AuthContext`
Gere a sessão do utilizador. Ao arrancar, faz `GET /api/auth/me` para verificar se há sessão ativa. Expõe `customer`, `isAuthenticated`, `isLoading`, `login()`, `logout()`.

#### `CartContext`
Gere o carrinho de compras em `localStorage`. Expõe `items`, `addProduct()`, `removeProduct()`, `updateQuantity()`, `clearCart()`. O carrinho persiste entre sessões do browser.

#### `WishlistContext`
Sincroniza a lista de desejos com o backend. Carrega os IDs em wishlist ao autenticar. Expõe `wishlistIds`, `toggle(productId)`, `isWishlisted(id)`. Redireciona para login se não autenticado.

### Serviços (Camada de API)

Cada ficheiro de serviço encapsula os pedidos Axios para um recurso:

| Ficheiro | Endpoints que usa |
|---------|-------------------|
| `auth.service.ts` | `/api/auth/*` |
| `product.service.ts` | `/api/products/*` |
| `category.service.ts` | `/api/categories/*` |
| `sale.service.ts` | `/api/sales/*`, `/api/admin/sales/*` |
| `review.service.ts` | `/api/products/{id}/reviews` |
| `wishlist.service.ts` | `/api/wishlist/*` |
| `stats.service.ts` | `/api/stats/*` |

### Axios Client (`api/apiClient.ts`)

O `apiClient` é uma instância Axios configurada com:
- `baseURL`: `/api` (em dev, Vite faz proxy para `http://localhost:8080`)
- `withCredentials: true` — envia cookies HttpOnly em todos os pedidos
- **Interceptor de resposta**: ao receber `401 Unauthorized`, tenta automaticamente fazer `POST /api/auth/refresh`. Se o refresh for bem-sucedido, repete o pedido original de forma transparente. Se falhar, redireciona para `/login`.

### Routing

O projeto usa routing manual baseado em `window.history.pushState` (sem React Router). A função `navigate(path)` altera o URL e dispara um evento `popstate` para re-renderizar o componente `Page` em `App.tsx`.

### Estilos

O CSS está centralizado em `styles/global.css` e usa variáveis CSS (`--blue`, `--orange`, `--surface`, `--border`, etc.) inspiradas no design da Decathlon. Não usa qualquer framework CSS (sem Bootstrap, Tailwind, etc.). É totalmente responsivo com media queries.

---

## Segurança e Autenticação

### Fluxo de Autenticação

```
1. Cliente faz POST /api/auth/login
2. Backend valida credenciais com BCrypt
3. Backend gera:
   - JWT (access token, 15 min, cookie "jwt")
   - Refresh token (UUID, 7 dias, cookie "refresh_token", guardado na BD)
4. Cookies HttpOnly enviados para o browser
5. Browser envia cookies automaticamente em cada pedido
6. JwtAuthenticationFilter valida o JWT em cada request
7. Quando JWT expira (15 min):
   - Axios interceptor deteta 401
   - Faz POST /api/auth/refresh (envia cookie refresh_token)
   - Backend valida refresh token na BD, gera novo JWT e novo refresh token (rotação)
   - Pedido original é repetido automaticamente
8. Logout: apaga ambos os cookies e invalida o refresh token na BD
```

### Proteção dos Endpoints

| Tipo | Acesso |
|------|--------|
| Rotas públicas | `GET /api/products/**`, `GET /api/categories/**`, `POST /api/auth/login`, `POST /api/auth/register`, `POST /api/auth/refresh`, Swagger |
| Rotas autenticadas | `/api/wishlist/**`, `/api/sales/**`, `PUT /api/products/{id}/reviews`, `/api/auth/me` |
| Rotas de admin | `/api/products/**` (POST/PUT/DELETE), `/api/categories/**` (POST/PUT/DELETE), `/api/admin/**`, `/api/stats/**` |

### Tokens

| Token | Duração | Armazenamento | Configurável |
|-------|---------|---------------|-------------|
| JWT (access) | 15 minutos | Cookie HttpOnly `jwt` | `JWT_EXPIRATION_MINUTES` |
| Refresh token | 7 dias | Cookie HttpOnly `refresh_token` + tabela `refresh_tokens` | `REFRESH_EXPIRATION_DAYS` |

### Passwords

As passwords são encriptadas com **BCrypt** (Spring Security). O hash é armazenado em `password_hash` e nunca exposto em respostas da API.

---

## Execução do Projeto

### Pré-requisitos (sem Docker)

| Ferramenta | Versão | Instalar (Ubuntu/WSL) |
|-----------|--------|-----------------------|
| Java JDK | 21+ | `sudo apt install openjdk-21-jdk` |
| Node.js | 18+ | `sudo apt install nodejs npm` |
| MySQL | 8.0+ | `sudo apt install mysql-server` |

### Opção A — Script Automático (WSL/Linux) — Recomendado

```bash
# Na raiz do projeto
chmod +x start.sh
./start.sh
```

O script:
1. Verifica se Java, Node e MySQL estão disponíveis
2. Inicia o backend em background (`spring-boot:run` com perfil `dev`, sem compilar testes)
3. Aguarda o backend responder em `http://localhost:8080/actuator/health` (máx. 2 min)
4. Inicia o frontend com `npm run dev`

Abre o browser em **http://localhost:5173**

Para parar: `Ctrl+C`

### Opção B — Manual (dois terminais)

**Terminal 1 — Backend:**
```bash
cd backend
chmod +x mvnw
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dmaven.test.skip=true
```

**Terminal 2 — Frontend** (após o backend estar pronto):
```bash
cd frontend
npm install   # só na primeira vez
npm run dev
```

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

### Configurar o MySQL (necessário sem Docker)

```bash
sudo service mysql start
mysql -u root < scripts/setup-mysql.sql
# ou com password: mysql -u root -p < scripts/setup-mysql.sql
```

O script `setup-mysql.sql` cria:
- Base de dados: `sports_store`
- Utilizador: `sportflow` com password `sportflow123`

### Credenciais de Teste

| Utilizador | Email | Password | Role |
|-----------|-------|----------|------|
| Administrador | `admin@store.test` | `password` | ADMIN |

Regista um cliente normal em `/register`.

### Endereços (sem Docker)

| Serviço | URL |
|---------|-----|
| Loja (frontend) | http://localhost:5173 |
| API REST | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Health Check | http://localhost:8080/actuator/health |

---

## Docker

O Docker permite correr o projeto em qualquer máquina sem instalar Java, Node ou MySQL.

### Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução

### Iniciar com Docker

```bash
# Na raiz do projeto
docker compose up --build
```

O `--build` compila as imagens. Nas execuções seguintes (sem alterações ao código), pode usar apenas:

```bash
docker compose up
```

Abre o browser em **http://localhost:3000**

### Parar

```bash
docker compose down
```

Para também apagar os dados da base de dados (volume):

```bash
docker compose down -v
```

### Serviços Docker

```yaml
db:
  image: mysql:8.0
  # Base de dados MySQL com health check
  # Volume persistente: db_data

backend:
  build: ./backend
  # Compila e corre o JAR Spring Boot
  # Depende de db estar healthy
  # Health check: /actuator/health/liveness

frontend:
  build: ./frontend
  ports: "3000:80"
  # Build estático do React servido por Nginx
  # Depende de backend estar healthy
  # Proxy /api → http://backend:8080
```

### Endereços (com Docker)

| Serviço | URL |
|---------|-----|
| Loja (frontend) | http://localhost:3000 |
| API REST | http://localhost:8080 (não exposta por defeito) |

### Noutro Computador

```bash
git clone https://github.com/Diogozalao/SD2.git
cd SD2
docker compose up --build
# Abre http://localhost:3000
```

Não são necessários Java, Node ou MySQL — o Docker trata de tudo.

### Variáveis de Ambiente (Docker)

Configuram-se em `docker-compose.yml` ou num ficheiro `.env`:

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `SPRING_DATASOURCE_URL` | jdbc:mysql://db:3306/sports_store... | URL de ligação MySQL |
| `SPRING_DATASOURCE_USERNAME` | sportflow | Utilizador MySQL |
| `SPRING_DATASOURCE_PASSWORD` | sportflow123 | Password MySQL |
| `JWT_SECRET` | sportflow-secret-key-... | Chave para assinar JWT (mínimo 32 chars) |
| `JWT_EXPIRATION_MINUTES` | 15 | Duração do access token |
| `REFRESH_EXPIRATION_DAYS` | 7 | Duração do refresh token |
| `MAIL_ENABLED` | false | Ativar envio de emails |
| `MAIL_HOST` | smtp.gmail.com | Servidor SMTP |
| `MAIL_USERNAME` | — | Email remetente |
| `MAIL_PASSWORD` | — | Password do email |

---

## Testes

### Executar todos os testes

```bash
cd backend
./mvnw test
```

Os testes usam **H2 em modo MySQL** (configurado em `src/test/resources/application-test.yml`) — não é necessário MySQL externo.

### Tipos de Testes

#### Testes Unitários (Mockito)

Testam a lógica de negócio de cada service isoladamente, sem acesso à base de dados real. As dependências são simuladas com Mockito (`@Mock`).

| Ficheiro | O que testa |
|---------|-------------|
| `ProductServiceTest` | Filtros de pesquisa, CRUD de produtos, stock |
| `CategoryServiceTest` | CRUD de categorias, validação de duplicados |
| `AuthServiceTest` | Registo, login, alteração de perfil, passwords |
| `SaleServiceTest` | Checkout, validação de stock, atualização de estado |

Exemplo — como o Mockito é usado:
```java
@Mock private ProductRepository productRepository;

@Test
void findById_throwsNotFound_whenProductDoesNotExist() {
    when(productRepository.findById(999L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> productService.findById(999L))
        .isInstanceOf(NotFoundException.class);
}
```

#### Testes de Integração (H2 + Spring Boot)

`ApiIntegrationTest` testa o fluxo completo HTTP → Controller → Service → Repository → H2, usando `MockMvc` para simular pedidos HTTP reais.

Cenários testados:
- Registo e login retornam JWT válido
- Cliente não pode criar produtos (403 Forbidden)
- Admin pode criar produtos (201 Created)
- Checkout cria venda, gera fatura e deduz stock
- Checkout rejeita se stock insuficiente
- Estatísticas requerem role ADMIN
- Admin vê todas as vendas; cliente só as suas

### Estrutura de Testes

```
backend/src/test/
├── java/pt/ubi/gruposd/loja/
│   ├── ApiIntegrationTest.java       # Testes de integração completos
│   └── service/
│       ├── ProductServiceTest.java
│       ├── CategoryServiceTest.java
│       ├── AuthServiceTest.java
│       └── SaleServiceTest.java
└── resources/
    └── application-test.yml          # H2 em modo MySQL
```

---

## CI/CD — GitHub Actions

O projeto tem dois workflows independentes que correm automaticamente a cada `git push`.

### Backend CI (`.github/workflows/backend-ci.yml`)

**Quando corre:** em push com alterações em `backend/**` ou nos próprios workflows.

**Passos:**
1. Checkout do código
2. Setup Java 21 (distribuição Temurin/Eclipse)
3. **Testes unitários** — `./mvnw test -Dtest="*ServiceTest" -Dspring.profiles.active=test`
4. **Testes de integração** — `./mvnw test -Dtest="ApiIntegrationTest" -Dspring.profiles.active=test`
5. **Build do JAR** — `./mvnw package -DskipTests`

Falha se qualquer teste falhar ou se o código não compilar.

### Frontend CI (`.github/workflows/frontend-ci.yml`)

**Quando corre:** em push com alterações em `frontend/**` ou nos próprios workflows.

**Passos:**
1. Checkout do código
2. Setup Node.js 20
3. `npm ci` — instalação limpa de dependências (usa `package-lock.json`)
4. **Type check** — `tsc --noEmit` (verifica tipos TypeScript sem gerar ficheiros)
5. **Build** — `npm run build` (verifica que o build de produção funciona)

Falha se houver erros de TypeScript ou se o build falhar.

---

## Estrutura de Ficheiros

```
SD2/
├── .github/
│   └── workflows/
│       ├── backend-ci.yml            # CI do backend
│       └── frontend-ci.yml           # CI do frontend
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/pt/ubi/gruposd/loja/
│   │   │   │   ├── config/
│   │   │   │   │   ├── OpenApiConfig.java        # Config do Swagger UI
│   │   │   │   │   ├── SecurityConfig.java        # Cadeia de filtros de segurança
│   │   │   │   │   └── WebConfig.java             # CORS e configuração web
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AuthController.java        # /api/auth/*
│   │   │   │   │   ├── ProductController.java     # /api/products/*
│   │   │   │   │   ├── CategoryController.java    # /api/categories/*
│   │   │   │   │   ├── SaleController.java        # /api/sales/*
│   │   │   │   │   ├── AdminSaleController.java   # /api/admin/sales/*
│   │   │   │   │   ├── AdminCustomerController.java # /api/admin/customers/*
│   │   │   │   │   ├── ReviewController.java      # /api/products/{id}/reviews
│   │   │   │   │   ├── WishlistController.java    # /api/wishlist/*
│   │   │   │   │   ├── StatsController.java       # /api/stats/*
│   │   │   │   │   └── InvoiceController.java     # /api/invoices/*
│   │   │   │   ├── dto/                           # 20+ DTOs de request/response
│   │   │   │   ├── exception/
│   │   │   │   │   ├── ApiExceptionHandler.java   # Handler global de erros
│   │   │   │   │   ├── BadRequestException.java
│   │   │   │   │   ├── NotFoundException.java
│   │   │   │   │   ├── UnauthorizedException.java
│   │   │   │   │   └── ConflictException.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Customer.java
│   │   │   │   │   ├── Product.java
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── Sale.java
│   │   │   │   │   ├── SaleItem.java
│   │   │   │   │   ├── Invoice.java
│   │   │   │   │   ├── Review.java
│   │   │   │   │   ├── WishlistItem.java
│   │   │   │   │   ├── RefreshToken.java
│   │   │   │   │   ├── UserRole.java             # Enum: ADMIN, CLIENT
│   │   │   │   │   └── SaleStatus.java           # Enum: CONFIRMED, PROCESSING, ...
│   │   │   │   ├── repository/
│   │   │   │   │   ├── CustomerRepository.java
│   │   │   │   │   ├── ProductRepository.java
│   │   │   │   │   ├── ProductSpecifications.java # Filtros dinâmicos (JPA Spec)
│   │   │   │   │   ├── CategoryRepository.java
│   │   │   │   │   ├── SaleRepository.java
│   │   │   │   │   ├── SaleItemRepository.java
│   │   │   │   │   ├── InvoiceRepository.java
│   │   │   │   │   ├── ReviewRepository.java
│   │   │   │   │   ├── WishlistRepository.java
│   │   │   │   │   └── RefreshTokenRepository.java
│   │   │   │   ├── security/
│   │   │   │   │   ├── JwtService.java            # Gera e valida JWTs
│   │   │   │   │   ├── JwtAuthenticationFilter.java # Extrai JWT de cookie ou header
│   │   │   │   │   ├── CustomerUserDetails.java    # Adapter Spring Security
│   │   │   │   │   └── CustomerUserDetailsService.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── ProductService.java
│   │   │   │   │   ├── CategoryService.java
│   │   │   │   │   ├── SaleService.java
│   │   │   │   │   ├── InvoiceService.java
│   │   │   │   │   ├── ReviewService.java
│   │   │   │   │   ├── WishlistService.java
│   │   │   │   │   ├── StatsService.java
│   │   │   │   │   ├── EmailService.java
│   │   │   │   │   └── RefreshTokenService.java
│   │   │   │   └── Application.java               # Ponto de entrada (@SpringBootApplication)
│   │   │   └── resources/
│   │   │       ├── db/migration/
│   │   │       │   ├── V1__init.sql
│   │   │       │   ├── V2__seed.sql
│   │   │       │   ├── V3__demo_data.sql
│   │   │       │   ├── V4__add_image_url.sql
│   │   │       │   ├── V5__add_shipping_address.sql
│   │   │       │   ├── V6__expand_catalog.sql
│   │   │       │   ├── V7__wishlist_and_reviews.sql
│   │   │       │   └── V8__refresh_tokens.sql
│   │   │       └── application.yml
│   │   └── test/
│   │       ├── java/pt/ubi/gruposd/loja/
│   │       │   ├── ApiIntegrationTest.java
│   │       │   └── service/
│   │       │       ├── ProductServiceTest.java
│   │       │       ├── CategoryServiceTest.java
│   │       │       ├── AuthServiceTest.java
│   │       │       └── SaleServiceTest.java
│   │       └── resources/
│   │           └── application-test.yml           # H2 em modo MySQL
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── apiClient.ts                       # Axios com interceptor de refresh
│   │   ├── components/
│   │   │   ├── Header/Header.tsx                  # Navbar
│   │   │   ├── Footer/Footer.tsx
│   │   │   ├── Logo/Logo.tsx                      # SVG do logótipo
│   │   │   └── ProductCard/ProductCard.tsx         # Cartão de produto
│   │   ├── contexts/
│   │   │   ├── AuthContext.tsx
│   │   │   ├── CartContext.tsx
│   │   │   └── WishlistContext.tsx
│   │   ├── pages/
│   │   │   ├── HomePage/
│   │   │   ├── CatalogPage/
│   │   │   ├── ProductPage/
│   │   │   ├── CartPage/
│   │   │   ├── CheckoutPage/
│   │   │   ├── OrdersPage/
│   │   │   ├── InvoicePage/
│   │   │   ├── ProfilePage/
│   │   │   ├── WishlistPage/
│   │   │   ├── LoginPage/
│   │   │   ├── RegisterPage/
│   │   │   ├── AdminProductsPage/
│   │   │   ├── AdminCategoriesPage/
│   │   │   ├── AdminSalesPage/
│   │   │   ├── AdminCustomersPage/
│   │   │   └── AdminStatsPage/
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   ├── product.service.ts
│   │   │   ├── category.service.ts
│   │   │   ├── sale.service.ts
│   │   │   ├── review.service.ts
│   │   │   ├── wishlist.service.ts
│   │   │   └── stats.service.ts
│   │   ├── styles/
│   │   │   └── global.css                         # Estilos globais
│   │   ├── utils/
│   │   │   └── categoryUtils.ts                   # Ícones e cores por categoria
│   │   ├── App.tsx                                # Router e providers
│   │   └── main.tsx                               # Entry point React
│   ├── Dockerfile
│   ├── nginx.conf                                 # Configuração Nginx (Docker)
│   ├── vite.config.ts
│   └── package.json
│
├── scripts/
│   ├── setup-mysql.sql                            # Cria BD e utilizador
│   └── run-local.ps1                              # Script PowerShell
│
├── docker-compose.yml
├── start.sh                                       # Script de arranque WSL/Linux
└── README.md
```

---

## Resolução de Problemas

### Backend não arranca

```bash
# Ver o log completo
cat /tmp/sportflow-backend.log

# Verificar MySQL
sudo service mysql status
sudo service mysql start

# Verificar utilizador MySQL
mysql -u sportflow -psportflow123 sports_store -e "SELECT 1;"

# Recriar utilizador se necessário
mysql -u root < scripts/setup-mysql.sql
```

### Erro de Flyway (checksum mismatch)

Se editares um ficheiro de migração já aplicado:

```bash
cd backend
./mvnw flyway:repair -Dspring-boot.run.profiles=dev
```

### Porta ocupada

```bash
# Libertar porta 8080
lsof -ti tcp:8080 | xargs kill -9

# Libertar porta 5173
lsof -ti tcp:5173 | xargs kill -9
```

### Docker — frontend não arranca

O frontend depende do backend estar `healthy`. Se o backend demorar muito:

```bash
docker compose logs backend   # ver logs do backend
docker compose ps             # verificar estados dos containers
```

### Docker — XAMPP na porta 80

O docker-compose usa a porta `3000:80` precisamente para evitar conflito com o XAMPP/Apache. Se a porta 3000 também estiver ocupada, edita `docker-compose.yml` e muda `"3000:80"` para outra porta (ex: `"8090:80"`).

---

## Informação Académica

- **Cadeira:** Sistemas Distribuídos
- **Instituição:** Universidade da Beira Interior (UBI)
- **Ano letivo:** 2025/2026
