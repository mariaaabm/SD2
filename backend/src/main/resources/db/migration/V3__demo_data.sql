-- SportFlow — Dados de demonstração (27 produtos, 7 categorias)
-- IDs das categorias: Calcado=1, Vestuario=2, Equipamento=3, Acessorios=4 (inseridos em V2)
-- Novas categorias inseridas abaixo: Natacao=5, Ciclismo=6, Fitness=7

INSERT INTO categories (name, description) VALUES
('Natacao',  'Fatos de banho, oculos e equipamento de natacao'),
('Ciclismo', 'Capacetes, luvas e acessorios de ciclismo'),
('Fitness',  'Pesos, bandas elasticas e equipamento de ginasio');

INSERT INTO products (name, description, price, stock, active, category_id) VALUES
-- Calcado (id=1)
('Sapatilhas Trail Running',  'Sapatilhas para terreno irregular com sola agarre', 109.99, 30, TRUE, 1),
('Sapatilhas Basket',         'Sapatilhas de basquetebol com suporte no tornozelo', 79.99,  25, TRUE, 1),
('Sapatilhas Padel',          'Calcado especifico para padel com sola espinha',    69.99,  35, TRUE, 1),

-- Vestuario (id=2)
('Calcoes Desporto',          'Calcoes ligeiros com bolsos laterais',              18.99,  90, TRUE, 2),
('Leggings Compressao',       'Leggings de compressao para corrida',               34.99,  60, TRUE, 2),
('Casaco Corta-Vento',        'Casaco leve e impermeavel para desporto outdoor',   54.99,  40, TRUE, 2),
('Meias Desportivas Pack 3',  'Pack de 3 pares de meias tecnicas antiderrapantes', 12.99, 120, TRUE, 2),

-- Equipamento (id=3)
('Bola de Basquetebol',       'Bola de basquetebol tamanho 7 grip premium',        29.99,  45, TRUE, 3),
('Raquete de Padel',          'Raquete de padel carbono para nivel intermedio',     79.99,  20, TRUE, 3),
('Raquete de Tenis',          'Raquete de tenis com cordas incluidas',              64.99,  22, TRUE, 3),
('Bola de Rugby',             'Bola de rugby tamanho oficial com grip texturado',   24.99,  30, TRUE, 3),

-- Acessorios (id=4)
('Mochila Desporto 30L',      'Mochila resistente com compartimento para calcado', 44.99,  50, TRUE, 4),
('Garrafa Termica 750ml',     'Garrafa de aco inoxidavel com isolamento duplo',     22.99,  70, TRUE, 4),
('Cinto de Hidratacao',       'Cinto com 2 garrafas para corrida de longa distancia', 29.99, 25, TRUE, 4),
('Pulseira GPS Desportiva',   'Monitor de atividade com GPS e cardiofrequencimetro', 149.99, 15, TRUE, 4),

-- Natacao (id=5)
('Fato de Banho Competicao',  'Fato de banho de competicao hidrodinamico',          49.99,  20, TRUE, 5),
('Oculos Natacao',            'Oculos anti-niebla com protecao UV',                  17.99,  55, TRUE, 5),
('Touca de Silicone',         'Touca de natacao em silicone premium',                 8.99,  80, TRUE, 5),

-- Ciclismo (id=6)
('Capacete Ciclismo',         'Capacete leve com ventilacao otimizada',              59.99,  18, TRUE, 6),
('Luvas Ciclismo',            'Luvas com palma acolchoada para longas distancias',   19.99,  45, TRUE, 6),

-- Fitness (id=7)
('Halteres 5kg Par',          'Par de halteres revestidos a borracha 5kg',           34.99,  30, TRUE, 7),
('Banda Elastica Resistencia', 'Set de 3 bandas elasticas de resistencia variavel',  14.99,  65, TRUE, 7),
('Tapete Yoga',               'Tapete de yoga antiderrapante 6mm espessura',          27.99,  40, TRUE, 7),

-- Produto inativo (para demonstracao de gestao)
('Produto Descontinuado',     'Produto inativo para demonstracao de gestao',           9.99,   0, FALSE, 4);
