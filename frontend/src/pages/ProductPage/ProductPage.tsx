import { useEffect, useState } from "react";
import { useCart } from "../../contexts/CartContext";
import { getProduct, type Product } from "../../services/product.service";

type ProductPageProps = {
  productId: number;
};

export function ProductPage({ productId }: ProductPageProps) {
  const { addProduct } = useCart();
  const [product, setProduct] = useState<Product | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadProduct() {
      try {
        setProduct(await getProduct(productId));
      } catch {
        setError("Nao foi possivel carregar o produto.");
      } finally {
        setLoading(false);
      }
    }

    loadProduct();
  }, [productId]);

  if (loading) {
    return <div className="status-message">A carregar produto...</div>;
  }

  if (error || !product) {
    return <div className="status-message status-message--error">{error || "Produto nao encontrado."}</div>;
  }

  const available = product.active && product.stock > 0;

  return (
    <section className="product-page">
      <a className="secondary-link" href="/catalog">Voltar ao catalogo</a>
      <div className="product-detail">
        <span>{product.categoryName}</span>
        <h1>{product.name}</h1>
        <p>{product.description || "Sem descricao disponivel."}</p>
        <strong>{product.price.toFixed(2)} EUR</strong>
        <p>{product.stock} unidades em stock</p>
        <button type="button" disabled={!available} onClick={() => addProduct(product)}>
          {available ? "Adicionar ao carrinho" : "Indisponivel"}
        </button>
      </div>
    </section>
  );
}
