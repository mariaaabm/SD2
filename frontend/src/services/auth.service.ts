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

export type UpdateProfileRequest = {
  name?: string;
  currentPassword?: string;
  newPassword?: string;
};

export async function updateProfile(data: UpdateProfileRequest) {
  const response = await apiClient.patch<Customer>("/auth/profile", data);
  return response.data;
}

export async function logoutApi() {
  try {
    await apiClient.post("/auth/logout");
  } catch {
    // ignora — o cookie será limpo mesmo que o servidor não responda
  }
}

export function saveAuth(response: AuthResponse) {
  localStorage.setItem("authToken", response.token);
  localStorage.setItem("authCustomer", JSON.stringify(response.customer));
}

export function getStoredCustomer() {
  const raw = localStorage.getItem("authCustomer");
  return raw ? (JSON.parse(raw) as Customer) : null;
}

export function clearLocalAuth() {
  localStorage.removeItem("authToken");
  localStorage.removeItem("authCustomer");
}

export function logout() {
  clearLocalAuth();
}
