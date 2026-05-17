import { useEffect, useState } from "react";
import {
  getBestCustomers,
  getLeastSellingProducts,
  getRevenue,
  getTopSellingProducts,
  type StatsCustomer,
  type StatsProduct,
  type StatsRevenue
} from "../../services/stats.service";

export function AdminStatsPage() {
  const [topProducts, setTopProducts] = useState<StatsProduct[]>([]);
  const [leastProducts, setLeastProducts] = useState<StatsProduct[]>([]);
  const [bestCustomers, setBestCustomers] = useState<StatsCustomer[]>([]);
  const [revenue, setRevenue] = useState<StatsRevenue | null>(null);
  const [period, setPeriod] = useState<"day" | "week" | "month">("day");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadStats() {
      setLoading(true);
      setError(null);

      try {
        const [top, least, customers, revenueData] = await Promise.all([
          getTopSellingProducts(),
          getLeastSellingProducts(),
          getBestCustomers(),
          getRevenue(period)
        ]);
        setTopProducts(top);
        setLeastProducts(least);
        setBestCustomers(customers);
        setRevenue(revenueData);
      } catch {
        setError("Nao foi possivel carregar as estatisticas. Confirma que tens permissao de administrador.");
      } finally {
        setLoading(false);
      }
    }

    loadStats();
  }, [period]);

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Estatisticas</h1>
          <p>Resumo operacional da loja.</p>
        </div>
        <label className="filter-control">
          <span>Periodo</span>
          <select value={period} onChange={(event) => setPeriod(event.target.value as "day" | "week" | "month")}>
            <option value="day">Dia</option>
            <option value="week">Semana</option>
            <option value="month">Mes</option>
          </select>
        </label>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      {loading ? (
        <div className="status-message">A carregar estatisticas...</div>
      ) : (
        <>
          {revenue && (
            <div className="stats-card">
              <span>Faturacao</span>
              <strong>{revenue.revenue.toFixed(2)} EUR</strong>
              <p>{revenue.periodStart} a {revenue.periodEnd}</p>
            </div>
          )}

          <StatsTable title="Produtos mais vendidos" rows={topProducts} />
          <StatsTable title="Produtos menos vendidos" rows={leastProducts} />

          <section className="stats-section">
            <h2>Melhores clientes</h2>
            <div className="stats-table">
              {bestCustomers.map((customer) => (
                <div className="stats-row" key={customer.customerId}>
                  <span>{customer.customerName}</span>
                  <span>{customer.totalPurchases} compras</span>
                  <strong>{customer.totalSpent.toFixed(2)} EUR</strong>
                </div>
              ))}
            </div>
          </section>
        </>
      )}
    </section>
  );
}

type StatsTableProps = {
  title: string;
  rows: StatsProduct[];
};

function StatsTable({ title, rows }: StatsTableProps) {
  return (
    <section className="stats-section">
      <h2>{title}</h2>
      <div className="stats-table">
        {rows.map((product) => (
          <div className="stats-row" key={product.productId}>
            <span>{product.productName}</span>
            <strong>{product.quantitySold} unidades</strong>
          </div>
        ))}
      </div>
    </section>
  );
}
