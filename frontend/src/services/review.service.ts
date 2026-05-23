import { apiClient } from "../api/apiClient";

export type ReviewResponse = {
  id: number;
  customerId: number;
  customerName: string;
  productId: number;
  rating: number;
  comment?: string;
  createdAt: string;
};

export type ProductRatingResponse = {
  average: number;
  count: number;
  reviews: ReviewResponse[];
};

export async function getProductReviews(productId: number): Promise<ProductRatingResponse> {
  const res = await apiClient.get<ProductRatingResponse>(`/products/${productId}/reviews`);
  return res.data;
}

export async function upsertReview(productId: number, rating: number, comment: string): Promise<ReviewResponse> {
  const res = await apiClient.put<ReviewResponse>(`/products/${productId}/reviews`, { rating, comment });
  return res.data;
}
