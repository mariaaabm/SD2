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
};

export type ListProductsParams = {
  categoryId?: number;
  activeOnly?: boolean;
};

export type ProductRequest = {
  name: string;
  description?: string;
  price: number;
  stock: number;
  categoryId: number;
  active: boolean;
};

export async function listProducts(params: ListProductsParams = {}) {
  const response = await apiClient.get<Product[]>("/products", { params });
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
