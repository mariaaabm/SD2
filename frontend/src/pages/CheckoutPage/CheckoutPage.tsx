import { useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useCart } from "../../contexts/CartContext";
import { checkout, type Sale } from "../../services/sale.service";
import type { AxiosError } from "axios";

type ApiError = { messages: string[] };

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
        quantity: item.quantity,
      })));
      setSale(response);
      clear();
    } catch (err: unknown) {
      const axiosErr = err as AxiosError<ApiError>;
      const msgs = axiosErr?.response?.data?.messages;
      if (msgs && msgs.length > 0) {
        setError(msgs[0]);
      } else {
        setError("Nao foi possivel finalizar a compra. Confirma o login e o stock disponivel.");
      }
    } finally {
      setLoading(false);
    }
  }

  if (!isAuthenticated) {
    return (
      <section className="checkout-page">
        <div className="status-message status-message--error">
          Tens de entrar antes de finalizar a compra.
        </div>
        <a className="primary-link" href="/login">Entrar</a>
      </section>
    );
  }

  if (items.length === 0 && !sale) {
    return (
      <section className="checkout-page">
        <div className="page-heading"><h1>Checkout</h1></div>
        <div className="status-message">O teu carrinho esta vazio.</div>
        <a className="primary-link" href="/catalog">Ver produtos</a>
      </section>
    );
  }

  if (sale) {
    return (
      <section className="checkout-page">
        <div className="checkout-success">
          <div className="checkout-success__icon">✓</div>
          <h1>Compra concluida!</h1>
          <p>Obrigado pela tua compra. A tua encomenda #{sale.id} foi registada.</p>
          <div className="checkout-success__total">
            Total pago: <strong>{sale.total.toFixed(2)} €</strong>
          </div>
          {sale.invoice && (
            <div className="checkout-success__invoice">
              Fatura <strong>{sale.invoice.invoiceNumber}</strong> emitida em{" "}
              {new Date(sale.invoice.issuedAt).toLocaleDateString("pt-PT")}
            </div>
          )}
          <div className="action-row">
            {sale.invoice && (
              <a className="primary-link" href={`/orders/${sale.id}/invoice`}>
                Ver fatura
              </a>
            )}
            <a className="secondary-link" href="/orders">Ver todas as compras</a>
            <a className="secondary-link" href="/catalog">Continuar a comprar</a>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section className="checkout-page">
      <div className="page-heading">
        <h1>Checkout</h1>
        <p>Confirma os artigos antes de finalizar.</p>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}

      <div className="checkout-items">
        {items.map((item) => (
          <div className="checkout-item" key={item.product.id}>
            {item.product.imageUrl && (
              <img
                className="checkout-item__img"
                src={item.product.imageUrl}
                alt={item.product.name}
                onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }}
              />
            )}
            <div className="checkout-item__info">
              <strong>{item.product.name}</strong>
              <span className="checkout-item__category">{item.product.categoryName}</span>
            </div>
            <div className="checkout-item__qty">× {item.quantity}</div>
            <div className="checkout-item__price">
              {(item.product.price * item.quantity).toFixed(2)} €
            </div>
          </div>
        ))}
      </div>

      <div className="checkout-summary">
        <div className="checkout-summary__row">
          <span>Artigos ({items.reduce((s, i) => s + i.quantity, 0)})</span>
          <span>{total.toFixed(2)} €</span>
        </div>
        <div className="checkout-summary__row checkout-summary__row--total">
          <strong>Total</strong>
          <strong>{total.toFixed(2)} €</strong>
        </div>
        <p className="checkout-summary__note">Pagamento na entrega · Envio gratuito</p>
        <button
          className="checkout-summary__btn"
          type="button"
          disabled={loading}
          onClick={handleCheckout}
        >
          {loading ? "A finalizar..." : "Confirmar compra"}
        </button>
        <a className="secondary-link" href="/cart">← Voltar ao carrinho</a>
      </div>
    </section>
  );
}
