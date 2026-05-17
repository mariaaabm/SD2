import { useState, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { register } from "../../services/auth.service";

export function RegisterPage() {
  const { applyAuth } = useAuth();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const response = await register({ name, email, password });
      applyAuth(response);
      window.history.pushState({}, "", "/catalog");
      window.dispatchEvent(new PopStateEvent("popstate"));
    } catch {
      setError("Nao foi possivel criar a conta.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <h1>Criar conta</h1>
        {error && <div className="status-message status-message--error">{error}</div>}
        <label>
          <span>Nome</span>
          <input value={name} onChange={(event) => setName(event.target.value)} required />
        </label>
        <label>
          <span>Email</span>
          <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required />
        </label>
        <label>
          <span>Password</span>
          <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" minLength={8} required />
        </label>
        <button type="submit" disabled={loading}>{loading ? "A criar..." : "Criar conta"}</button>
        <a href="/login">Ja tenho conta</a>
      </form>
    </section>
  );
}
