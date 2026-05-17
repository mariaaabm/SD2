import { useAuth } from "../../contexts/AuthContext";

export function Header() {
  const { customer, isAuthenticated, logout } = useAuth();

  function handleLogout() {
    logout();
    window.history.pushState({}, "", "/catalog");
    window.dispatchEvent(new PopStateEvent("popstate"));
  }

  return (
    <header className="site-header">
      <a href="/">Shopping Food Store</a>
      <nav>
        <a href="/catalog">Catalogo</a>
        <a href="/cart">Carrinho</a>
        {isAuthenticated && <a href="/orders">Compras</a>}
        {customer?.role === "ADMIN" && (
          <>
            <a href="/admin/products">Produtos</a>
            <a href="/admin/categories">Categorias</a>
            <a href="/admin/sales">Vendas</a>
            <a href="/admin/stats">Stats</a>
          </>
        )}
        {isAuthenticated ? (
          <button className="link-button" type="button" onClick={handleLogout}>
            Sair
          </button>
        ) : (
          <a href="/login">Entrar</a>
        )}
      </nav>
    </header>
  );
}
