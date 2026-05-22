import { useEffect, useState } from "react";
import { listAdminSales, type Sale } from "../../services/sale.service";

export function AdminSalesPage() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [expanded, setExpanded] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  useEffect(() => {
    listAdminSales()
      .then(setSales)
      .catch(() => setError("Nao foi possivel carregar as vendas."))
      .finally(() => setLoading(false));
  }, []);

  const filtered = sales.filter((s) =>
    search.trim() === "" ||
    String(s.id).includes(search) ||
    s.customerName.toLowerCase().includes(search.toLowerCase()) ||
    (s.invoice?.invoiceNumber ?? "").toLowerCase().includes(search.toLowerCase())
  );

  const totalRevenue = filtered.reduce((sum, s) => sum + s.total, 0);

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Vendas</h1>
          <p>{sales.length} venda(s) · Faturacao total: <strong>{totalRevenue.toFixed(2)} €</strong></p>
        </div>
        <input
          className="admin-search"
          type="text"
          placeholder="Pesquisar por ID, cliente, fatura..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      {loading ? (
        <div className="status-message">A carregar vendas...</div>
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
                    <span className="invoice-ref">Fatura {sale.invoice.invoiceNumber}</span>
                  )}
                </div>
                <div>
                  <strong>{sale.customerName}</strong>
                  <span>Cliente #{sale.customerId}</span>
                </div>
                <span>{sale.items.length} artigo(s)</span>
                <span className={`badge badge--status badge--${sale.status.toLowerCase()}`}>
                  {sale.status}
                </span>
                <strong>{sale.total.toFixed(2)} €</strong>
                <span className="expand-icon">{expanded === sale.id ? "▲" : "▼"}</span>
              </div>

              {expanded === sale.id && (
                <div className="sale-detail">
                  <div className="sale-detail__items">
                    {sale.items.map((item) => (
                      <div className="sale-detail__item" key={item.id}>
                        <span className="sale-detail__name">{item.productName}</span>
                        <span>{item.quantity} × {item.unitPrice.toFixed(2)} €</span>
                        <strong>{item.subtotal.toFixed(2)} €</strong>
                      </div>
                    ))}
                  </div>
                  <div className="sale-detail__footer">
                    <span>Total: <strong>{sale.total.toFixed(2)} €</strong></span>
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
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
