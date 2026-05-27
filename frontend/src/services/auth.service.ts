// Encapsula as chamadas aos endpoints de autenticação do backend, expõe os tipos partilhados com o servidor para o resto do frontend e oferece helpers para persistir e ler os dados básicos do utilizador em localStorage para manter o estado entre reloads.

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

// Guarda o token e os dados do cliente em localStorage para que o AuthContext
// consiga restaurar a sessão após um reload sem ter de voltar a fazer login.
// Nota: o JWT real está também no cookie HttpOnly — o token aqui é redundante mas
// permite ao frontend saber o role do utilizador sem precisar de fazer uma chamada ao servidor.
export function saveAuth(response: AuthResponse) {
  localStorage.setItem("authToken", response.token);
  localStorage.setItem("authCustomer", JSON.stringify(response.customer));
}

// Lê os dados do cliente de localStorage, usada na inicialização do AuthContext
// para não mostrar a página de login para utilizadores que já têm sessão ativa.
export function getStoredCustomer() {
  const raw = localStorage.getItem("authCustomer");
  return raw ? (JSON.parse(raw) as Customer) : null;
}

// Remove os dados de sessão do localStorage (não invalida o cookie — isso é feito pelo logoutApi).
export function clearLocalAuth() {
  localStorage.removeItem("authToken");
  localStorage.removeItem("authCustomer");
}

// Alias de clearLocalAuth exposto para uso direto em contextos que não precisam de chamar a API.
export function logout() {
  clearLocalAuth();
}
