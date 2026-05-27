import axios from "axios";

// Cria a instância única do axios usada por todos os serviços do frontend, configura a base URL a partir da variável de ambiente VITE_API_BASE_URL ou usa /api por defeito, e ativa withCredentials para enviar os cookies HttpOnly de autenticação.
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api",
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
});

// Bandeira global que impede múltiplos pedidos de refresh concorrentes.
// Sem este mecanismo, se 3 pedidos falharem com 401 ao mesmo tempo,
// seria disparado um refresh por cada um, podendo consumir o token de rotação.
let isRefreshing = false;
let failedQueue: Array<{ resolve: (v: unknown) => void; reject: (e: unknown) => void }> = [];

// Liberta todos os pedidos que estavam à espera do refresh:
// se o refresh teve sucesso, resolve-os (e serão repetidos pelo caller);
// se falhou, rejeita-os para que o erro chegue ao componente.
function processQueue(error: unknown) {
  failedQueue.forEach((p) => (error ? p.reject(error) : p.resolve(undefined)));
  failedQueue = [];
}

// Interceptor que captura respostas 401 e tenta renovar a sessão chamando /auth/refresh transparentemente, e em caso de sucesso repete o pedido original; se vários pedidos falharem ao mesmo tempo, enfileira-os para só fazer um refresh e libertar todos quando este termina.
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;

    if (
      error.response?.status !== 401 ||
      original._retry ||
      original.url?.includes("/auth/refresh") ||
      original.url?.includes("/auth/login")
    ) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject });
      }).then(() => apiClient(original)).catch((e) => Promise.reject(e));
    }

    original._retry = true;
    isRefreshing = true;

    try {
      await apiClient.post("/auth/refresh");
      processQueue(null);
      return apiClient(original);
    } catch (refreshError) {
      processQueue(refreshError);
      // Redirect to login — session fully expired
      window.history.pushState({}, "", "/login");
      window.dispatchEvent(new PopStateEvent("popstate"));
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);
