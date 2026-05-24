// Ponto de entrada da aplicação React que monta o componente App no elemento root do index.html, importa os estilos globais e envolve a árvore em StrictMode para apanhar problemas em desenvolvimento como side-effects duplicados ou APIs descontinuadas.

import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "./styles/global.css";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

