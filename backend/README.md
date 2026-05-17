# Backend

API RESTful Spring Boot para gestao da loja online.

## Modulos principais

- `controller`: endpoints REST.
- `service`: regras de negocio.
- `repository`: acesso a dados com Spring Data JPA.
- `model`: entidades JPA.
- `dto`: objetos de entrada e saida da API.
- `security`: autenticacao, autorizacao e sessao.
- `exception`: tratamento centralizado de erros.

## Execucao

```bash
./mvnw spring-boot:run
```

## Verificacao

```bash
curl http://localhost:8080/actuator/health
```

Documentacao da API:

```text
http://localhost:8080/swagger-ui.html
```

## Endpoints implementados

Autenticacao:

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

Categorias:

```text
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

Produtos:

```text
GET    /api/products
GET    /api/products?categoryId={id}
GET    /api/products?activeOnly=true
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

Vendas:

```text
POST /api/sales/checkout
GET  /api/sales
GET  /api/sales/{id}
GET  /api/sales/{id}/invoice
GET  /api/admin/sales
```

Faturas:

```text
GET /api/invoices/{id}
```

Estatisticas:

```text
GET /api/stats/products/top-selling
GET /api/stats/products/least-selling
GET /api/stats/customers/best
GET /api/stats/revenue?period=day
GET /api/stats/revenue?period=week
GET /api/stats/revenue?period=month
```

## Credenciais de desenvolvimento

Utilizador administrador inicial:

```text
email: admin@store.test
password: password
```

Os endpoints `POST`, `PUT` e `DELETE` de produtos/categorias exigem token JWT de um utilizador `ADMIN`.

## Dados iniciais

As migracoes Flyway criam um catalogo de demonstracao com categorias de alimentos, produtos ativos, stock realista e um produto inativo para testar a gestao de disponibilidade.

## Maven

O projeto inclui um wrapper Maven:

```bash
./mvnw test
./mvnw spring-boot:run
./mvnw package
```

Validacao de compilacao:

```bash
./mvnw test
./mvnw package
```

## Testes

Os testes de integracao usam H2 em modo PostgreSQL e validam:

- catalogo publico;
- registo e login;
- permissoes de cliente/admin;
- criacao de produto por admin;
- checkout com fatura;
- reducao de stock;
- rejeicao de checkout com stock insuficiente;
- acesso admin a estatisticas;
- acesso admin a listagem global de vendas.

No Windows:

```bat
mvnw.cmd test
mvnw.cmd spring-boot:run
mvnw.cmd package
```
