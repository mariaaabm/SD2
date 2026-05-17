import { useEffect, useState, type FormEvent } from "react";
import { listCategories, type Category } from "../../services/category.service";
import {
  createProduct,
  deleteProduct,
  listProducts,
  updateProduct,
  type Product
} from "../../services/product.service";

type ProductForm = {
  name: string;
  description: string;
  price: string;
  stock: string;
  categoryId: string;
  active: boolean;
};

const emptyForm: ProductForm = {
  name: "",
  description: "",
  price: "",
  stock: "",
  categoryId: "",
  active: true
};

export function AdminProductsPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [editing, setEditing] = useState<Product | null>(null);
  const [form, setForm] = useState<ProductForm>(emptyForm);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  async function loadData() {
    const [productData, categoryData] = await Promise.all([
      listProducts(),
      listCategories()
    ]);
    setProducts(productData);
    setCategories(categoryData);
  }

  useEffect(() => {
    loadData().catch(() => setError("Nao foi possivel carregar produtos."));
  }, []);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setSuccess(null);
    setSaving(true);

    const payload = {
      name: form.name,
      description: form.description,
      price: Number(form.price),
      stock: Number(form.stock),
      categoryId: Number(form.categoryId),
      active: form.active
    };

    try {
      if (editing) {
        await updateProduct(editing.id, payload);
      } else {
        await createProduct(payload);
      }
      setEditing(null);
      setForm(emptyForm);
      await loadData();
      setSuccess(editing ? "Produto atualizado com sucesso." : "Produto criado com sucesso.");
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
      active: product.active
    });
  }

  async function handleDelete(id: number) {
    setError(null);
    setSuccess(null);

    try {
      await deleteProduct(id);
      await loadData();
      setSuccess("Produto removido com sucesso.");
    } catch {
      setError("Nao foi possivel remover o produto.");
    }
  }

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

      <form className="admin-form admin-form--products" onSubmit={handleSubmit}>
        <input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} placeholder="Nome" required />
        <input value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} placeholder="Preco" type="number" step="0.01" min="0" required />
        <input value={form.stock} onChange={(event) => setForm({ ...form, stock: event.target.value })} placeholder="Stock" type="number" min="0" required />
        <select value={form.categoryId} onChange={(event) => setForm({ ...form, categoryId: event.target.value })} required>
          <option value="">Categoria</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>{category.name}</option>
          ))}
        </select>
        <input value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} placeholder="Descricao" />
        <label className="checkbox-control">
          <input type="checkbox" checked={form.active} onChange={(event) => setForm({ ...form, active: event.target.checked })} />
          <span>Ativo</span>
        </label>
        <button type="submit" disabled={saving}>{saving ? "A guardar..." : editing ? "Atualizar" : "Criar"}</button>
        {editing && <button type="button" className="secondary-button" onClick={() => { setEditing(null); setForm(emptyForm); }}>Cancelar</button>}
      </form>

      <div className="admin-table">
        {products.map((product) => (
          <div className="admin-row admin-row--product" key={product.id}>
            <div>
              <strong>{product.name}</strong>
              <span>{product.categoryName} · {product.price.toFixed(2)} EUR · stock {product.stock}</span>
            </div>
            <span>{product.active ? "Ativo" : "Inativo"}</span>
            <button type="button" className="secondary-button" onClick={() => startEdit(product)}>Editar</button>
            <button type="button" className="secondary-button" onClick={() => handleDelete(product.id)}>Remover</button>
          </div>
        ))}
      </div>
    </section>
  );
}
