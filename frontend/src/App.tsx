import { Header } from "./components/Header/Header";
import { AuthProvider, useAuth } from "./contexts/AuthContext";
import { CartProvider } from "./contexts/CartContext";
import { AdminCategoriesPage } from "./pages/AdminCategoriesPage/AdminCategoriesPage";
import { AdminProductsPage } from "./pages/AdminProductsPage/AdminProductsPage";
import { AdminSalesPage } from "./pages/AdminSalesPage/AdminSalesPage";
import { AdminStatsPage } from "./pages/AdminStatsPage/AdminStatsPage";
import { CartPage } from "./pages/CartPage/CartPage";
import { CatalogPage } from "./pages/CatalogPage/CatalogPage";
import { CheckoutPage } from "./pages/CheckoutPage/CheckoutPage";
import { InvoicePage } from "./pages/InvoicePage/InvoicePage";
import { LoginPage } from "./pages/LoginPage/LoginPage";
import { OrdersPage } from "./pages/OrdersPage/OrdersPage";
import { ProductPage } from "./pages/ProductPage/ProductPage";
import { RegisterPage } from "./pages/RegisterPage/RegisterPage";
import { type ReactNode, useEffect, useState } from "react";

function usePathname() {
  const [pathname, setPathname] = useState(window.location.pathname);

  useEffect(() => {
    function handlePopState() {
      setPathname(window.location.pathname);
    }

    window.addEventListener("popstate", handlePopState);
    return () => window.removeEventListener("popstate", handlePopState);
  }, []);

  return pathname;
}

function Page() {
  const pathname = usePathname();

  if (pathname === "/login") {
    return <LoginPage />;
  }

  if (pathname === "/register") {
    return <RegisterPage />;
  }

  if (pathname === "/cart") {
    return <CartPage />;
  }

  if (pathname === "/checkout") {
    return (
      <RequireAuth>
        <CheckoutPage />
      </RequireAuth>
    );
  }

  if (pathname === "/orders") {
    return (
      <RequireAuth>
        <OrdersPage />
      </RequireAuth>
    );
  }

  if (pathname.startsWith("/orders/") && pathname.endsWith("/invoice")) {
    const saleId = Number(pathname.replace("/orders/", "").replace("/invoice", ""));
    return Number.isFinite(saleId) ? (
      <RequireAuth>
        <InvoicePage saleId={saleId} />
      </RequireAuth>
    ) : (
      <RequireAuth>
        <OrdersPage />
      </RequireAuth>
    );
  }

  if (pathname.startsWith("/products/")) {
    const productId = Number(pathname.replace("/products/", ""));
    return Number.isFinite(productId) ? <ProductPage productId={productId} /> : <CatalogPage />;
  }

  if (pathname === "/admin/products") {
    return (
      <RequireAdmin>
        <AdminProductsPage />
      </RequireAdmin>
    );
  }

  if (pathname === "/admin/categories") {
    return (
      <RequireAdmin>
        <AdminCategoriesPage />
      </RequireAdmin>
    );
  }

  if (pathname === "/admin/sales") {
    return (
      <RequireAdmin>
        <AdminSalesPage />
      </RequireAdmin>
    );
  }

  if (pathname === "/admin/stats") {
    return (
      <RequireAdmin>
        <AdminStatsPage />
      </RequireAdmin>
    );
  }

  return <CatalogPage />;
}

type GuardProps = {
  children: ReactNode;
};

function RequireAuth({ children }: GuardProps) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="status-message">A validar sessao...</div>;
  }

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

function RequireAdmin({ children }: GuardProps) {
  const { customer, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div className="status-message">A validar sessao...</div>;
  }

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
      </CartProvider>
    </AuthProvider>
  );
}
