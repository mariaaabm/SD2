import { useState, type FormEvent } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { updateProfile } from "../../services/auth.service";
import type { AxiosError } from "axios";

type ApiError = { messages: string[] };

export function ProfilePage() {
  const { customer, applyAuth } = useAuth();

  const [name, setName] = useState(customer?.name ?? "");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [nameSuccess, setNameSuccess] = useState<string | null>(null);
  const [nameError, setNameError] = useState<string | null>(null);
  const [pwSuccess, setPwSuccess] = useState<string | null>(null);
  const [pwError, setPwError] = useState<string | null>(null);
  const [savingName, setSavingName] = useState(false);
  const [savingPw, setSavingPw] = useState(false);

  function extractError(err: unknown): string {
    const msgs = (err as AxiosError<ApiError>)?.response?.data?.messages;
    return msgs?.[0] ?? "Ocorreu um erro. Tenta novamente.";
  }

  async function handleNameSubmit(e: FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    setNameError(null);
    setNameSuccess(null);
    setSavingName(true);
    try {
      const updated = await updateProfile({ name: name.trim() });
      applyAuth({ token: localStorage.getItem("authToken")!, customer: updated });
      setNameSuccess("Nome atualizado com sucesso.");
    } catch (err) {
      setNameError(extractError(err));
    } finally {
      setSavingName(false);
    }
  }

  async function handlePasswordSubmit(e: FormEvent) {
    e.preventDefault();
    setPwError(null);
    setPwSuccess(null);
    if (newPassword !== confirmPassword) {
      setPwError("As passwords nao coincidem.");
      return;
    }
    setSavingPw(true);
    try {
      await updateProfile({ currentPassword, newPassword });
      setPwSuccess("Password alterada com sucesso.");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      setPwError(extractError(err));
    } finally {
      setSavingPw(false);
    }
  }

  return (
    <section className="profile-page">
      <div className="page-heading">
        <div>
          <h1>O meu perfil</h1>
          <p>{customer?.email} · {customer?.role === "ADMIN" ? "Administrador" : "Cliente"}</p>
        </div>
      </div>

      <div className="profile-grid">
        {/* ── Nome ── */}
        <div className="profile-card">
          <h2>Informacao pessoal</h2>
          {nameError && <div className="status-message status-message--error">{nameError}</div>}
          {nameSuccess && <div className="status-message status-message--success">{nameSuccess}</div>}
          <form onSubmit={handleNameSubmit}>
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
              <input value={customer?.email ?? ""} disabled />
            </label>
            <button type="submit" disabled={savingName || name.trim() === customer?.name}>
              {savingName ? "A guardar..." : "Guardar alteracoes"}
            </button>
          </form>
        </div>

        {/* ── Password ── */}
        <div className="profile-card">
          <h2>Alterar password</h2>
          {pwError && <div className="status-message status-message--error">{pwError}</div>}
          {pwSuccess && <div className="status-message status-message--success">{pwSuccess}</div>}
          <form onSubmit={handlePasswordSubmit}>
            <label>
              <span>Password atual</span>
              <input
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                placeholder="A tua password atual"
                required
              />
            </label>
            <label>
              <span>Nova password</span>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Minimo 8 caracteres"
                minLength={8}
                required
              />
            </label>
            <label>
              <span>Confirmar nova password</span>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Repete a nova password"
                required
              />
            </label>
            <button type="submit" disabled={savingPw}>
              {savingPw ? "A alterar..." : "Alterar password"}
            </button>
          </form>
        </div>

        {/* ── Ligacoes rapidas ── */}
        <div className="profile-card profile-card--links">
          <h2>Acesso rapido</h2>
          <a href="/orders">Ver historico de compras</a>
          <a href="/catalog">Explorar produtos</a>
          {customer?.role === "ADMIN" && <a href="/admin/products">Painel de administracao</a>}
        </div>
      </div>
    </section>
  );
}
