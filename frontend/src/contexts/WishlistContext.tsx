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

export function WishlistProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const [wishlistIds, setWishlistIds] = useState<number[]>([]);

  useEffect(() => {
    if (!isAuthenticated) { setWishlistIds([]); return; }
    getWishlistIds().then(setWishlistIds).catch(() => {});
  }, [isAuthenticated]);

  async function toggle(productId: number) {
    if (!isAuthenticated) {
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
