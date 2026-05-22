import { useEffect, useMemo, useState, type FormEvent } from "react";
import { ProductCard } from "../../components/ProductCard/ProductCard";
import { useCart } from "../../contexts/CartContext";
import { listCategories, type Category } from "../../services/category.service";
import { listProducts, type Product } from "../../services/product.service";

type CatalogPageProps = {
  initialCategoryId?: number;
  initialSearch?: string;
};

function navigate(path: string) {
  window.history.pushState({}, "", path);
  window.dispatchEvent(new PopStateEvent("popstate"));
}

export function CatalogPage({ initialCategoryId, initialSearch }: CatalogPageProps) {
  const { addProduct } = useCart();
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>(initialCategoryId);
  const [searchQuery, setSearchQuery] = useState(initialSearch ?? "");
  const [activeSearch, setActiveSearch] = useState(initialSearch ?? "");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {});
  }, []);

  useEffect(() => {
    let ignore = false;
    setLoading(true);
    setError(null);

    listProducts({ categoryId: selectedCategoryId, activeOnly: true, search: activeSearch || undefined })
      .then((data) => { if (!ignore) setProducts(data); })
      .catch(() => { if (!ignore) setError("Nao foi possivel carregar os produtos."); })
      .finally(() => { if (!ignore) setLoading(false); });

    return () => { ignore = true; };
  }, [selectedCategoryId, activeSearch]);

  function handleSearch(e: FormEvent) {
    e.preventDefault();
    setActiveSearch(searchQuery);
    const params = new URLSearchParams();
    if (selectedCategoryId) params.set("categoryId", String(selectedCategoryId));
    if (searchQuery.trim()) params.set("search", searchQuery.trim());
    navigate(`/catalog${params.toString() ? `?${params}` : ""}`);
  }

  function selectCategory(id: number | undefined) {
    setSelectedCategoryId(id);
    const params = new URLSearchParams();
    if (id) params.set("categoryId", String(id));
    if (activeSearch) params.set("search", activeSearch);
    navigate(`/catalog${params.toString() ? `?${params}` : ""}`);
  }

  const selectedCategoryName = useMemo(
    () => categories.find((c) => c.id === selectedCategoryId)?.name,
    [categories, selectedCategoryId]
  );

  const heading = activeSearch
    ? `Resultados para "${activeSearch}"`
    : selectedCategoryName ?? "Todos os produtos";

  const subheading = activeSearch
    ? `${products.length} produto(s) encontrado(s)`
    : "Equipamento desportivo de qualidade";

  return (
    <section className="catalog-page">
      <div className="catalog-toolbar">
        <div>
          <h1>{heading}</h1>
          <p>{subheading}</p>
        </div>
        <form className="search-inline" onSubmit={handleSearch}>
          <input
            type="text"
            placeholder="Pesquisar..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button type="submit">Ir</button>
        </form>
      </div>

      <div className="category-pills">
        <button
          className={`category-pill${selectedCategoryId === undefined ? " category-pill--active" : ""}`}
          onClick={() => selectCategory(undefined)}
        >
          Todas
        </button>
        {categories.map((cat) => (
          <button
            key={cat.id}
            className={`category-pill${selectedCategoryId === cat.id ? " category-pill--active" : ""}`}
            onClick={() => selectCategory(cat.id)}
          >
            {cat.name}
          </button>
        ))}
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}

      {loading ? (
        <div className="status-message">A carregar produtos...</div>
      ) : products.length === 0 ? (
        <div className="status-message">
          Nenhum produto encontrado.{" "}
          <button
            className="link-button"
            onClick={() => { setActiveSearch(""); setSearchQuery(""); selectCategory(undefined); }}
          >
            Limpar filtros
          </button>
        </div>
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
