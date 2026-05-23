import { useEffect, useState, type FormEvent } from "react";
import { listCategories, type Category } from "../../services/category.service";
import { createProduct, deleteProduct, listProducts, updateProduct, type Product } from "../../services/product.service";
import { getCategoryBg, getCategoryColor, getCategoryIcon } from "../../utils/categoryUtils";

type ProductForm = {
  name: string;
  description: string;
  price: string;
  stock: string;
  categoryId: string;
  active: boolean;
  imageUrl: string;
};

const emptyForm: ProductForm = { name: "", description: "", price: "", stock: "", categoryId: "", active: true, imageUrl: "" };

export function AdminProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [editing, setEditing] = useState<Product | null>(null);
  const [form, setForm] = useState<ProductForm>(emptyForm);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  async function loadData() {
    const [productData, categoryData] = await Promise.all([listProducts({ size: 100 }), listCategories()]);
    setProducts(productData.content);
    setCategories(categoryData);
  }

  useEffect(() => { loadData().catch(() => setError("Nao foi possivel carregar produtos.")); }, []);

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setSaving(true);

    const payload = {
      name: form.name,
      description: form.description,
      price: Number(form.price),
      stock: Number(form.stock),
      categoryId: Number(form.categoryId),
      active: form.active,
      imageUrl: form.imageUrl || undefined,
    };

    try {
      if (editing) {
        await updateProduct(editing.id, payload);
        setSuccess("Produto atualizado com sucesso.");
      } else {
        await createProduct(payload);
        setSuccess("Produto criado com sucesso.");
      }
      setEditing(null);
      setForm(emptyForm);
      await loadData();
    } catch {
      setError("Nao foi possivel guardar o produto.");
    } finally {
      setSaving(false);
    }
  }

  function startEdit(product: Product) {
    setEditing(product);
    setForm({
      name: product.name,
      description: product.description ?? "",
      price: String(product.price),
      stock: String(product.stock),
      categoryId: String(product.categoryId),
      active: product.active,
      imageUrl: product.imageUrl ?? "",
    });
  }

  async function handleDelete(id: number, name: string) {
    if (!window.confirm(`Remover o produto "${name}"? Esta acao nao pode ser desfeita.`)) return;
    setError(null);
    setSuccess(null);
    try {
      await deleteProduct(id);
      await loadData();
      setSuccess("Produto removido com sucesso.");
    } catch {
      setError("Nao foi possivel remover o produto. Pode ter vendas associadas.");
    }
  }

  const f = (key: keyof ProductForm, value: string | boolean) =>
    setForm((prev) => ({ ...prev, [key]: value }));

  return (
    <section className="admin-page">
      <div className="page-heading">
        <div>
          <h1>Produtos</h1>
          <p>Gestao de produtos, stock e disponibilidade.</p>
        </div>
      </div>

      {error && <div className="status-message status-message--error">{error}</div>}
      {success && <div className="status-message status-message--success">{success}</div>}

      <form onSubmit={handleSubmit} style={{ display: "grid", gap: 12, padding: 20, background: "var(--surface)", border: "1.5px solid var(--border)", borderRadius: "var(--radius)" }}>
        <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr 1fr 160px", gap: 12 }}>
          <input value={form.name} onChange={(e) => f("name", e.target.value)} placeholder="Nome do produto" required style={{ height: 40, padding: "0 12px", border: "1.5px solid var(--border)", borderRadius: "var(--radius-sm)", background: "var(--bg)" }} />
          <input value={form.price} onChange={(e) => f("price", e.target.value)} placeholder="Preco (€)" type="number" step="0.01" min="0" required style={{ height: 40, padding: "0 12px", border: "1.5px solid var(--border)", borderRadius: "var(--radius-sm)", background: "var(--bg)" }} />
          <input value={form.stock} onChange={(e) => f("stock", e.target.value)} placeholder="Stock" type="number" min="0" required style={{ height: 40, padding: "0 12px", border: "1.5px solid var(--border)", borderRadius: "var(--radius-sm)", background: "var(--bg)" }} />
          <select value={form.categoryId} onChange={(e) => f("categoryId", e.target.value)} required style={{ height: 40, padding: "0 12px", border: "1.5px solid var(--border)", borderRadius: "var(--radius-sm)", background: "var(--bg)" }}>
            <option value="">Categoria</option>
            {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr", gap: 12 }}>
          <input value={form.description} onChange={(e) => f("description", e.target.value)} placeholder="Descricao" style={{ height: 40, padding: "0 12px", border: "1.5px solid var(--border)", borderRadius: "var(--radius-sm)", background: "var(--bg)" }} />
          <input value={form.imageUrl} onChange={(e) => f("imageUrl", e.target.value)} placeholder="URL da imagem (opcional)" style={{ height: 40, padding: "0 12px", border: "1.5px solid var(--border)", borderRadius: "var(--radius-sm)", background: "var(--bg)" }} />
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
          <label className="checkbox-control">
            <input type="checkbox" checked={form.active} onChange={(e) => f("active", e.target.checked)} />
            <span>Produto ativo</span>
          </label>
          <button type="submit" disabled={saving}>{saving ? "A guardar..." : editing ? "Atualizar produto" : "Criar produto"}</button>
          {editing && <button type="button" className="secondary-button" onClick={() => { setEditing(null); setForm(emptyForm); }}>Cancelar</button>}
        </div>
      </form>

      <div className="admin-table">
        {products.map((product) => (
          <div className="admin-row admin-row--product" key={product.id}>
            {product.imageUrl ? (
              <img className="admin-row__thumb" src={product.imageUrl} alt={product.name} />
            ) : (
              <div className="admin-row__thumb-placeholder" style={{ background: getCategoryBg(product.categoryName) }}>
                <span style={{ fontWeight: 900, color: getCategoryColor(product.categoryName) }}>
                  {getCategoryIcon(product.categoryName)}
                </span>
              </div>
            )}
            <div>
              <strong>{product.name}</strong>
              <span>{product.categoryName} · {product.price.toFixed(2)} € · stock: {product.stock}</span>
            </div>
            <span className={`badge badge--${product.active ? "active" : "inactive"}`}>
              {product.active ? "Ativo" : "Inativo"}
            </span>
            <button type="button" className="secondary-button" onClick={() => startEdit(product)}>Editar</button>
            <button type="button" className="secondary-button" onClick={() => handleDelete(product.id, product.name)}>Remover</button>
          </div>
        ))}
      </div>
    </section>
  );
}
