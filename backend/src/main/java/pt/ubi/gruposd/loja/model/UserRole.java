package pt.ubi.gruposd.loja.model;

// Enumera os perfis de utilizador da loja, ADMIN com acesso à gestão completa de produtos, categorias, vendas e estatísticas, e CLIENT com acesso apenas ao catálogo, ao carrinho, às próprias encomendas e à wishlist.
public enum UserRole {
    ADMIN,
    CLIENT
}

