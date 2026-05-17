import type { Product } from "../../services/product.service";

type ProductCardProps = {
  product: Product;
  onAdd?: (product: Product) => void;
};

export function ProductCard({ product, onAdd }: ProductCardProps) {
  const isAvailable = product.active && product.stock > 0;

  return (
    <article className="product-card">
      <div className="product-card__content">
        <span className="product-card__category">{product.categoryName}</span>
        <h2><a href={`/products/${product.id}`}>{product.name}</a></h2>
        <p>{product.description || "Sem descricao disponivel."}</p>
      </div>
      <div className="product-card__footer">
        <div>
          <strong>{product.price.toFixed(2)} EUR</strong>
          <span>{product.stock} em stock</span>
        </div>
        <button type="button" disabled={!isAvailable} onClick={() => onAdd?.(product)}>
          {isAvailable ? "Adicionar" : "Indisponivel"}
        </button>
      </div>
    </article>
  );
}
