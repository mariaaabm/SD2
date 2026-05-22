# Demo pratica

Este guiao valida o fluxo principal da aplicacao SportFlow antes da apresentacao.

## Arranque sem Docker

Backend com H2 em memoria (sem necessidade de PostgreSQL):

```powershell
cd backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Frontend:

```powershell
cd frontend
npm run dev
```

Ou usar o script automatico (Windows):

```powershell
.\scripts\run-local.ps1
```

URLs:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console`

Credenciais admin:

```text
email: admin@store.test
password: password
```

## Validacao automatica por API

Com o backend a correr:

```bash
./scripts/validate-demo-api.sh
```

O script confirma:

- health check;
- catalogo publico com categorias e produtos desportivos;
- login admin;
- estatisticas admin;
- registo de cliente;
- criacao de produto admin;
- checkout;
- historico e fatura;
- consulta admin de vendas.

## Validacao manual no browser

1. Abrir `http://localhost:5173/catalog`.
2. Confirmar que existem categorias desportivas (Calcado, Vestuario, Equipamento, etc.) e produtos.
3. Entrar com `admin@store.test` / `password`.
4. Abrir `/admin/products` e criar ou editar um produto.
5. Abrir `/admin/categories` e confirmar a listagem de categorias.
6. Abrir `/admin/stats` e confirmar estatisticas.
7. Sair.
8. Criar uma conta de cliente em `/register`.
9. Adicionar produtos ao carrinho.
10. Finalizar compra em `/checkout`.
11. Abrir a fatura a partir do checkout ou de `/orders`.
12. Confirmar que `/admin/sales` mostra a venda quando se volta a entrar como admin.

## Arranque com Docker

Quando Docker estiver disponivel:

```bash
docker compose -f infra/docker-compose.yml up --build
./scripts/validate-stack.sh
```
