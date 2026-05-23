INSERT INTO categories (name, description) VALUES
('Calçado', 'Sapatilhas, chuteiras e calçado desportivo'),
('Vestuário', 'Camisolas, calções e vestuário desportivo'),
('Equipamento', 'Bolas, raquetes e equipamento desportivo'),
('Acessórios', 'Mochilas, garrafas e acessórios de desporto');

INSERT INTO customers (name, email, password_hash, role) VALUES
('Administrador', 'admin@store.test', '$2a$12$N1FjTySI0M.bYFQb8Ffw8eyIKr2AoadN2B4r0t/U/FSx7qcM/j8rq', 'ADMIN');

INSERT INTO products (name, description, price, stock, active, category_id) VALUES
('Sapatilhas Running Pro', 'Sapatilhas de corrida com amortecimento superior', 89.99, 50, TRUE, 1),
('Chuteiras Futebol Elite', 'Chuteiras de futebol com sola de borracha', 59.99, 40, TRUE, 1),
('Camisola de Treino Respirável', 'Camisola técnica de secagem rápida', 24.99, 80, TRUE, 2),
('Bola de Futebol Oficial', 'Bola de futebol tamanho 5 certificada', 19.99, 60, TRUE, 3);
