import { useState, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { login } from "../../services/auth.service";

export function LoginPage() {
  const { applyAuth } = useAuth();
  const [email, setEmail] = useState("admin@store.test");
  const [password, setPassword] = useState("password");
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
    } catch {
      setError("Credenciais invalidas ou servidor indisponivel.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h1>Entrar</h1>
        {error && <div className="status-message status-message--error">{error}</div>}
        <label>
          <span>Email</span>
          <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required />
        </label>
        <label>
          <span>Password</span>
          <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" required />
        </label>
        <button type="submit" disabled={loading}>{loading ? "A entrar..." : "Entrar"}</button>
        <a href="/register">Criar conta</a>
      </form>
    </section>
  );
}
