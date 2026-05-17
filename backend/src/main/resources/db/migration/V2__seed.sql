INSERT INTO categories (name, description) VALUES
('Fruta', 'Fruta fresca'),
('Legumes', 'Legumes e vegetais'),
('Mercearia', 'Produtos de mercearia');

INSERT INTO customers (name, email, password_hash, role) VALUES
('Administrador', 'admin@store.test', '$2a$12$N1FjTySI0M.bYFQb8Ffw8eyIKr2AoadN2B4r0t/U/FSx7qcM/j8rq', 'ADMIN');

INSERT INTO products (name, description, price, stock, active, category_id) VALUES
('Maca Fuji', 'Maca fresca vendida ao kg', 1.99, 100, TRUE, 1),
('Banana', 'Banana vendida ao kg', 1.49, 120, TRUE, 1),
('Cenoura', 'Cenoura vendida ao kg', 0.99, 80, TRUE, 2),
('Arroz Carolino', 'Embalagem de 1 kg', 1.29, 60, TRUE, 3);
