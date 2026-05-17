import { useEffect, useState } from "react";
import { listAdminSales, type Sale } from "../../services/sale.service";

export function AdminSalesPage() {
  const [sales, setSales] = useState<Sale[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadSales() {
      try {
        setSales(await listAdminSales());
      } catch {
        setError("Nao foi possivel carregar as vendas.");
      } finally {
        setLoading(false);
      }
    }

    loadSales();
  }, []);

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Vendas</h1>
          <p>Consulta global das compras realizadas na loja.</p>
        </div>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      {loading ? (
        <div className="status-message">A carregar vendas...</div>
      ) : sales.length === 0 ? (
        <div className="status-message">Ainda nao existem vendas.</div>
      ) : (
        <div className="admin-table">
          {sales.map((sale) => (
            <div className="admin-row admin-row--sale" key={sale.id}>
              <div>
                <strong>Venda #{sale.id}</strong>
                <span>{new Date(sale.createdAt).toLocaleString("pt-PT")}</span>
                {sale.invoice && <span>Fatura {sale.invoice.invoiceNumber}</span>}
              </div>
              <div>
                <strong>{sale.customerName}</strong>
                <span>Cliente #{sale.customerId}</span>
              </div>
              <span>{sale.items.length} artigo(s)</span>
              <span>{sale.status}</span>
              <strong>{sale.total.toFixed(2)} EUR</strong>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
