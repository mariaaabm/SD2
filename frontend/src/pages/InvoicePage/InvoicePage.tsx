import { useEffect, useState } from "react";
import { getSale, type Sale } from "../../services/sale.service";

type InvoicePageProps = {
  saleId: number;
};

export function InvoicePage({ saleId }: InvoicePageProps) {
  const [sale, setSale] = useState<Sale | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadSale() {
      try {
        setSale(await getSale(saleId));
      } catch {
        setError("Nao foi possivel carregar a fatura.");
      } finally {
        setLoading(false);
      }
    }

    loadSale();
  }, [saleId]);

  if (loading) {
    return (
      <section className="invoice-page">
        <div className="status-message">A carregar fatura...</div>
      </section>
    );
  }

  if (error || !sale || !sale.invoice) {
    return (
      <section className="invoice-page">
        <div className="status-message status-message--error">{error ?? "Fatura nao encontrada."}</div>
        <a className="secondary-link" href="/orders">Voltar as compras</a>
      </section>
    );
  }

  return (
    <section className="invoice-page">
      <div className="invoice-document">
        <div className="invoice-document__header">
          <div>
            <span>Shopping Food Store</span>
            <h1>Fatura {sale.invoice.invoiceNumber}</h1>
          </div>
          <button className="secondary-button print-button" type="button" onClick={() => window.print()}>
            Imprimir
          </button>
        </div>

        <div className="invoice-meta">
          <div>
            <span>Cliente</span>
            <strong>{sale.customerName}</strong>
            <small>Cliente #{sale.customerId}</small>
          </div>
          <div>
            <span>Venda</span>
            <strong>#{sale.id}</strong>
            <small>{new Date(sale.createdAt).toLocaleString("pt-PT")}</small>
          </div>
          <div>
            <span>Emissao</span>
            <strong>{new Date(sale.invoice.issuedAt).toLocaleDateString("pt-PT")}</strong>
            <small>{sale.status}</small>
          </div>
        </div>

        <div className="invoice-lines">
          <div className="invoice-line invoice-line--head">
            <span>Produto</span>
            <span>Qtd.</span>
            <span>Preco</span>
            <span>Total</span>
          </div>
          {sale.items.map((item) => (
            <div className="invoice-line" key={item.id}>
              <strong>{item.productName}</strong>
              <span>{item.quantity}</span>
              <span>{item.unitPrice.toFixed(2)} EUR</span>
              <strong>{item.subtotal.toFixed(2)} EUR</strong>
            </div>
          ))}
        </div>

        <div className="invoice-total">
          <span>Total</span>
          <strong>{sale.total.toFixed(2)} EUR</strong>
        </div>
      </div>

      <a className="secondary-link" href="/orders">Voltar as compras</a>
    </section>
  );
}
