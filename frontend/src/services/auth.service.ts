import { apiClient } from "../api/apiClient";

export type UserRole = "ADMIN" | "CLIENT";

export type Customer = {
  id: number;
  name: string;
  email: string;
  role: UserRole;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type RegisterRequest = {
  name: string;
  email: string;
  password: string;
};

export type AuthResponse = {
  token: string;
  customer: Customer;
};

export async function login(data: LoginRequest) {
  const response = await apiClient.post<AuthResponse>("/auth/login", data);
  return response.data;
}

export async function register(data: RegisterRequest) {
  const response = await apiClient.post<AuthResponse>("/auth/register", data);
  return response.data;
}

export async function getCurrentUser() {
  const response = await apiClient.get<Customer>("/auth/me");
  return response.data;
}

export function saveAuth(response: AuthResponse) {
  localStorage.setItem("authToken", response.token);
  localStorage.setItem("authCustomer", JSON.stringify(response.customer));
}

export function getStoredCustomer() {
  const raw = localStorage.getItem("authCustomer");
  return raw ? (JSON.parse(raw) as Customer) : null;
}

export function logout() {
  localStorage.removeItem("authToken");
  localStorage.removeItem("authCustomer");
}
