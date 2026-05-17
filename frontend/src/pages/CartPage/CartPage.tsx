import { useCart } from "../../contexts/CartContext";

export function CartPage() {
  const { items, total, updateQuantity, removeProduct } = useCart();

  if (items.length === 0) {
    return (
      <section className="cart-page">
        <div className="page-heading">
          <div>
            <h1>Carrinho</h1>
            <p>O carrinho esta vazio.</p>
          </div>
        </div>
        <a className="primary-link" href="/catalog">Ver catalogo</a>
      </section>
    );
  }

  return (
    <section className="cart-page">
      <div className="page-heading">
        <div>
          <h1>Carrinho</h1>
          <p>Confirma os produtos antes de finalizar a compra.</p>
        </div>
      </div>

      <div className="cart-list">
        {items.map((item) => (
          <article className="cart-item" key={item.product.id}>
            <div>
              <strong>{item.product.name}</strong>
              <span>{item.product.price.toFixed(2)} EUR cada</span>
            </div>
            <input
              type="number"
              min="1"
              max={item.product.stock}
              value={item.quantity}
              onChange={(event) => updateQuantity(item.product.id, Number(event.target.value))}
            />
            <strong>{(item.product.price * item.quantity).toFixed(2)} EUR</strong>
            <button type="button" className="secondary-button" onClick={() => removeProduct(item.product.id)}>
              Remover
            </button>
          </article>
        ))}
      </div>

      <div className="cart-summary">
        <strong>Total: {total.toFixed(2)} EUR</strong>
        <a className="primary-link" href="/checkout">Finalizar compra</a>
      </div>
    </section>
  );
}
