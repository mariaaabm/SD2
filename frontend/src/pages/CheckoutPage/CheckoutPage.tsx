import { useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useCart } from "../../contexts/CartContext";
import { checkout, type Sale } from "../../services/sale.service";

export function CheckoutPage() {
  const { isAuthenticated } = useAuth();
  const { items, total, clear } = useCart();
  const [sale, setSale] = useState<Sale | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleCheckout() {
    setError(null);
    setLoading(true);

    try {
      const response = await checkout(items.map((item) => ({
        productId: item.product.id,
        quantity: item.quantity
      })));
      setSale(response);
      clear();
    } catch {
      setError("Nao foi possivel finalizar a compra. Confirma o login e o stock disponivel.");
    } finally {
      setLoading(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <section className="checkout-page">
        <div className="status-message status-message--error">Tens de entrar antes de finalizar a compra.</div>
        <a className="primary-link" href="/login">Entrar</a>
      </section>
    );
  }

  if (sale) {
    return (
      <section className="checkout-page">
        <div className="invoice-box">
          <h1>Compra concluida</h1>
          <p>Venda #{sale.id}</p>
          <p>Total: {sale.total.toFixed(2)} EUR</p>
          {sale.invoice && (
            <>
              <p>Fatura: {sale.invoice.invoiceNumber}</p>
              <p>Emitida em {new Date(sale.invoice.issuedAt).toLocaleString("pt-PT")}</p>
            </>
          )}
        </div>
        <div className="action-row">
          {sale.invoice && <a className="primary-link" href={`/orders/${sale.id}/invoice`}>Ver fatura</a>}
          <a className="secondary-link" href="/orders">Ver compras</a>
        </div>
      </section>
    );
  }

  return (
    <section className="checkout-page">
      <div className="page-heading">
        <div>
          <h1>Checkout</h1>
          <p>Pagamento considerado no momento da entrega.</p>
        </div>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      <div className="cart-summary">
        <strong>Total: {total.toFixed(2)} EUR</strong>
        <button type="button" disabled={loading || items.length === 0} onClick={handleCheckout}>
          {loading ? "A finalizar..." : "Confirmar compra"}
        </button>
      </div>
    </section>
  );
}
