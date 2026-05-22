export const CATEGORY_COLORS: Record<string, string> = {
  "Calcado":    "#003ec7",
  "Vestuario":  "#fe6b00",
  "Equipamento":"#16a34a",
  "Acessorios": "#7c3aed",
  "Natacao":    "#0891b2",
  "Ciclismo":   "#d97706",
  "Fitness":    "#dc2626",
};

export const CATEGORY_BG: Record<string, string> = {
  "Calcado":    "#e8efff",
  "Vestuario":  "#fff3e0",
  "Equipamento":"#dcfce7",
  "Acessorios": "#f3e8ff",
  "Natacao":    "#e0f7fa",
  "Ciclismo":   "#fffbeb",
  "Fitness":    "#fee2e2",
};

export const CATEGORY_ICON: Record<string, string> = {
  "Calcado":    "👟",
  "Vestuario":  "👕",
  "Equipamento":"⚽",
  "Acessorios": "🎒",
  "Natacao":    "🏊",
  "Ciclismo":   "🚴",
  "Fitness":    "💪",
};

export const CATEGORY_IMAGE: Record<string, string> = {
  "Calcado":    "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=220&fit=crop&auto=format",
  "Vestuario":  "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=220&fit=crop&auto=format",
  "Equipamento":"https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=400&h=220&fit=crop&auto=format",
  "Acessorios": "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=220&fit=crop&auto=format",
  "Natacao":    "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=400&h=220&fit=crop&auto=format",
  "Ciclismo":   "https://images.unsplash.com/photo-1541625602330-2277a4c46182?w=400&h=220&fit=crop&auto=format",
  "Fitness":    "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400&h=220&fit=crop&auto=format",
};

export function getCategoryColor(name: string): string {
  return CATEGORY_COLORS[name] ?? "#003ec7";
}

export function getCategoryBg(name: string): string {
  return CATEGORY_BG[name] ?? "#e8efff";
}

export function getCategoryIcon(name: string): string {
  return CATEGORY_ICON[name] ?? name.charAt(0).toUpperCase();
}

export function getCategoryImage(name: string): string | undefined {
  return CATEGORY_IMAGE[name];
}
