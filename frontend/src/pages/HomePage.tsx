import { useEffect, useState } from "react";
import { useCart } from "../../contexts/CartContext";
import { listCategories, type Category } from "../../services/category.service";
import { listProducts, type Product } from "../../services/product.service";
import { getCategoryBg, getCategoryColor, getCategoryIcon, getCategoryImage } from "../../utils/categoryUtils";
import { ProductCard } from "../../components/ProductCard/ProductCard";

function navigate(path: string) {
  window.history.pushState({}, "", path);
  window.dispatchEvent(new PopStateEvent("popstate"));
}

export function HomePage() {
  const { addProduct } = useCart();
  const [categories, setCategories] = useState<Category[]>([]);
  const [featured, setFeatured] = useState<Product[]>([]);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {});
    listProducts({ activeOnly: true, size: 8 }).then((data) => setFeatured(data.content)).catch(() => {});
  }, []);

  return (
    <div>
      {/* Hero */}
      <section className="hero">
        <div className="hero__content">
          <p className="hero__eyebrow">SportFlow — A tua loja desportiva</p>
          <h1 className="hero__title">
            Equipa-te para <span>qualquer desporto.</span>
          </h1>
          <p className="hero__subtitle">
            Do running ao fitness, do futebol ao ciclismo — encontra o equipamento certo para o teu nivel e objetivo.
          </p>
          <div className="hero__actions">
            <button className="hero__cta" onClick={() => navigate("/catalog")}>
              Ver catalogo
            </button>
            <a className="hero__cta hero__cta--secondary" href="/register">
              Criar conta gratis
            </a>
          </div>
        </div>
      </section>

      {/* Categories */}
      {categories.length > 0 && (
        <section style={{ marginBottom: 48 }}>
          <div className="section-heading">
            <h2>Categorias</h2>
            <a href="/catalog">Ver todos os produtos</a>
          </div>
          <div className="category-grid">
            {categories.map((cat) => {
              const img = getCategoryImage(cat.name);
              return (
                <button
                  key={cat.id}
                  className="category-card"
                  onClick={() => navigate(`/catalog?categoryId=${cat.id}`)}
                >
                  <div className="category-card__image-wrap">
                    {img ? (
                      <img
                        src={img}
                        alt={cat.name}
                        className="category-card__img"
                        loading="lazy"
                        onError={(e) => {
                          (e.currentTarget as HTMLImageElement).style.display = "none";
                          const fb = (e.currentTarget as HTMLImageElement).nextElementSibling as HTMLElement | null;
                          if (fb) fb.style.display = "flex";
                        }}
                      />
                    ) : null}
                    <div
                      className="category-card__img-fallback"
                      style={{
                        background: getCategoryBg(cat.name),
                        display: img ? "none" : "flex",
                      }}
                    >
                      <span style={{ fontSize: 32 }}>{getCategoryIcon(cat.name)}</span>
                    </div>
                    <div
                      className="category-card__overlay"
                      style={{ background: `${getCategoryColor(cat.name)}cc` }}
                    />
                  </div>
                  <span className="category-card__name">{cat.name}</span>
                </button>
              );
            })}
          </div>
        </section>
      )}

      {/* Featured products */}
      {featured.length > 0 && (
        <section>
          <div className="section-heading">
            <h2>Produtos em destaque</h2>
            <a href="/catalog">Ver todos</a>
          </div>
          <div className="product-grid">
            {featured.map((product) => (
              <ProductCard key={product.id} product={product} onAdd={addProduct} />
            ))}
          </div>
        </section>
      )}
    </div>
  );
}
