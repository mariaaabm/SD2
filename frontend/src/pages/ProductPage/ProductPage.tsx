import { useEffect, useState } from "react";
import { useCart } from "../../contexts/CartContext";
import { getProduct, type Product } from "../../services/product.service";
import { getCategoryBg, getCategoryColor, getCategoryIcon } from "../../utils/categoryUtils";

type ProductPageProps = { productId: number };

export function ProductPage({ productId }: ProductPageProps) {
  const { addProduct } = useCart();
  const [product, setProduct] = useState<Product | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [added, setAdded] = useState(false);

  useEffect(() => {
    getProduct(productId)
      .then(setProduct)
      .catch(() => setError("Nao foi possivel carregar o produto."))
      .finally(() => setLoading(false));
  }, [productId]);

  if (loading) return <div className="status-message">A carregar produto...</div>;
  if (error || !product) return <div className="status-message status-message--error">{error || "Produto nao encontrado."}</div>;

  const available = product.active && product.stock > 0;

  function handleAdd() {
    if (!product) return;
    addProduct(product);
    setAdded(true);
    setTimeout(() => setAdded(false), 2000);
  }

  return (
    <section className="product-page">
      <a className="secondary-link" href="/catalog">← Voltar ao catalogo</a>

      <div className="product-detail">
        <div className="product-detail__image-wrap">
          {product.imageUrl ? (
            <img src={product.imageUrl} alt={product.name} />
          ) : (
            <div
              className="product-detail__placeholder"
              style={{ background: getCategoryBg(product.categoryName) }}
            >
              <span style={{ fontWeight: 900, color: getCategoryColor(product.categoryName) }}>
                {getCategoryIcon(product.categoryName)}
              </span>
            </div>
          )}
        </div>

        <div className="product-detail__info">
          <span className="product-detail__category">{product.categoryName}</span>
          <h1 className="product-detail__name">{product.name}</h1>

          {product.description && (
            <p className="product-detail__desc">{product.description}</p>
          )}

          <div className="product-detail__price">
            {product.price.toFixed(2)} €
            <span> / unidade</span>
          </div>

          <p className="product-detail__stock">
            {available
              ? <><strong>{product.stock}</strong> unidades em stock</>
              : <span style={{ color: "#dc2626" }}>Produto indisponivel</span>}
          </p>

          <div className="action-row" style={{ marginTop: 8 }}>
            <button
              type="button"
              className="btn-add"
              style={{ height: 48, padding: "0 28px", fontSize: 15 }}
              disabled={!available}
              onClick={handleAdd}
            >
              {added ? "Adicionado!" : available ? "+ Adicionar ao carrinho" : "Indisponivel"}
            </button>
            <a className="primary-link" href="/cart">Ver carrinho</a>
          </div>
        </div>
      </div>
    </section>
  );
}
