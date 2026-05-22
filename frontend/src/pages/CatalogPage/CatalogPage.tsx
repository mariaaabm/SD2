import { useEffect, useMemo, useState } from "react";
import { ProductCard } from "../../components/ProductCard/ProductCard";
import { useCart } from "../../contexts/CartContext";
import { listCategories, type Category } from "../../services/category.service";
import { listProducts, type Product } from "../../services/product.service";

export function CatalogPage() {
  const { addProduct } = useCart();
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let ignore = false;

    async function loadCategories() {
      try {
        const data = await listCategories();
        if (!ignore) {
          setCategories(data);
        }
      } catch {
        if (!ignore) {
          setError("Nao foi possivel carregar as categorias.");
        }
      }
    }

    loadCategories();

    return () => {
      ignore = true;
    };
  }, []);

  useEffect(() => {
    let ignore = false;

    async function loadProducts() {
      setLoading(true);
      setError(null);

      try {
        const data = await listProducts({
          categoryId: selectedCategoryId,
          activeOnly: true
        });

        if (!ignore) {
          setProducts(data);
        }
      } catch {
        if (!ignore) {
          setError("Nao foi possivel carregar os produtos.");
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    loadProducts();

    return () => {
      ignore = true;
    };
  }, [selectedCategoryId]);

  const selectedCategoryName = useMemo(() => {
    return categories.find((category) => category.id === selectedCategoryId)?.name;
  }, [categories, selectedCategoryId]);

  return (
    <section className="catalog-page">
      <div className="page-heading">
        <div>
          <h1>Catalogo</h1>
          <p>{selectedCategoryName ? `Produtos em ${selectedCategoryName}` : "Artigos desportivos disponiveis"}</p>
        </div>
        <label className="filter-control">
          <span>Categoria</span>
          <select
            value={selectedCategoryId ?? ""}
            onChange={(event) => {
              const value = event.target.value;
              setSelectedCategoryId(value ? Number(value) : undefined);
            }}
          >
            <option value="">Todas</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
        </label>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}

      {loading ? (
        <div className="status-message">A carregar produtos...</div>
      ) : products.length === 0 ? (
        <div className="status-message">Nao existem produtos para esta selecao.</div>
      ) : (
        <div className="product-grid">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} onAdd={addProduct} />
          ))}
        </div>
      )}
    </section>
  );
}
