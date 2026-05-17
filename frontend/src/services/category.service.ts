import { apiClient } from "../api/apiClient";

export type Category = {
  id: number;
  name: string;
  description?: string;
};

export type CategoryRequest = {
  name: string;
  description?: string;
};

export async function listCategories() {
  const response = await apiClient.get<Category[]>("/categories");
  return response.data;
}

export async function createCategory(data: CategoryRequest) {
  const response = await apiClient.post<Category>("/categories", data);
  return response.data;
}

export async function updateCategory(id: number, data: CategoryRequest) {
  const response = await apiClient.put<Category>(`/categories/${id}`, data);
  return response.data;
}

export async function deleteCategory(id: number) {
  await apiClient.delete(`/categories/${id}`);
}
