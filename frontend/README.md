# Frontend

Aplicacao React para consumo da API REST do backend.

## Execucao

```bash
npm install
npm run dev
```

Por omissao, o frontend usa `/api` como base URL. Em desenvolvimento, o Vite encaminha `/api` para `http://localhost:8080`.

## Paginas principais

- `/catalog`: catalogo publico.
- `/products/{id}`: detalhe de produto.
- `/cart`: carrinho.
- `/checkout`: finalizacao de compra.
- `/orders`: historico de compras.
- `/orders/{id}/invoice`: detalhe e impressao da fatura.
- `/login`: login.
- `/register`: registo.
- `/admin/products`: gestao de produtos.
- `/admin/categories`: gestao de categorias.
- `/admin/sales`: consulta global de vendas.
- `/admin/stats`: estatisticas.
