import { useCart } from "../contexts/CartContext";
import { getCategoryBg, getCategoryColor, getCategoryIcon } from "../utils/categoryUtils";

// Página do carrinho que lista os produtos adicionados a partir do CartContext, permite atualizar quantidades ou remover itens, e mostra um resumo com o total e o botão para avançar para o checkout.
export function CartPage() {
  const { items, total, updateQuantity, removeProduct } = useCart();

  if (items.length === 0) {
    return (
      <section className="cart-page">
        <div className="page-heading"><div><h1>Carrinho</h1><p>O carrinho esta vazio.</p></div></div>
        <div className="status-message">
          Ainda nao adicionaste nenhum produto.{" "}
          <a className="secondary-link" href="/catalog">Explorar produtos</a>
        </div>
      </section>
    );
  }

  return (
    <section className="cart-page">
      <div className="page-heading">
        <div>
          <h1>Carrinho</h1>
          <p>{items.length} produto(s) selecionado(s)</p>
        </div>
      </div>

      <div className="cart-list">
        {items.map((item) => (
          <article className="cart-item" key={item.product.id}>
            {item.product.imageUrl ? (
              <img className="cart-item__img" src={item.product.imageUrl} alt={item.product.name} />
            ) : (
              <div
                className="cart-item__img-placeholder"
                style={{ background: getCategoryBg(item.product.categoryName) }}
              >
                <span style={{ fontWeight: 900, color: getCategoryColor(item.product.categoryName) }}>
                  {getCategoryIcon(item.product.categoryName)}
                </span>
              </div>
            )}

            <div className="cart-item__info">
              <span className="cart-item__cat">{item.product.categoryName}</span>
              <strong className="cart-item__name">
                <a href={`/products/${item.product.id}`}>{item.product.name}</a>
              </strong>
              <span className="cart-item__price">{item.product.price.toFixed(2)} € / un.</span>
            </div>

            <input
              className="cart-item__qty"
              type="number"
              min="1"
              max={item.product.stock}
              value={item.quantity}
              onChange={(e) => updateQuantity(item.product.id, Number(e.target.value))}
            />

            <strong className="cart-item__subtotal">
              {(item.product.price * item.quantity).toFixed(2)} €
            </strong>

            <button
              type="button"
              className="secondary-button"
              style={{ height: 36, fontSize: 13 }}
              onClick={() => removeProduct(item.product.id)}
            >
              Remover
            </button>
          </article>
        ))}
      </div>

      <div className="cart-summary">
        <span style={{ fontSize: 14, color: "var(--text-muted)" }}>Total a pagar:</span>
        <strong className="cart-summary__total">{total.toFixed(2)} €</strong>
        <a className="primary-link" href="/checkout">Finalizar compra</a>
      </div>
    </section>
  );
}
