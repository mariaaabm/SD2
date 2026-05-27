import { useEffect, useState } from "react";
import { listAdminSales, updateSaleStatus, type Sale, type SaleStatus } from "../services/sale.service";

const STATUS_LABELS: Record<SaleStatus, string> = {
  CONFIRMED:  "Confirmada",
  PROCESSING: "Em preparação",
  SHIPPED:    "Enviada",
  DELIVERED:  "Entregue",
  CANCELLED:  "Cancelada",
};

// Mapa de progressão unidirecional do estado da encomenda.
// DELIVERED e CANCELLED não estão incluídos porque são estados terminais —
// não faz sentido avançar a partir deles com o botão rápido.
const STATUS_NEXT: Partial<Record<SaleStatus, SaleStatus>> = {
  CONFIRMED:  "PROCESSING",
  PROCESSING: "SHIPPED",
  SHIPPED:    "DELIVERED",
};

const PAYMENT_LABELS: Record<string, string> = {
  CARD:       "Cartão",
  MBWAY:      "MB Way",
  MULTIBANCO: "Multibanco",
  COD:        "Entrega",
};

// Página de administração que lista todas as vendas da loja com cliente, total, método de pagamento e estado, permite expandir cada venda para ver os itens, mudar o estado para o passo seguinte no ciclo da encomenda e abrir a fatura associada.
export function AdminSalesPage() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [expanded, setExpanded] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  useEffect(() => {
    listAdminSales()
      .then(setSales)
      .catch(() => setError("Não foi possível carregar as vendas."))
      .finally(() => setLoading(false));
  }, []);

  async function handleStatusChange(sale: Sale, status: SaleStatus) {
    setUpdatingId(sale.id);
    try {
      const updated = await updateSaleStatus(sale.id, status);
      setSales((prev) => prev.map((s) => s.id === updated.id ? updated : s));
    } catch {
      setError("Não foi possível atualizar o estado da venda.");
    } finally {
      setUpdatingId(null);
    }
  }

  const filtered = sales.filter((s) =>
    search.trim() === "" ||
    String(s.id).includes(search) ||
    s.customerName.toLowerCase().includes(search.toLowerCase()) ||
    (s.invoice?.invoiceNumber ?? "").toLowerCase().includes(search.toLowerCase())
  );

  // Total calculado sobre os resultados filtrados pela pesquisa, não sobre todas as vendas,
  // para dar feedback imediato ao admin quando filtra por cliente ou período específico.
  const totalRevenue = filtered.reduce((sum, s) => sum + s.total, 0);

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Vendas</h1>
          <p>
            {sales.length} venda(s) · Faturação filtrada:{" "}
            <strong>{totalRevenue.toFixed(2)} €</strong>
          </p>
        </div>
        <input
          className="admin-search"
          type="text"
          placeholder="Pesquisar por ID, cliente, fatura…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}

      {loading ? (
        <div className="status-message">A carregar vendas…</div>
      ) : filtered.length === 0 ? (
        <div className="status-message">Nenhuma venda encontrada.</div>
      ) : (
        <div className="admin-table">
          {filtered.map((sale) => (
            <div key={sale.id}>
              <div
                className="admin-row admin-row--sale admin-row--clickable"
                onClick={() => setExpanded(expanded === sale.id ? null : sale.id)}
              >
                <div>
                  <strong>Venda #{sale.id}</strong>
                  <span>{new Date(sale.createdAt).toLocaleString("pt-PT")}</span>
                  {sale.invoice && (
                    <span className="invoice-ref">{sale.invoice.invoiceNumber}</span>
                  )}
                </div>
                <div>
                  <strong>{sale.customerName}</strong>
                  <span>
                    {sale.shippingCity
                      ? `${sale.shippingCity}${sale.shippingCountry ? `, ${sale.shippingCountry}` : ""}`
                      : `Cliente #${sale.customerId}`}
                  </span>
                </div>
                <span>{sale.items.length} artigo(s)</span>
                <span className={`badge badge--status badge--${sale.status.toLowerCase()}`}>
                  {STATUS_LABELS[sale.status] ?? sale.status}
                </span>
                <strong>{sale.total.toFixed(2)} €</strong>
                <span className="expand-icon">{expanded === sale.id ? "▲" : "▼"}</span>
              </div>

              {expanded === sale.id && (
                <div className="sale-detail">
                  {/* Items */}
                  <div className="sale-detail__items">
                    {sale.items.map((item) => (
                      <div className="sale-detail__item" key={item.id}>
                        <span className="sale-detail__name">{item.productName}</span>
                        <span>{item.quantity} × {item.unitPrice.toFixed(2)} €</span>
                        <strong>{item.subtotal.toFixed(2)} €</strong>
                      </div>
                    ))}
                  </div>

                  {/* Shipping + payment info */}
                  {sale.shippingAddress && (
                    <div className="sale-detail__shipping">
                      <strong>Entrega:</strong>{" "}
                      {sale.shippingName} · {sale.shippingPhone}
                      <br />
                      {sale.shippingAddress}
                      {sale.shippingAddress2 ? `, ${sale.shippingAddress2}` : ""}
                      {" — "}{sale.shippingPostalCode} {sale.shippingCity}
                      {sale.shippingRegion ? `, ${sale.shippingRegion}` : ""}
                      {" · "}{sale.shippingCountry}
                      <br />
                      <strong>Pagamento:</strong>{" "}
                      {sale.paymentMethod ? (PAYMENT_LABELS[sale.paymentMethod] ?? sale.paymentMethod) : "—"}
                    </div>
                  )}

                  {/* Footer: total + status change + invoice link */}
                  <div className="sale-detail__footer">
                    <span>Total: <strong>{sale.total.toFixed(2)} €</strong></span>
                    <div className="sale-detail__actions">
                      <select
                        className="sale-status-select"
                        value={sale.status}
                        disabled={updatingId === sale.id || sale.status === "CANCELLED" || sale.status === "DELIVERED"}
                        onChange={(e) => handleStatusChange(sale, e.target.value as SaleStatus)}
                        onClick={(e) => e.stopPropagation()}
                      >
                        {(Object.keys(STATUS_LABELS) as SaleStatus[]).map((s) => (
                          <option key={s} value={s}>{STATUS_LABELS[s]}</option>
                        ))}
                      </select>
                      {STATUS_NEXT[sale.status] && (
                        <button
                          className="sale-advance-btn"
                          disabled={updatingId === sale.id}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleStatusChange(sale, STATUS_NEXT[sale.status]!);
                          }}
                        >
                          {updatingId === sale.id ? "…" : `→ ${STATUS_LABELS[STATUS_NEXT[sale.status]!]}`}
                        </button>
                      )}
                      {sale.invoice && (
                        <a
                          className="secondary-link"
                          href={`/orders/${sale.id}/invoice`}
                          onClick={(e) => e.stopPropagation()}
                        >
                          Ver fatura
                        </a>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
