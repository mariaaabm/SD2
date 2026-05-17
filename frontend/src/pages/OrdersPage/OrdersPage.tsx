import { useEffect, useState } from "react";
import { listSales, type Sale } from "../../services/sale.service";

export function OrdersPage() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadSales() {
      try {
        setSales(await listSales());
      } catch {
        setError("Nao foi possivel carregar o historico.");
      } finally {
        setLoading(false);
      }
    }

    loadSales();
  }, []);

  return (
    <section className="orders-page">
      <div className="page-heading">
        <div>
          <h1>Compras</h1>
          <p>Historico de compras do cliente autenticado.</p>
        </div>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      {loading ? (
        <div className="status-message">A carregar compras...</div>
      ) : sales.length === 0 ? (
        <div className="status-message">Ainda nao existem compras.</div>
      ) : (
        <div className="cart-list">
          {sales.map((sale) => (
            <article className="cart-item" key={sale.id}>
              <div>
                <strong>Venda #{sale.id}</strong>
                <span>{new Date(sale.createdAt).toLocaleString("pt-PT")}</span>
                {sale.invoice && <span>Fatura {sale.invoice.invoiceNumber}</span>}
              </div>
              <strong>{sale.total.toFixed(2)} EUR</strong>
              <span>{sale.status}</span>
              {sale.invoice && <a className="secondary-link" href={`/orders/${sale.id}/invoice`}>Fatura</a>}
            </article>
          ))}
        </div>
      )}
    </section>
  );
}
