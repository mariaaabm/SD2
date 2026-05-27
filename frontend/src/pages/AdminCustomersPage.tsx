import { useEffect, useState } from "react";
import { apiClient } from "../api/apiClient";
import type { UserRole } from "../services/auth.service";

type CustomerRow = {
  id: number;
  name: string;
  email: string;
  role: UserRole;
};

// Página de administração que lista todos os clientes registados na loja com nome, email e role, suporta pesquisa local por qualquer um destes campos para filtrar resultados sem ter de voltar a chamar o backend.
export function AdminCustomersPage() {
  const [customers, setCustomers] = useState<CustomerRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState("");

  useEffect(() => {
    apiClient.get<CustomerRow[]>("/admin/customers")
      .then((r) => setCustomers(r.data))
      .catch(() => setError("Não foi possível carregar os clientes."))
      .finally(() => setLoading(false));
  }, []);

  // Filtragem local no cliente — eficiente porque a lista de clientes é carregada de uma vez
  // e o número de utilizadores tipicamente não é suficientemente grande para exigir paginação.
  const filtered = customers.filter((c) =>
    search.trim() === "" ||
    c.name.toLowerCase().includes(search.toLowerCase()) ||
    c.email.toLowerCase().includes(search.toLowerCase()) ||
    String(c.id).includes(search)
  );

  const clientCount = customers.filter((c) => c.role === "CLIENT").length;
  const adminCount  = customers.filter((c) => c.role === "ADMIN").length;

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Clientes</h1>
          <p>
            {clientCount} cliente(s) · {adminCount} administrador(es)
          </p>
        </div>
        <input
          className="admin-search"
          type="text"
          placeholder="Pesquisar por nome, email ou ID…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}

      {loading ? (
        <div className="status-message">A carregar clientes…</div>
      ) : filtered.length === 0 ? (
        <div className="status-message">Nenhum cliente encontrado.</div>
      ) : (
        <div className="admin-table">
          {filtered.map((c) => (
            <div key={c.id} className="admin-row admin-row--customer">
              <div>
                <strong>{c.name}</strong>
                <span>{c.email}</span>
              </div>
              <span className="customer-id">#{c.id}</span>
              <span className={`badge ${c.role === "ADMIN" ? "badge--inactive" : "badge--active"}`}>
                {c.role === "ADMIN" ? "Admin" : "Cliente"}
              </span>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
