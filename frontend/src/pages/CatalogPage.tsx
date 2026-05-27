import { useEffect, useMemo, useState, type FormEvent } from "react";
import { ProductCard } from "../components/ProductCard";
import { useCart } from "../contexts/CartContext";
import { listCategories, type Category } from "../services/category.service";
import { listProducts, type Product } from "../services/product.service";

const PAGE_SIZE = 20;

type CatalogPageProps = {
  initialCategoryId?: number;
  initialSearch?: string;
};

function navigate(path: string) {
  window.history.pushState({}, "", path);
  window.dispatchEvent(new PopStateEvent("popstate"));
}

// Página do catálogo com filtros por categoria, pesquisa e paginação, sincroniza o estado com a URL para que pesquisas e filtros sejam partilháveis e funcionem com o botão voltar do browser, e mostra um estado vazio amigável quando a pesquisa não devolve nada.
export function CatalogPage({ initialCategoryId, initialSearch }: CatalogPageProps) {
  const { addProduct } = useCart();
  const [categories, setCategories] = useState<Category[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | undefined>(initialCategoryId);
  const [searchQuery, setSearchQuery] = useState(initialSearch ?? "");
  const [activeSearch, setActiveSearch] = useState(initialSearch ?? "");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {});
  }, []);

  // Sincroniza com a URL (ex.: nova pesquisa feita a partir do header)
  useEffect(() => {
    setSearchQuery(initialSearch ?? "");
    setActiveSearch(initialSearch ?? "");
    setSelectedCategoryId(initialCategoryId);
    setPage(0);
  }, [initialSearch, initialCategoryId]);

  useEffect(() => {
    // A variável "ignore" implementa o padrão de cleanup do useEffect para React:
    // se o utilizador muda de filtro antes de a resposta chegar, o resultado antigo é descartado
    // e não sobrescreve o estado da pesquisa mais recente.
    let ignore = false;
    setLoading(true);
    setError(null);

    listProducts({
      categoryId: selectedCategoryId,
      activeOnly: true,
      search: activeSearch || undefined,
      page,
      size: PAGE_SIZE,
    })
      .then((data) => {
        if (!ignore) {
          setProducts(data.content);
          setTotalPages(data.totalPages);
          setTotalElements(data.totalElements);
        }
      })
      .catch(() => { if (!ignore) setError("Não foi possível carregar os produtos."); })
      .finally(() => { if (!ignore) setLoading(false); });

    return () => { ignore = true; };
  }, [selectedCategoryId, activeSearch, page]);

  function handleSearch(e: FormEvent) {
    e.preventDefault();
    setPage(0);
    setActiveSearch(searchQuery);
    const params = new URLSearchParams();
    if (selectedCategoryId) params.set("categoryId", String(selectedCategoryId));
    if (searchQuery.trim()) params.set("search", searchQuery.trim());
    navigate(`/catalog${params.toString() ? `?${params}` : ""}`);
  }

  function clearSearch() {
    setPage(0);
    setSearchQuery("");
    setActiveSearch("");
    const params = new URLSearchParams();
    if (selectedCategoryId) params.set("categoryId", String(selectedCategoryId));
    navigate(`/catalog${params.toString() ? `?${params}` : ""}`);
  }

  function selectCategory(id: number | undefined) {
    setPage(0);
    setSelectedCategoryId(id);
    const params = new URLSearchParams();
    if (id) params.set("categoryId", String(id));
    if (activeSearch) params.set("search", activeSearch);
    navigate(`/catalog${params.toString() ? `?${params}` : ""}`);
  }

  function clearFilters() {
    setPage(0);
    setActiveSearch("");
    setSearchQuery("");
    selectCategory(undefined);
  }

  const selectedCategoryName = useMemo(
    () => categories.find((c) => c.id === selectedCategoryId)?.name,
    [categories, selectedCategoryId]
  );

  const heading = activeSearch
    ? `Resultados para "${activeSearch}"`
    : selectedCategoryName ?? "Todos os produtos";

  const hasFilters = !!activeSearch || selectedCategoryId !== undefined;

  return (
    <section className="catalog-page">
      <div className="catalog-toolbar">
        <div>
          <h1>{heading}</h1>
          <p>
            {loading ? "A carregar…" : `${totalElements} produto(s)`}
          </p>
        </div>
        <form className="search-inline" onSubmit={handleSearch}>
          <input
            type="text"
            placeholder="Pesquisar…"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          {searchQuery && (
            <button
              type="button"
              className="search-clear-btn"
              onClick={clearSearch}
              title="Limpar pesquisa"
              aria-label="Limpar pesquisa"
            >
              ✕
            </button>
          )}
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
        <div className="status-message">A carregar produtos…</div>
      ) : products.length === 0 ? (
        <div className="empty-results">
          <div className="empty-results__icon">🔍</div>
          <h2 className="empty-results__title">
            {activeSearch
              ? `Nenhum resultado para "${activeSearch}"`
              : "Nenhum produto nesta categoria"}
          </h2>
          <p className="empty-results__desc">
            {activeSearch
              ? "Experimenta outros termos de pesquisa ou navega pelas categorias."
              : "Esta categoria não tem produtos disponíveis de momento."}
          </p>
          {hasFilters && (
            <button className="empty-results__clear" onClick={clearFilters}>
              ✕ Limpar filtros
            </button>
          )}
        </div>
      ) : (
        <>
          <div className="product-grid">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} onAdd={addProduct} />
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="pagination__btn"
                disabled={page === 0}
                onClick={() => setPage(0)}
                title="Primeira página"
              >
                «
              </button>
              <button
                className="pagination__btn"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                ‹ Anterior
              </button>

              <span className="pagination__info">
                Página {page + 1} de {totalPages}
              </span>

              <button
                className="pagination__btn"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Próxima ›
              </button>
              <button
                className="pagination__btn"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(totalPages - 1)}
                title="Última página"
              >
                »
              </button>
            </div>
          )}
        </>
      )}
    </section>
  );
}
