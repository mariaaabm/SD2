import { useEffect, useState } from "react";
import { getSale, type Sale } from "../../services/sale.service";

type InvoicePageProps = { saleId: number };

const PAYMENT_LABELS: Record<string, string> = {
  CARD:       "Cartao de credito / debito",
  MBWAY:      "MB Way",
  MULTIBANCO: "Referencia Multibanco",
  COD:        "Pagamento na entrega",
};

function money(value: number, currency = "EUR") {
  return new Intl.NumberFormat("pt-PT", { style: "currency", currency }).format(value);
}

function ratePct(rate: number) {
  return `${rate.toFixed(rate % 1 === 0 ? 0 : 2)}%`;
}

export function InvoicePage({ saleId }: InvoicePageProps) {
  const [sale, setSale] = useState<Sale | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getSale(saleId)
      .then(setSale)
      .catch(() => setError("Nao foi possivel carregar a fatura."))
      .finally(() => setLoading(false));
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
        <div className="status-message status-message--error">
          {error ?? "Fatura nao encontrada."}
        </div>
        <a className="secondary-link" href="/orders">Voltar as compras</a>
      </section>
    );
  }

  const invoice = sale.invoice;
  const issuer = invoice.issuer;
  const customer = invoice.customer;
  const shipping = invoice.shippingTo;
  const currency = invoice.currency || "EUR";
  const issued = new Date(invoice.issuedAt);
  const operation = new Date(invoice.operationDate ?? sale.createdAt);
  const paymentLabel =
    invoice.paymentMethod && PAYMENT_LABELS[invoice.paymentMethod]
      ? PAYMENT_LABELS[invoice.paymentMethod]
      : invoice.paymentMethod ?? "—";

  const shippingDiffers =
    shipping && shipping.address && shipping.address !== customer.address;

  return (
    <section className="invoice-page">
      <div className="invoice-document">
        {/* ── Cabecalho ─────────────────────────────────────── */}
        <header className="invoice-document__header">
          <div>
            <div className="brand">
              Sport<span style={{ color: "var(--orange)" }}>Flow</span>
            </div>
            <h1>{invoice.documentType} {invoice.series}{" "}
              {invoice.invoiceNumber.replace(invoice.series, "").replace(/^\s*/, "")}
            </h1>
          </div>
          <button
            className="secondary-button print-button"
            type="button"
            onClick={() => window.print()}
          >
            Imprimir / Guardar PDF
          </button>
        </header>

        {/* ── Emitente vs Adquirente ────────────────────────── */}
        <div className="invoice-parties">
          <div className="invoice-party">
            <span className="invoice-party__label">Emitente</span>
            <strong>{issuer.companyName}</strong>
            <div>NIF: {issuer.taxId}</div>
            <div>{issuer.address}</div>
            <div>{issuer.postalCode} {issuer.city} — {issuer.country}</div>
            <div className="invoice-party__contacts">
              <span>{issuer.phone}</span>
              <span>{issuer.email}</span>
              <span>{issuer.website}</span>
            </div>
            <small>Capital social {issuer.shareCapital} · {issuer.registryInfo}</small>
          </div>

          <div className="invoice-party">
            <span className="invoice-party__label">Adquirente</span>
            <strong>{customer.name ?? "—"}</strong>
            <div>NIF: {customer.taxId ?? "Consumidor Final"}</div>
            {customer.address && <div>{customer.address}{customer.address2 ? `, ${customer.address2}` : ""}</div>}
            {(customer.postalCode || customer.city) && (
              <div>
                {customer.postalCode ?? ""} {customer.city ?? ""}
                {customer.country ? ` — ${customer.country}` : ""}
              </div>
            )}
            <div className="invoice-party__contacts">
              {customer.email && <span>{customer.email}</span>}
              {customer.phone && <span>{customer.phone}</span>}
            </div>
          </div>
        </div>

        {/* ── Metadados do documento ────────────────────────── */}
        <div className="invoice-meta">
          <div>
            <span>Encomenda</span>
            <strong>#{sale.id}</strong>
            <small>Estado: {sale.status}</small>
          </div>
          <div>
            <span>Data de emissao</span>
            <strong>{issued.toLocaleDateString("pt-PT")}</strong>
            <small>{issued.toLocaleTimeString("pt-PT", { hour: "2-digit", minute: "2-digit" })}</small>
          </div>
          <div>
            <span>Data da operacao</span>
            <strong>{operation.toLocaleDateString("pt-PT")}</strong>
            <small>Moeda: {currency}</small>
          </div>
        </div>

        {/* ── Linhas da fatura ──────────────────────────────── */}
        <div className="invoice-lines invoice-lines--detailed">
          <div className="invoice-line invoice-line--head">
            <span>Descricao</span>
            <span>Qtd.</span>
            <span>Preco unit. s/IVA</span>
            <span>Taxa IVA</span>
            <span>IVA</span>
            <span>Total c/IVA</span>
          </div>
          {invoice.lines.map((line) => (
            <div className="invoice-line" key={line.productId}>
              <strong>{line.description}</strong>
              <span>{line.quantity}</span>
              <span>{money(line.unitPriceNet, currency)}</span>
              <span>{ratePct(line.vatRate)}</span>
              <span>{money(line.vatAmount, currency)}</span>
              <strong>{money(line.lineGross, currency)}</strong>
            </div>
          ))}
        </div>

        {/* ── Resumo de IVA ─────────────────────────────────── */}
        <div className="invoice-vat-summary">
          <div className="invoice-vat-summary__title">Resumo de IVA</div>
          <table>
            <thead>
              <tr>
                <th>Taxa</th>
                <th>Base tributavel</th>
                <th>Montante de IVA</th>
              </tr>
            </thead>
            <tbody>
              {invoice.vatSummary.map((row) => (
                <tr key={row.rate}>
                  <td>{ratePct(row.rate)}</td>
                  <td>{money(row.base, currency)}</td>
                  <td>{money(row.amount, currency)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* ── Totais ────────────────────────────────────────── */}
        <div className="invoice-totals">
          <div className="invoice-totals__row">
            <span>Subtotal (sem IVA)</span>
            <span>{money(invoice.subtotal, currency)}</span>
          </div>
          <div className="invoice-totals__row">
            <span>Total IVA</span>
            <span>{money(invoice.vatTotal, currency)}</span>
          </div>
          <div className="invoice-totals__row invoice-totals__row--grand">
            <strong>Total a pagar</strong>
            <strong>{money(invoice.total, currency)}</strong>
          </div>
        </div>

        {/* ── Pagamento + entrega ───────────────────────────── */}
        <div className="invoice-footer-grid">
          <div className="invoice-footer-block">
            <span>Forma de pagamento</span>
            <strong>{paymentLabel}</strong>
            {invoice.paymentTerms && <small>Condicoes: {invoice.paymentTerms}</small>}
          </div>

          {shippingDiffers && shipping && (
            <div className="invoice-footer-block">
              <span>Morada de entrega</span>
              <strong>{shipping.name ?? customer.name}</strong>
              {shipping.address && (
                <div>
                  {shipping.address}
                  {shipping.address2 ? `, ${shipping.address2}` : ""}
                </div>
              )}
              <div>
                {shipping.postalCode} {shipping.city}
                {shipping.country ? ` — ${shipping.country}` : ""}
              </div>
            </div>
          )}
        </div>

      </div>

      <a className="secondary-link" href="/orders">← Voltar as compras</a>
    </section>
  );
}
