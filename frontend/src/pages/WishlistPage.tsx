import { useEffect, useState } from "react";
import { useCart } from "../contexts/CartContext";
import { useWishlist } from "../contexts/WishlistContext";
import { getWishlist, type WishlistItem } from "../services/wishlist.service";
import { getCategoryBg, getCategoryColor, getCategoryIcon } from "../utils/categoryUtils";

// Página dedicada aos favoritos do utilizador autenticado, carrega a lista completa via /wishlist, permite remover diretamente ou adicionar ao carrinho a partir da própria página, e mostra um estado vazio com ligação para o catálogo quando a lista está vazia.
export function WishlistPage() {
  const [items, setItems] = useState<WishlistItem[]>([]);
  const [loading, setLoading] = useState(true);
  const { toggle } = useWishlist();
  const { addProduct } = useCart();

  useEffect(() => {
    getWishlist()
      .then(setItems)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  async function handleRemove(productId: number) {
    await toggle(productId);
    setItems((prev) => prev.filter((i) => i.productId !== productId));
  }

  if (loading) return <div className="status-message">A carregar favoritos…</div>;

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Os meus favoritos</h1>
          <p>{items.length} produto(s) guardado(s)</p>
        </div>
        {items.length > 0 && <a href="/catalog" className="secondary-link">Continuar a comprar</a>}
      </div>

      {items.length === 0 ? (
        <div className="empty-results">
          <div className="empty-results__icon">♡</div>
          <h2 className="empty-results__title">Sem favoritos ainda</h2>
          <p className="empty-results__desc">Clica no ♡ em qualquer produto para o adicionar aos favoritos.</p>
          <a href="/catalog" className="empty-results__clear" style={{ textDecoration: "none", display: "inline-block" }}>
            Ver catálogo
          </a>
        </div>
      ) : (
        <div className="wishlist-grid">
          {items.map((item) => (
            <div key={item.id} className="wishlist-card">
              <a href={`/products/${item.productId}`} className="wishlist-card__img-wrap">
                {item.imageUrl ? (
                  <img src={item.imageUrl} alt={item.productName} loading="lazy"
                    onError={(e) => {
                      e.currentTarget.style.display = "none";
                      const fb = e.currentTarget.nextElementSibling as HTMLElement | null;
                      if (fb) fb.style.display = "flex";
                    }}
                  />
                ) : null}
                <div className="wishlist-card__img-fallback"
                  style={{ background: getCategoryBg(item.categoryName), display: item.imageUrl ? "none" : "flex" }}>
                  <span style={{ fontSize: 36, color: getCategoryColor(item.categoryName) }}>
                    {getCategoryIcon(item.categoryName)}
                  </span>
                </div>
              </a>

              <div className="wishlist-card__body">
                <span className="product-card__category">{item.categoryName}</span>
                <a href={`/products/${item.productId}`} className="wishlist-card__name">{item.productName}</a>
                <div className="wishlist-card__price">{item.price.toFixed(2)} €</div>

                <div className="wishlist-card__actions">
                  <button
                    type="button"
                    className="btn-add"
                    disabled={!item.active || item.stock === 0}
                    onClick={() => addProduct({
                      id: item.productId, name: item.productName,
                      price: item.price, stock: item.stock,
                      active: item.active, categoryId: 0,
                      categoryName: item.categoryName, imageUrl: item.imageUrl,
                    })}
                  >
                    + Carrinho
                  </button>
                  <button
                    type="button"
                    className="btn-wishlist btn-wishlist--active"
                    title="Remover dos favoritos"
                    onClick={() => handleRemove(item.productId)}
                  >
                    ♥
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
