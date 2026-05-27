import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import type { Product } from "../services/product.service";

export type CartItem = {
  product: Product;
  quantity: number;
};

type CartContextValue = {
  items: CartItem[];
  totalItems: number;
  total: number;
  addProduct: (product: Product) => void;
  updateQuantity: (productId: number, quantity: number) => void;
  removeProduct: (productId: number) => void;
  clear: () => void;
};

const CartContext = createContext<CartContextValue | undefined>(undefined);

type CartProviderProps = {
  children: ReactNode;
};

// Mantém o carrinho do utilizador em estado React e persiste-o em localStorage para que sobreviva a reloads ou ao fecho do separador, expondo helpers para adicionar, atualizar quantidade, remover e limpar, e garantindo sempre que a quantidade nunca ultrapassa o stock disponível do produto.
export function CartProvider({ children }: CartProviderProps) {
  const [items, setItems] = useState<CartItem[]>(() => {
    const raw = localStorage.getItem("cartItems");
    return raw ? (JSON.parse(raw) as CartItem[]) : [];
  });

  // Atualiza o estado React e persiste em localStorage de forma sincronizada para garantir
  // que o estado em memória e o armazenamento local estão sempre coerentes.
  function persist(nextItems: CartItem[]) {
    setItems(nextItems);
    localStorage.setItem("cartItems", JSON.stringify(nextItems));
  }

  // useMemo garante que o objeto de contexto só é recriado quando "items" muda,
  // evitando re-renders desnecessários em todos os consumidores do CartContext.
  const value = useMemo<CartContextValue>(() => ({
    items,
    totalItems: items.reduce((sum, item) => sum + item.quantity, 0),
    total: items.reduce((sum, item) => sum + item.product.price * item.quantity, 0),
    addProduct: (product) => {
      const current = items.find((item) => item.product.id === product.id);

      if (current) {
        // Se o produto já está no carrinho, incrementa a quantidade mas respeita o stock máximo.
        const nextQuantity = Math.min(current.quantity + 1, product.stock);
        persist(items.map((item) => item.product.id === product.id ? { ...item, quantity: nextQuantity } : item));
        return;
      }

      persist([...items, { product, quantity: 1 }]);
    },
    updateQuantity: (productId, quantity) => {
      if (quantity <= 0) {
        persist(items.filter((item) => item.product.id !== productId));
        return;
      }

      persist(items.map((item) => {
        if (item.product.id !== productId) {
          return item;
        }

        return { ...item, quantity: Math.min(quantity, item.product.stock) };
      }));
    },
    removeProduct: (productId) => {
      persist(items.filter((item) => item.product.id !== productId));
    },
    clear: () => persist([])
  }), [items]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error("useCart deve ser usado dentro de CartProvider.");
  }

  return context;
}
