import { apiClient } from "../api/apiClient";

export type CheckoutItem = {
  productId: number;
  quantity: number;
};

export type SaleItem = {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
};

export type Invoice = {
  id: number;
  saleId: number;
  invoiceNumber: string;
  issuedAt: string;
};

export type Sale = {
  id: number;
  customerId: number;
  customerName: string;
  createdAt: string;
  total: number;
  status: string;
  items: SaleItem[];
  invoice: Invoice | null;
};

export async function checkout(items: CheckoutItem[]) {
  const response = await apiClient.post<Sale>("/sales/checkout", { items });
  return response.data;
}

export async function listSales() {
  const response = await apiClient.get<Sale[]>("/sales");
  return response.data;
}

export async function getSale(saleId: number) {
  const response = await apiClient.get<Sale>(`/sales/${saleId}`);
  return response.data;
}

export async function listAdminSales() {
  const response = await apiClient.get<Sale[]>("/admin/sales");
  return response.data;
}

export async function getSaleInvoice(saleId: number) {
  const response = await apiClient.get<Invoice>(`/sales/${saleId}/invoice`);
  return response.data;
}
