import { useState, type FormEvent } from "react";
import { useAuth } from "../contexts/AuthContext";
import { register } from "../services/auth.service";
import { Logo } from "../components/Logo";
import type { AxiosError } from "axios";

type ApiError = { messages: string[] };

// Página de registo de novo cliente que recolhe nome, email e password, chama o endpoint /auth/register e em caso de sucesso já entra automaticamente com a conta nova porque o backend devolve o JWT na mesma resposta.
export function RegisterPage() {
  const { applyAuth } = useAuth();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [errors, setErrors] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setErrors([]);

    if (password !== confirm) {
      setErrors(["As passwords nao coincidem."]);
      return;
    }

    setLoading(true);

    try {
      const response = await register({ name, email, password });
      applyAuth(response);
      window.history.pushState({}, "", "/catalog");
      window.dispatchEvent(new PopStateEvent("popstate"));
    } catch (err: unknown) {
      const axiosErr = err as AxiosError<ApiError>;
      const msgs = axiosErr?.response?.data?.messages;
      if (msgs && msgs.length > 0) {
        setErrors(msgs);
      } else {
        setErrors(["Nao foi possivel criar a conta. Tenta novamente."]);
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="auth-page">
      <form className="auth-form" onSubmit={handleSubmit}>
        <div className="auth-form__brand">
          <Logo size={40} />
          Sport<span style={{ color: "var(--orange)" }}>Flow</span>
        </div>
        <h1>Criar conta</h1>

        {errors.length > 0 && (
          <div className="status-message status-message--error">
            {errors.map((msg, i) => <div key={i}>{msg}</div>)}
          </div>
        )}

        <label>
          <span>Nome completo</span>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="O teu nome"
            required
          />
        </label>
        <label>
          <span>Email</span>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            placeholder="exemplo@email.com"
            required
          />
        </label>
        <label>
          <span>Password</span>
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            placeholder="Minimo 8 caracteres"
            minLength={8}
            required
          />
        </label>
        <label>
          <span>Confirmar password</span>
          <input
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
            type="password"
            placeholder="Repete a password"
            required
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "A criar conta..." : "Criar conta"}
        </button>
        <div className="auth-form__footer">
          Ja tens conta? <a href="/login">Entrar</a>
        </div>
      </form>
    </section>
  );
}
