# Arquitetura

A aplicacao segue uma arquitetura web em tres camadas:

- Frontend React comunica com o backend por HTTP/REST.
- Backend Spring Boot expoe endpoints em `/api`.
- Spring Data JPA gere a persistencia em PostgreSQL.
- Flyway cria e inicializa o esquema da base de dados.
- Spring Security gere autenticacao JWT e controlo de acesso por roles.

## Entidades principais

- `Customer`
- `Category`
- `Product`
- `Sale`
- `SaleItem`
- `Invoice`

## Modelo de dados

### Customer

Representa um utilizador registado na loja. Pode ter perfil `CLIENT` ou `ADMIN`.

Campos principais:

- `id`
- `name`
- `email`
- `passwordHash`
- `role`
- `createdAt`

### Category

Agrupa produtos desportivos no catalogo (ex: Calcado, Vestuario, Equipamento, Acessorios).

Campos principais:

- `id`
- `name`
- `description`

### Product

Representa um artigo desportivo disponivel no catalogo.

Campos principais:

- `id`
- `name`
- `description`
- `price`
- `stock`
- `active`
- `category`

### Sale

Representa uma compra concluida por um cliente.

Campos principais:

- `id`
- `customer`
- `createdAt`
- `total`
- `status`

### SaleItem

Representa uma linha de uma venda.

Campos principais:

- `id`
- `sale`
- `product`
- `quantity`
- `unitPrice`

### Invoice

Representa a fatura gerada para uma venda.

Campos principais:

- `id`
- `sale`
- `invoiceNumber`
- `issuedAt`

## Dados de demonstracao

As migracoes Flyway incluem dados iniciais: administrador, categorias desportivas e produtos ativos com stock. Um produto inativo valida a gestao de disponibilidade.

## Regras de integridade

- Produtos nao podem ter preco negativo.
- Produtos nao podem ter stock negativo.
- Itens de venda devem ter quantidade superior a zero.
- Cada venda pertence a um cliente.
- Cada item de venda pertence a uma venda e a um produto.
- Cada venda tem no maximo uma fatura.
- Emails de clientes sao unicos.
- Nomes de categorias sao unicos.

## Endpoints

### Autenticacao

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Categorias

- `GET /api/categories`
- `GET /api/categories/{id}`
- `POST /api/categories` (ADMIN)
- `PUT /api/categories/{id}` (ADMIN)
- `DELETE /api/categories/{id}` (ADMIN)

### Produtos

- `GET /api/products`
- `GET /api/products?categoryId={id}`
- `GET /api/products?activeOnly=true`
- `GET /api/products/{id}`
- `POST /api/products` (ADMIN)
- `PUT /api/products/{id}` (ADMIN)
- `DELETE /api/products/{id}` (ADMIN)

### Vendas

- `POST /api/sales/checkout` (autenticado)
- `GET /api/sales` (autenticado)
- `GET /api/sales/{id}` (autenticado)
- `GET /api/sales/{id}/invoice` (autenticado)
- `GET /api/admin/sales` (ADMIN)

### Faturas

- `GET /api/invoices/{id}` (autenticado)

### Estatisticas

- `GET /api/stats/products/top-selling` (ADMIN)
- `GET /api/stats/products/least-selling` (ADMIN)
- `GET /api/stats/customers/best` (ADMIN)
- `GET /api/stats/revenue?period=day|week|month` (ADMIN)

## Seguranca

- Passwords guardadas com BCrypt.
- Autenticacao por JWT no header `Authorization: Bearer <token>`.
- Catalogo publico para consulta.
- Operacoes de escrita em produtos e categorias restritas a `ADMIN`.
- Compras, faturas e historico requerem utilizador autenticado.
- Estatisticas e vendas admin restritas a `ADMIN`.

## Frontend

Paginas principais:

- `/catalog`: catalogo publico.
- `/products/{id}`: detalhe de produto.
- `/cart`: carrinho.
- `/checkout`: finalizacao de compra.
- `/orders`: historico de compras.
- `/orders/{id}/invoice`: detalhe e impressao da fatura.
- `/login`: login.
- `/register`: registo.
- `/admin/products`: gestao admin de produtos.
- `/admin/categories`: gestao admin de categorias.
- `/admin/sales`: consulta admin de vendas.
- `/admin/stats`: estatisticas admin.
