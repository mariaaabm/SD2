#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

request() {
  local method="$1"
  local path="$2"
  local body="${3:-}"
  local token="${4:-}"
  local output="$5"

  if [ -n "$token" ]; then
    curl -fsS -X "$method" \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $token" \
      ${body:+-d "$body"} \
      "$BASE_URL$path" > "$output"
  else
    curl -fsS -X "$method" \
      -H "Content-Type: application/json" \
      ${body:+-d "$body"} \
      "$BASE_URL$path" > "$output"
  fi
}

tmp_dir="$(mktemp -d)"
trap 'rm -rf "$tmp_dir"' EXIT

echo "1/9 Health check"
request GET /actuator/health "" "" "$tmp_dir/health.json"
grep -q '"status":"UP"' "$tmp_dir/health.json"

echo "2/9 Catalogo publico"
request GET /api/categories "" "" "$tmp_dir/categories.json"
request GET /api/products "" "" "$tmp_dir/products.json"
grep -q 'Calcado' "$tmp_dir/categories.json"
grep -q 'Sapatilhas Running Pro' "$tmp_dir/products.json"

echo "3/9 Login admin"
request POST /api/auth/login '{"email":"admin@store.test","password":"password"}' "" "$tmp_dir/admin-login.json"
admin_token="$(sed -n 's/.*"token":"\([^"]*\)".*/\1/p' "$tmp_dir/admin-login.json")"
test -n "$admin_token"

echo "4/9 Estatisticas admin"
request GET /api/stats/products/top-selling "" "$admin_token" "$tmp_dir/top-products.json"
request GET /api/stats/revenue?period=month "" "$admin_token" "$tmp_dir/revenue.json"

echo "5/9 Criar cliente demo"
demo_email="demo$(date +%s)@store.test"
request POST /api/auth/register "{\"name\":\"Cliente Demo\",\"email\":\"$demo_email\",\"password\":\"password123\"}" "" "$tmp_dir/client-register.json"
client_token="$(sed -n 's/.*"token":"\([^"]*\)".*/\1/p' "$tmp_dir/client-register.json")"
test -n "$client_token"

echo "6/9 Criar produto admin para compra demo"
request POST /api/products '{"name":"Produto Demo API","description":"Produto criado pela validacao demo","price":3.25,"stock":5,"categoryId":1,"active":true}' "$admin_token" "$tmp_dir/product.json"
product_id="$(sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p' "$tmp_dir/product.json")"
test -n "$product_id"

echo "7/9 Checkout cliente"
request POST /api/sales/checkout "{\"items\":[{\"productId\":$product_id,\"quantity\":2}]}" "$client_token" "$tmp_dir/sale.json"
sale_id="$(sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p' "$tmp_dir/sale.json")"
test -n "$sale_id"
grep -q '"invoice"' "$tmp_dir/sale.json"

echo "8/9 Historico e fatura do cliente"
request GET /api/sales "" "$client_token" "$tmp_dir/sales.json"
request GET "/api/sales/$sale_id/invoice" "" "$client_token" "$tmp_dir/invoice.json"
grep -q 'FT-' "$tmp_dir/invoice.json"

echo "9/9 Vendas admin"
request GET /api/admin/sales "" "$admin_token" "$tmp_dir/admin-sales.json"
grep -q 'Cliente Demo' "$tmp_dir/admin-sales.json"

echo "Demo API validada com sucesso em $BASE_URL"
