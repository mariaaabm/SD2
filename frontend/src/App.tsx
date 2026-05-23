import { Header } from "./components/Header/Header";
import { Footer } from "./components/Footer/Footer";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import { CartProvider } from "./contexts/CartContext";
import { AdminCategoriesPage } from "./pages/AdminCategoriesPage/AdminCategoriesPage";
import { AdminCustomersPage } from "./pages/AdminCustomersPage/AdminCustomersPage";
import { AdminProductsPage } from "./pages/AdminProductsPage/AdminProductsPage";
import { AdminSalesPage } from "./pages/AdminSalesPage/AdminSalesPage";
import { AdminStatsPage } from "./pages/AdminStatsPage/AdminStatsPage";
import { CartPage } from "./pages/CartPage/CartPage";
import { CatalogPage } from "./pages/CatalogPage/CatalogPage";
import { CheckoutPage } from "./pages/CheckoutPage/CheckoutPage";
import { HomePage } from "./pages/HomePage/HomePage";
import { InvoicePage } from "./pages/InvoicePage/InvoicePage";
import { LoginPage } from "./pages/LoginPage/LoginPage";
import { OrdersPage } from "./pages/OrdersPage/OrdersPage";
import { ProductPage } from "./pages/ProductPage/ProductPage";
import { RegisterPage } from "./pages/RegisterPage/RegisterPage";
import { ProfilePage } from "./pages/ProfilePage/ProfilePage";
import { type ReactNode, useEffect, useState } from "react";

function useLocation() {
  const [location, setLocation] = useState({ pathname: window.location.pathname, search: window.location.search });

  useEffect(() => {
    function handle() {
      setLocation({ pathname: window.location.pathname, search: window.location.search });
    }
    window.addEventListener("popstate", handle);
    return () => window.removeEventListener("popstate", handle);
  }, []);

  return location;
}

function Page() {
  const { pathname, search } = useLocation();
  const params = new URLSearchParams(search);

  if (pathname === "/login") return <LoginPage />;
  if (pathname === "/register") return <RegisterPage />;
  if (pathname === "/cart") return <CartPage />;

  if (pathname === "/checkout") {
    return <RequireAuth><CheckoutPage /></RequireAuth>;
  }

  if (pathname === "/orders") {
    return <RequireAuth><OrdersPage /></RequireAuth>;
  }

  if (pathname === "/profile") {
    return <RequireAuth><ProfilePage /></RequireAuth>;
  }

  if (pathname.startsWith("/orders/") && pathname.endsWith("/invoice")) {
    const saleId = Number(pathname.replace("/orders/", "").replace("/invoice", ""));
    return Number.isFinite(saleId)
      ? <RequireAuth><InvoicePage saleId={saleId} /></RequireAuth>
      : <RequireAuth><OrdersPage /></RequireAuth>;
  }

  if (pathname.startsWith("/products/")) {
    const productId = Number(pathname.replace("/products/", ""));
    return Number.isFinite(productId) ? <ProductPage productId={productId} /> : <CatalogPage />;
  }

  if (pathname === "/catalog") {
    const categoryId = params.get("categoryId") ? Number(params.get("categoryId")) : undefined;
    const search = params.get("search") ?? undefined;
    return <CatalogPage initialCategoryId={categoryId} initialSearch={search} />;
  }

  if (pathname === "/admin/products") return <RequireAdmin><AdminProductsPage /></RequireAdmin>;
  if (pathname === "/admin/categories") return <RequireAdmin><AdminCategoriesPage /></RequireAdmin>;
  if (pathname === "/admin/sales") return <RequireAdmin><AdminSalesPage /></RequireAdmin>;
  if (pathname === "/admin/customers") return <RequireAdmin><AdminCustomersPage /></RequireAdmin>;
  if (pathname === "/admin/stats") return <RequireAdmin><AdminStatsPage /></RequireAdmin>;

  if (pathname === "/") return <HomePage />;

  return <NotFoundPage />;
}

function NotFoundPage() {
  return (
    <section className="not-found-page">
      <div className="not-found-page__code">404</div>
      <h1>Pagina nao encontrada</h1>
      <p>A pagina que procuras nao existe ou foi movida.</p>
      <div className="action-row">
        <a className="primary-link" href="/">Pagina inicial</a>
        <a className="secondary-link" href="/catalog">Ver catalogo</a>
      </div>
    </section>
  );
}

function RequireAuth({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <div className="status-message">A validar sessao...</div>;
  if (!isAuthenticated) {
    return (
      <section className="access-card">
        <h1>Autenticacao necessaria</h1>
        <p>Entra na tua conta para aceder a esta area.</p>
        <a className="primary-link" href="/login">Entrar</a>
      </section>
    );
  }
  return children;
}

function RequireAdmin({ children }: { children: ReactNode }) {
  const { customer, isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <div className="status-message">A validar sessao...</div>;
  if (!isAuthenticated) {
    return (
      <section className="access-card">
        <h1>Autenticacao necessaria</h1>
        <p>Entra com uma conta de administrador para aceder a esta area.</p>
        <a className="primary-link" href="/login">Entrar</a>
      </section>
    );
  }
  if (customer?.role !== "ADMIN") {
    return (
      <section className="access-card">
        <h1>Acesso reservado</h1>
        <p>Esta area esta limitada a utilizadores administradores.</p>
        <a className="secondary-link" href="/catalog">Voltar ao catalogo</a>
      </section>
    );
  }
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Header />
        <main className="app-main">
          <Page />
        </main>
        <Footer />
      </CartProvider>
    </AuthProvider>
  );
}
