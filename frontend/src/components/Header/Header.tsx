import { useState, useEffect, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useCart } from "../../contexts/CartContext";
import { listCategories, type Category } from "../../services/category.service";

function navigate(path: string) {
  window.history.pushState({}, "", path);
  window.dispatchEvent(new PopStateEvent("popstate"));
}

export function Header() {
  const { customer, isAuthenticated, logout } = useAuth();
  const { items } = useCart();
  const [query, setQuery] = useState("");
  const [categories, setCategories] = useState<Category[]>([]);
  const [menuOpen, setMenuOpen] = useState(false);

  const cartCount = items.reduce((sum, i) => sum + i.quantity, 0);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => {});
  }, []);

  function handleLogout() {
    logout();
    navigate("/");
  }

  function handleSearch(e: FormEvent) {
    e.preventDefault();
    if (query.trim()) {
      navigate(`/catalog?search=${encodeURIComponent(query.trim())}`);
    } else {
      navigate("/catalog");
    }
    setMenuOpen(false);
  }

  return (
    <header className="site-header">
      <div className="site-header__top">
        <a className="site-header__logo" href="/">
          Sport<span>Flow</span>
        </a>

        <form className="site-header__search" onSubmit={handleSearch}>
          <input
            type="text"
            placeholder="Pesquisar produtos, marcas, categorias..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button type="submit">Pesquisar</button>
        </form>

        <div className="site-header__actions">
          {isAuthenticated ? (
            <>
              <a href="/profile" className="site-header__user">
                Ola, {customer?.name?.split(" ")[0]}
                {customer?.role === "ADMIN" && <span className="admin-badge">Admin</span>}
              </a>
              <button className="link-button" type="button" onClick={handleLogout}>Sair</button>
            </>
          ) : (
            <>
              <a href="/login">Entrar</a>
              <a href="/register">Registar</a>
            </>
          )}
          <a className="btn-cart" href="/cart">
            Carrinho {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
          </a>
        </div>

        <button
          className="site-header__burger"
          type="button"
          aria-label="Menu"
          onClick={() => setMenuOpen((v) => !v)}
        >
          ☰
        </button>
      </div>

      <nav className={`site-header__nav${menuOpen ? " site-header__nav--open" : ""}`}>
        <ul>
          <li><a href="/catalog" onClick={() => setMenuOpen(false)}>Todos</a></li>
          {categories.map((cat) => (
            <li key={cat.id}>
              <a href={`/catalog?categoryId=${cat.id}`} onClick={() => setMenuOpen(false)}>
                {cat.name}
              </a>
            </li>
          ))}
          {customer?.role === "ADMIN" && (
            <>
              <li className="nav-separator" />
              <li><a href="/admin/products" onClick={() => setMenuOpen(false)}>Produtos</a></li>
              <li><a href="/admin/categories" onClick={() => setMenuOpen(false)}>Categorias</a></li>
              <li><a href="/admin/sales" onClick={() => setMenuOpen(false)}>Vendas</a></li>
              <li><a href="/admin/customers" onClick={() => setMenuOpen(false)}>Clientes</a></li>
              <li><a href="/admin/stats" onClick={() => setMenuOpen(false)}>Stats</a></li>
            </>
          )}
        </ul>
      </nav>
    </header>
  );
}
