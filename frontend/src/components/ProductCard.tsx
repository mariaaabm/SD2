import { useWishlist } from "../contexts/WishlistContext";
import type { Product } from "../services/product.service";
import { getCategoryBg, getCategoryColor, getCategoryIcon } from "../utils/categoryUtils";

type ProductCardProps = {
  product: Product;
  onAdd?: (product: Product) => void;
};

// Renderiza a card de um produto no catálogo com imagem, categoria, nome, descrição, preço e stock, mostra um placeholder colorido específico da categoria quando a imagem falha a carregar, e expõe ainda o botão de favorito e o botão de adicionar ao carrinho desativado quando o produto não está disponível.
export function ProductCard({ product, onAdd }: ProductCardProps) {
  const isAvailable = product.active && product.stock > 0;
  const { toggle, isWishlisted } = useWishlist();
  const wishlisted = isWishlisted(product.id);

  return (
    <article className="product-card">
      <a href={`/products/${product.id}`} className="product-card__image-wrap">
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.name}
            loading="lazy"
            onError={(e) => {
              const el = e.currentTarget;
              el.style.display = "none";
              const placeholder = el.nextElementSibling as HTMLElement | null;
              if (placeholder) placeholder.style.display = "flex";
            }}
          />
        ) : null}
        {product.imageUrl ? (
          <div
            className="product-card__placeholder"
            style={{ background: getCategoryBg(product.categoryName), display: "none" }}
          >
            <span style={{ fontWeight: 900, fontSize: 40, color: getCategoryColor(product.categoryName) }}>
              {getCategoryIcon(product.categoryName)}
            </span>
          </div>
        ) : (
          <div
            className="product-card__placeholder"
            style={{ background: getCategoryBg(product.categoryName) }}
          >
            <span style={{ fontWeight: 900, fontSize: 40, color: getCategoryColor(product.categoryName) }}>
              {getCategoryIcon(product.categoryName)}
            </span>
          </div>
        )}

        <button
          className={`btn-wishlist${wishlisted ? " btn-wishlist--active" : ""}`}
          type="button"
          title={wishlisted ? "Remover dos favoritos" : "Adicionar aos favoritos"}
          onClick={(e) => { e.preventDefault(); toggle(product.id); }}
        >
          {wishlisted ? "♥" : "♡"}
        </button>
      </a>

      <div className="product-card__body">
        <span className="product-card__category">{product.categoryName}</span>
        <div className="product-card__name">
          <a href={`/products/${product.id}`}>{product.name}</a>
        </div>
        {product.description && (
          <p className="product-card__desc">{product.description}</p>
        )}

        <div className="product-card__footer">
          <div>
            <div className="product-card__price">{product.price.toFixed(2)} €</div>
            {isAvailable ? (
              <div className="product-card__stock">{product.stock} em stock</div>
            ) : (
              <div className="product-card__stock" style={{ color: "#dc2626" }}>Indisponivel</div>
            )}
          </div>
          <button
            className="btn-add"
            type="button"
            disabled={!isAvailable}
            onClick={(e) => { e.preventDefault(); onAdd?.(product); }}
          >
            + Adicionar
          </button>
        </div>
      </div>
    </article>
  );
}
