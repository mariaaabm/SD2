// Encapsula as chamadas REST ao catálogo de produtos e expõe os tipos partilhados Product, PageResponse, ListProductsParams e ProductRequest para que páginas e componentes consumam a API com type-safety.

import { apiClient } from "../api/apiClient";

export type Product = {
  id: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  active: boolean;
  categoryId: number;
  categoryName: string;
  imageUrl?: string;
};

export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type ListProductsParams = {
  categoryId?: number;
  activeOnly?: boolean;
  search?: string;
  page?: number;
  size?: number;
};

export type ProductRequest = {
  name: string;
  description?: string;
  price: number;
  stock: number;
  categoryId: number;
  active: boolean;
  imageUrl?: string;
};

export async function listProducts(params: ListProductsParams = {}): Promise<PageResponse<Product>> {
  const response = await apiClient.get<PageResponse<Product>>("/products", { params });
  return response.data;
}

export async function getProduct(id: number) {
  const response = await apiClient.get<Product>(`/products/${id}`);
  return response.data;
}

export async function createProduct(data: ProductRequest) {
  const response = await apiClient.post<Product>("/products", data);
  return response.data;
}

export async function updateProduct(id: number, data: ProductRequest) {
  const response = await apiClient.put<Product>(`/products/${id}`, data);
  return response.data;
}

export async function deleteProduct(id: number) {
  await apiClient.delete(`/products/${id}`);
}
