// Encapsula as chamadas REST da wishlist do utilizador, expõe um endpoint leve /wishlist/ids para componentes que só precisam de saber quais ids já estão favoritados e o endpoint completo /wishlist para a página dedicada.

import { apiClient } from "../api/apiClient";

export type WishlistItem = {
  id: number;
  productId: number;
  productName: string;
  productDescription?: string;
  price: number;
  stock: number;
  active: boolean;
  categoryName: string;
  imageUrl?: string;
  addedAt: string;
};

export async function getWishlist(): Promise<WishlistItem[]> {
  const res = await apiClient.get<WishlistItem[]>("/wishlist");
  return res.data;
}

export async function getWishlistIds(): Promise<number[]> {
  const res = await apiClient.get<number[]>("/wishlist/ids");
  return res.data;
}

export async function addToWishlist(productId: number): Promise<WishlistItem> {
  const res = await apiClient.post<WishlistItem>(`/wishlist/${productId}`);
  return res.data;
}

export async function removeFromWishlist(productId: number): Promise<void> {
  await apiClient.delete(`/wishlist/${productId}`);
}
