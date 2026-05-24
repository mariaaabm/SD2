import { apiClient } from "../api/apiClient";

export type CheckoutItem = {
  productId: number;
  quantity: number;
};

export type CheckoutPayload = {
  items: CheckoutItem[];
  shippingName: string;
  shippingPhone: string;
  shippingAddress: string;
  shippingAddress2?: string;
  shippingPostalCode: string;
  shippingCity: string;
  shippingRegion?: string;
  shippingCountry: string;
  paymentMethod: string;
};

export type SaleItem = {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  unitPriceNet: number;
  vatRate: number;
  vatAmount: number;
  subtotalNet: number;
  subtotal: number;
};

export type InvoiceIssuer = {
  companyName: string;
  taxId: string;
  address: string;
  postalCode: string;
  city: string;
  country: string;
  email: string;
  phone: string;
  website: string;
  shareCapital: string;
  registryInfo: string;
};

export type InvoiceParty = {
  name: string | null;
  taxId: string | null;
  email: string | null;
  phone: string | null;
  address: string | null;
  address2: string | null;
  postalCode: string | null;
  city: string | null;
  region: string | null;
  country: string | null;
};

export type InvoiceLine = {
  productId: number;
  description: string;
  quantity: number;
  unitPriceNet: number;
  unitPriceGross: number;
  vatRate: number;
  lineNet: number;
  vatAmount: number;
  lineGross: number;
};

export type InvoiceVatSummary = {
  rate: number;
  base: number;
  amount: number;
};

export type Invoice = {
  id: number;
  saleId: number;
  documentType: string;
  series: string;
  invoiceNumber: string;
  formattedNumber: string;
  issuedAt: string;
  operationDate: string | null;
  issuer: InvoiceIssuer;
  customer: InvoiceParty;
  shippingTo: InvoiceParty;
  lines: InvoiceLine[];
  vatSummary: InvoiceVatSummary[];
  subtotal: number;
  vatTotal: number;
  total: number;
  currency: string;
  paymentMethod: string | null;
  paymentTerms: string | null;
  notes: string | null;
  atcud: string | null;
  hashControl: string | null;
  certificationText: string | null;
};

export type SaleStatus = "CONFIRMED" | "PROCESSING" | "SHIPPED" | "DELIVERED" | "CANCELLED";

export type Sale = {
  id: number;
  customerId: number;
  customerName: string;
  createdAt: string;
  subtotal: number;
  vatAmount: number;
  vatRate: number;
  total: number;
  status: SaleStatus;
  items: SaleItem[];
  invoice: Invoice | null;
  shippingName: string | null;
  shippingPhone: string | null;
  shippingAddress: string | null;
  shippingAddress2: string | null;
  shippingPostalCode: string | null;
  shippingCity: string | null;
  shippingRegion: string | null;
  shippingCountry: string | null;
  paymentMethod: string | null;
};

export async function checkout(payload: CheckoutPayload) {
  const response = await apiClient.post<Sale>("/sales/checkout", payload);
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

export async function updateSaleStatus(saleId: number, status: SaleStatus) {
  const response = await apiClient.patch<Sale>(`/admin/sales/${saleId}/status`, { status });
  return response.data;
}
