import { apiClient } from "../api/apiClient";

export type StatsProduct = {
  productId: number;
  productName: string;
  quantitySold: number;
};

export type StatsCustomer = {
  customerId: number;
  customerName: string;
  totalPurchases: number;
  totalSpent: number;
};

export type StatsRevenue = {
  periodStart: string;
  periodEnd: string;
  revenue: number;
};

export async function getTopSellingProducts() {
  const response = await apiClient.get<StatsProduct[]>("/stats/products/top-selling");
  return response.data;
}

export async function getLeastSellingProducts() {
  const response = await apiClient.get<StatsProduct[]>("/stats/products/least-selling");
  return response.data;
}

export async function getBestCustomers() {
  const response = await apiClient.get<StatsCustomer[]>("/stats/customers/best");
  return response.data;
}

export async function getRevenue(period: "day" | "week" | "month") {
  const response = await apiClient.get<StatsRevenue>("/stats/revenue", { params: { period } });
  return response.data;
}
