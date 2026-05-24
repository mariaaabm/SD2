import { useEffect, useState, type FormEvent } from "react";
import {
  createCategory,
  deleteCategory,
  listCategories,
  updateCategory,
  type Category
} from "../services/category.service";

// Página de administração de categorias com lista, formulário de criação e edição inline, e botão de remover, que consome os endpoints de categoria do backend e atualiza o estado local após cada operação para manter a UI sincronizada.
export function AdminCategoriesPage() {
  const [categories, setCategories] = useState<Category[]>([]);
  const [editing, setEditing] = useState<Category | null>(null);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  async function loadCategories() {
    setCategories(await listCategories());
  }

  useEffect(() => {
    loadCategories().catch(() => setError("Nao foi possivel carregar categorias."));
  }, []);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setSuccess(null);
    setSaving(true);

    try {
      if (editing) {
        await updateCategory(editing.id, { name, description });
      } else {
        await createCategory({ name, description });
      }
      setEditing(null);
      setName("");
      setDescription("");
      await loadCategories();
      setSuccess(editing ? "Categoria atualizada com sucesso." : "Categoria criada com sucesso.");
    } catch {
      setError("Nao foi possivel guardar a categoria.");
    } finally {
      setSaving(false);
    }
  }

  function startEdit(category: Category) {
    setEditing(category);
    setName(category.name);
    setDescription(category.description ?? "");
  }

  async function handleDelete(id: number, name: string) {
    if (!window.confirm(`Remover a categoria "${name}"? Tem de remover os produtos associados primeiro.`)) return;
    setError(null);
    setSuccess(null);

    try {
      await deleteCategory(id);
      await loadCategories();
      setSuccess("Categoria removida com sucesso.");
    } catch {
      setError("Nao foi possivel remover a categoria. Existem produtos associados.");
    }
  }

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Categorias</h1>
          <p>Gestao do catalogo por categorias.</p>
        </div>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      {success && <div className="status-message status-message--success">{success}</div>}

      <form className="admin-form" onSubmit={handleSubmit}>
        <input value={name} onChange={(event) => setName(event.target.value)} placeholder="Nome" required />
        <input value={description} onChange={(event) => setDescription(event.target.value)} placeholder="Descricao" />
        <button type="submit" disabled={saving}>{saving ? "A guardar..." : editing ? "Atualizar" : "Criar"}</button>
        {editing && <button type="button" className="secondary-button" onClick={() => setEditing(null)}>Cancelar</button>}
      </form>

      <div className="admin-table">
        {categories.map((category) => (
          <div className="admin-row" key={category.id}>
            <div>
              <strong>{category.name}</strong>
              <span>{category.description}</span>
            </div>
            <button type="button" className="secondary-button" onClick={() => startEdit(category)}>Editar</button>
            <button type="button" className="secondary-button" onClick={() => handleDelete(category.id, category.name)}>Remover</button>
          </div>
        ))}
      </div>
    </section>
  );
}
