// Configuração do Vite para desenvolvimento, ativa o plugin React, arranca o dev server na porta 5173 e faz proxy dos pedidos /api para o backend em localhost:8080 reescrevendo o domínio do cookie para que os cookies HttpOnly de autenticação funcionem no browser apesar do cross-origin.

import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        cookieDomainRewrite: "localhost",
      }
    }
  }
});
