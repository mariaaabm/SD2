import { useState, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { useCart } from "../../contexts/CartContext";

function navigate(path: string) {
  window.history.pushState({}, "", path);
  window.dispatchEvent(new PopStateEvent("popstate"));
}

export function Header() {
  const { customer, isAuthenticated, logout } = useAuth();
  const { items } = useCart();
  const [query, setQuery] = useState("");

  const cartCount = items.reduce((sum, i) => sum + i.quantity, 0);

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
              <a href="/orders">Ola, {customer?.name?.split(" ")[0]}</a>
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
      </div>

      <nav className="site-header__nav">
        <ul>
          <li><a href="/catalog">Todos</a></li>
          <li><a href="/catalog?categoryId=1">Calcado</a></li>
          <li><a href="/catalog?categoryId=2">Vestuario</a></li>
          <li><a href="/catalog?categoryId=3">Equipamento</a></li>
          <li><a href="/catalog?categoryId=4">Acessorios</a></li>
          <li><a href="/catalog?categoryId=5">Natacao</a></li>
          <li><a href="/catalog?categoryId=6">Ciclismo</a></li>
          <li><a href="/catalog?categoryId=7">Fitness</a></li>
          {customer?.role === "ADMIN" && (
            <>
              <li><a href="/admin/products">Gerir Produtos</a></li>
              <li><a href="/admin/categories">Categorias</a></li>
              <li><a href="/admin/sales">Vendas</a></li>
              <li><a href="/admin/stats">Stats</a></li>
            </>
          )}
        </ul>
      </nav>
    </header>
  );
}
