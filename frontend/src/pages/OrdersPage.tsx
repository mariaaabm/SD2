import { useEffect, useState } from "react";
import { listSales, type Sale } from "../../services/sale.service";

export function OrdersPage() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    listSales()
      .then(setSales)
      .catch(() => setError("Nao foi possivel carregar o historico."))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section className="orders-page">
      <div className="page-heading">
        <div>
          <h1>As minhas compras</h1>
          <p>Historico de todas as compras realizadas.</p>
        </div>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}

      {loading ? (
        <div className="status-message">A carregar compras...</div>
      ) : sales.length === 0 ? (
        <div className="status-message">
          Ainda nao realizaste nenhuma compra.{" "}
          <a className="secondary-link" href="/catalog">Explorar produtos</a>
        </div>
      ) : (
        <div className="admin-table">
          {sales.map((sale) => (
            <div className="order-card" key={sale.id}>
              <div>
                <strong className="order-card__id">Encomenda #{sale.id}</strong>
                <span className="order-card__date">{new Date(sale.createdAt).toLocaleString("pt-PT")}</span>
                {sale.invoice && <span className="order-card__invoice">Fatura {sale.invoice.invoiceNumber}</span>}
              </div>
              <span className="order-card__status">{sale.status}</span>
              <strong className="order-card__total">{sale.total.toFixed(2)} €</strong>
              {sale.invoice && (
                <a className="primary-link" href={`/orders/${sale.id}/invoice`} style={{ fontSize: 13, height: 36, padding: "0 16px" }}>
                  Ver fatura
                </a>
              )}
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
