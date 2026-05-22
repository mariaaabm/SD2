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
  "Calcado":    "C",
  "Vestuario":  "V",
  "Equipamento":"E",
  "Acessorios": "A",
  "Natacao":    "N",
  "Ciclismo":   "Ci",
  "Fitness":    "F",
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
