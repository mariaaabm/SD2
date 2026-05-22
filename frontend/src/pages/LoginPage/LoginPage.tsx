import { useState, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { login } from "../../services/auth.service";
import type { AxiosError } from "axios";

type ApiError = { messages: string[] };

export function LoginPage() {
  const { applyAuth } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await login({ email, password });
      applyAuth(response);
      window.history.pushState({}, "", "/catalog");
      window.dispatchEvent(new PopStateEvent("popstate"));
    } catch (err: unknown) {
      const axiosErr = err as AxiosError<ApiError>;
      const msgs = axiosErr?.response?.data?.messages;
      if (msgs && msgs.length > 0) {
        setError(msgs[0]);
      } else if (axiosErr?.response?.status === 401) {
        setError("Email ou password incorretos.");
      } else {
        setError("Nao foi possivel entrar. Verifica a tua ligacao.");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <div className="auth-form__brand">
          Sport<span style={{ color: "var(--orange)" }}>Flow</span>
        </div>
        <h1>Entrar</h1>

        {error && <div className="status-message status-message--error">{error}</div>}

        <label>
          <span>Email</span>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            placeholder="exemplo@email.com"
            autoComplete="email"
            required
          />
        </label>
        <label>
          <span>Password</span>
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            placeholder="A tua password"
            autoComplete="current-password"
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "A entrar..." : "Entrar"}
        </button>
        <div className="auth-form__footer">
          Nao tens conta? <a href="/register">Criar conta</a>
        </div>
      </form>
    </section>
  );
}
