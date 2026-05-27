import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { useAuth } from "./AuthContext";
import { addToWishlist, getWishlistIds, removeFromWishlist } from "../services/wishlist.service";

type WishlistContextType = {
  wishlistIds: number[];
  toggle: (productId: number) => Promise<void>;
  isWishlisted: (productId: number) => boolean;
};

const WishlistContext = createContext<WishlistContextType>({
  wishlistIds: [],
  toggle: async () => {},
  isWishlisted: () => false,
});

// Mantém em memória a lista de ids dos produtos favoritados pelo utilizador autenticado para evitar consultar o backend de cada vez que uma card precisa de pintar o coração, sincroniza com o backend a cada login e redireciona para a página de entrada quando um utilizador não autenticado tenta favoritar algo.
export function WishlistProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const [wishlistIds, setWishlistIds] = useState<number[]>([]);

  useEffect(() => {
    if (!isAuthenticated) { setWishlistIds([]); return; }
    getWishlistIds().then(setWishlistIds).catch(() => {});
  }, [isAuthenticated]);

  // Toggle otimista: atualiza o estado local imediatamente para UI responsiva.
  // Numa implementação mais robusta, o estado seria revertido se a chamada ao servidor falhasse.
  async function toggle(productId: number) {
    if (!isAuthenticated) {
      // Redireciona para login em vez de mostrar um erro para o utilizador perceber o que tem de fazer.
      window.location.href = "/login";
      return;
    }
    if (wishlistIds.includes(productId)) {
      await removeFromWishlist(productId);
      setWishlistIds((ids) => ids.filter((id) => id !== productId));
    } else {
      await addToWishlist(productId);
      setWishlistIds((ids) => [...ids, productId]);
    }
  }

  return (
    <WishlistContext.Provider value={{
      wishlistIds,
      toggle,
      isWishlisted: (id) => wishlistIds.includes(id),
    }}>
      {children}
    </WishlistContext.Provider>
  );
}

export function useWishlist() {
  return useContext(WishlistContext);
}
