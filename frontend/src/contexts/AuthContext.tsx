import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { clearLocalAuth, getCurrentUser, getStoredCustomer, logoutApi, saveAuth, type AuthResponse, type Customer } from "../services/auth.service";

type AuthContextValue = {
  customer: Customer | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  applyAuth: (response: AuthResponse) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [customer, setCustomer] = useState<Customer | null>(() => getStoredCustomer());
  const [isLoading, setIsLoading] = useState(() => getStoredCustomer() !== null);

  useEffect(() => {
    if (!customer) {
      setIsLoading(false);
      return;
    }

    let active = true;

    getCurrentUser()
      .then((current) => {
        if (active) setCustomer(current);
      })
      .catch(() => {
        if (active) {
          clearLocalAuth();
          setCustomer(null);
        }
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => { active = false; };
  }, []);

  const value = useMemo<AuthContextValue>(() => ({
    customer,
    isAuthenticated: customer !== null,
    isLoading,
    applyAuth: (response) => {
      saveAuth(response);
      setCustomer(response.customer);
      setIsLoading(false);
    },
    logout: () => {
      clearLocalAuth();
      setCustomer(null);
      setIsLoading(false);
      logoutApi();
    },
  }), [customer, isLoading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth deve ser usado dentro de AuthProvider.");
  return context;
}
