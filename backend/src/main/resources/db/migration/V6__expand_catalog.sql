-- SportFlow — Catálogo expandido
-- Novas categorias: Futebol=8, Running=9, Montanhismo=10, Artes Marciais=11, Golfe=12, Desportos de Inverno=13
-- Clientes demo: password = "password" (mesmo hash do admin)

-- ── Novas categorias ────────────────────────────────────────────────
INSERT INTO categories (name, description) VALUES
('Futebol',              'Chuteiras, bolas e equipamento especifico de futebol'),
('Running',              'Calcado, vestuario e acessorios para corredores'),
('Montanhismo',          'Botas, mochilas e material para trekking e montanha'),
('Artes Marciais',       'Equipamento de boxe, judo, karate e MMA'),
('Golfe',                'Tacos, bolas e acessorios de golfe'),
('Desportos de Inverno', 'Ski, snowboard e equipamento para neve');

-- ── Utilizadores demo (password: "password") ─────────────────────────
INSERT INTO customers (name, email, password_hash, role) VALUES
('Joao Silva',    'joao.silva@demo.pt',    '$2a$12$N1FjTySI0M.bYFQb8Ffw8eyIKr2AoadN2B4r0t/U/FSx7qcM/j8rq', 'CLIENT'),
('Maria Santos',  'maria.santos@demo.pt',  '$2a$12$N1FjTySI0M.bYFQb8Ffw8eyIKr2AoadN2B4r0t/U/FSx7qcM/j8rq', 'CLIENT'),
('Pedro Costa',   'pedro.costa@demo.pt',   '$2a$12$N1FjTySI0M.bYFQb8Ffw8eyIKr2AoadN2B4r0t/U/FSx7qcM/j8rq', 'CLIENT');

-- ── Mais produtos — Calcado (id=1) ─────────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Sapatilhas Fitness Indoor',      'Sapatilhas leves para treino em sala, palmilha gel ortopedica',                          54.99,  45, TRUE, 1, 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=500&h=500&fit=crop&auto=format'),
('Sandálias Desportivas',          'Sandálias com sola de borracha EVA para uso casual e praia',                              34.99,  60, TRUE, 1, 'https://images.unsplash.com/photo-1603487742131-4160ec999306?w=500&h=500&fit=crop&auto=format'),
('Sapatilhas Ténis Indoor',        'Calcado especifico para tenis indoor com borracha nao-raspante',                          64.99,  30, TRUE, 1, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500&h=500&fit=crop&auto=format'),
('Sapatilhas de Caminhada',        'Sapatilhas confortaveis para caminhadas longas, impermeabilizadas',                       79.99,  35, TRUE, 1, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&h=500&fit=crop&auto=format&sat=-50'),
('Chuteiras de Futsal Profissão',  'Chuteiras de futsal com sola lisa para parquet e campos artificiais',                    49.99,  55, TRUE, 1, 'https://images.unsplash.com/photo-1614632537190-23e4a4479caf?w=500&h=500&fit=crop&auto=format');

-- ── Mais produtos — Vestuário (id=2) ──────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Hoodie Desporto Premium',        'Sweatshirt com capuz em algodão organico e fecho em ziper',                              44.99,  50, TRUE, 2, 'https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=500&h=500&fit=crop&auto=format'),
('Fato de Treino Completo',        'Conjunto de calças e casaco em poliester reciclado, fato coordenado',                   69.99,  30, TRUE, 2, 'https://images.unsplash.com/photo-1611915387288-fd8d2f5f928b?w=500&h=500&fit=crop&auto=format'),
('Camisola Manga Longa Térmica',   'Camisola de compressao termica para desportos de frio',                                  28.99,  65, TRUE, 2, 'https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500&h=500&fit=crop&auto=format'),
('Gorro Desportivo Lã Merino',     'Gorro de la merino antivento para desportos de inverno e montanha',                     16.99,  80, TRUE, 2, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=500&h=500&fit=crop&auto=format'),
('Collants Running Femininos',     'Collants de cintura alta com bolso lateral para chave ou cartao',                        32.99,  55, TRUE, 2, 'https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=500&h=500&fit=crop&auto=format&sat=10'),
('Camisola Futebol Replica',       'Camisola de futebol em tecido respiravel personalizada com numero',                     29.99,  70, TRUE, 2, 'https://images.unsplash.com/photo-1516478177764-9fe5bd7e9717?w=500&h=500&fit=crop&auto=format');

-- ── Mais produtos — Equipamento (id=3) ───────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Bola de Voleibol Oficial',       'Bola de voleibol tamanho 5 para uso em exterior e interior',                             22.99,  40, TRUE, 3, 'https://images.unsplash.com/photo-1592553366374-12e4dc86e138?w=500&h=500&fit=crop&auto=format'),
('Bola de Andebol Competição',     'Bola de andebol tamanho 3 com grip texturado premium',                                  28.99,  25, TRUE, 3, 'https://images.unsplash.com/photo-1546519638-68e109498ffc?w=500&h=500&fit=crop&auto=format&sat=-30'),
('Rede de Badminton Portátil',     'Rede de badminton para jardim e praia com postes ajustaveis',                            34.99,  20, TRUE, 3, 'https://images.unsplash.com/photo-1521537634581-0dced2fee2ef?w=500&h=500&fit=crop&auto=format'),
('Raquete Squash Carbono',         'Raquete de squash em carbono para nivel intermedio/avancado',                            59.99,  15, TRUE, 3, 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=500&h=500&fit=crop&auto=format&sat=20');

-- ── Mais produtos — Acessórios (id=4) ────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Cinto de Pesos 10kg',            'Cinto de mergulho com lastros de chumbo de 1kg cada, ajustavel',                         39.99,  20, TRUE, 4, 'https://images.unsplash.com/photo-1517963879433-6ad2b056d712?w=500&h=500&fit=crop&auto=format'),
('Porta-Bidon de Bicicleta',       'Suporte de bidon em aluminio universal para qualquer bicicleta',                          9.99,  90, TRUE, 4, 'https://images.unsplash.com/photo-1557803175-a11f01aed3e3?w=500&h=500&fit=crop&auto=format&sat=-20'),
('Toalha Microfibra Desporto',     'Toalha de microfibra extra-absorvente 80x160cm, secagem rapida',                         14.99,  75, TRUE, 4, 'https://images.unsplash.com/photo-1581647637898-0ec54ce29f36?w=500&h=500&fit=crop&auto=format'),
('Suporte Telemóvel Bicicleta',    'Suporte universal para telemóvel compativel com todos os guiodoes',                      12.99,  60, TRUE, 4, 'https://images.unsplash.com/photo-1502744688674-c619d1586c9e?w=500&h=500&fit=crop&auto=format');

-- ── Mais produtos — Natação (id=5) ───────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Prancha de Natação',             'Prancha flutuante em espuma EVA para treino de pernas',                                  14.99,  50, TRUE, 5, 'https://images.unsplash.com/photo-1519315901367-f34ff9154487?w=500&h=500&fit=crop&auto=format'),
('Snorkel e Máscara Set',          'Conjunto de snorkel com mascara de visao panoramica, anti-niebla',                       29.99,  35, TRUE, 5, 'https://images.unsplash.com/photo-1530549387789-4c1017266635?w=500&h=500&fit=crop&auto=format&sat=30'),
('Fato de Banho Masculino',        'Calção de banho de competição com elastano e cordon ajustavel',                          22.99,  45, TRUE, 5, 'https://images.unsplash.com/photo-1549476464-37392f717541?w=500&h=500&fit=crop&auto=format&sat=10'),
('Palmares de Natação',            'Palmares de treinoo para forcaa de braco, talla M, silicone',                             12.99,  40, TRUE, 5, 'https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=500&h=500&fit=crop&auto=format&sat=-20');

-- ── Mais produtos — Ciclismo (id=6) ──────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Bomba de Ar Portátil',           'Bomba de ar manual com manometro compativel com valvulas Presta e Schrader', 19.99,  55, TRUE, 6, 'https://images.unsplash.com/photo-1571731956672-f2b94d7dd0cb?w=500&h=500&fit=crop&auto=format'),
('Ciclocomputador GPS',            'Computador de bicicleta com GPS, cardio e integração com apps de treino',                79.99,  12, TRUE, 6, 'https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=500&h=500&fit=crop&auto=format&sat=15'),
('Luz LED Dianteira e Traseira',   'Set de luzes de bicicleta LED recarregaveis USB, 500 lumens',                            24.99,  70, TRUE, 6, 'https://images.unsplash.com/photo-1502744688674-c619d1586c9e?w=500&h=500&fit=crop&auto=format&sat=-10'),
('Sela Conforto Gel',              'Sela ergonomica com gel de memoria para maior conforto em longos percursos',              34.99,  25, TRUE, 6, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=500&h=500&fit=crop&auto=format&hue=200'),
('Camisola Ciclismo Manga Curta',  'Camisola de ciclismo aerodinamica com 3 bolsos traseiros e fecho em ziper',              54.99,  30, TRUE, 6, 'https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=500&h=500&fit=crop&auto=format&hue=150');

-- ── Mais produtos — Fitness (id=7) ────────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Barra de Dominadas Porta',       'Barra de dominadas para montagem em porta sem furos, ate 150kg',                         49.99,  20, TRUE, 7, 'https://images.unsplash.com/photo-1576678927484-cc907957088c?w=500&h=500&fit=crop&auto=format'),
('Corda de Saltar Speed',          'Corda de saltar profissional com rolamentos de aco, ajustavel',                           12.99, 100, TRUE, 7, 'https://images.unsplash.com/photo-1598971639058-fab3c3109a67?w=500&h=500&fit=crop&auto=format'),
('Rolo de Espuma Foam Roller',     'Foam roller de alta densidade para recuperacao muscular e miofascial',                    24.99,  45, TRUE, 7, 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=500&h=500&fit=crop&auto=format'),
('Kettlebell 16kg Ferro',          'Kettlebell de ferro fundido com base plana, revestimento antioxidante',                   44.99,  22, TRUE, 7, 'https://images.unsplash.com/photo-1517963879433-6ad2b056d712?w=500&h=500&fit=crop&auto=format&sat=20'),
('Stepper Degrau Ajustável',       'Degrau para step aerobics com 4 alturas ajustaveis e superficie antiderrapante',          39.99,  18, TRUE, 7, 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=500&h=500&fit=crop&auto=format&sat=-30'),
('Bolas Pilates 65cm',             'Bola de pilates anti-rebentamento com bomba incluida, 65cm de diametro',                 22.99,  35, TRUE, 7, 'https://images.unsplash.com/photo-1601925228016-f5e3d8e0a97c?w=500&h=500&fit=crop&auto=format&sat=20'),
('Halteres 10kg Par',              'Par de halteres revestidos a borracha 10kg, agarre ergonomico',                           54.99,  20, TRUE, 7, 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=500&h=500&fit=crop&auto=format&sat=30'),
('Luvas de Treino Fitness',        'Luvas de treino com acolchoamento na palma e suporte de pulso',                           16.99,  50, TRUE, 7, 'https://images.unsplash.com/photo-1577998474517-7eeeed4e448a?w=500&h=500&fit=crop&auto=format');

-- ── Produtos — Futebol (id=8) ─────────────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Chuteiras Campo Profissional',   'Chuteiras de futebol em pele sintetica para relva natural, sola TPU',                    89.99,  30, TRUE, 8, 'https://images.unsplash.com/photo-1543326727-cf6c39e8f84c?w=500&h=500&fit=crop&auto=format'),
('Chuteiras Relva Artificial',     'Chuteiras com sola multitravao para relva artificial e pisos sinteticos',                 69.99,  40, TRUE, 8, 'https://images.unsplash.com/photo-1551958219-acbc595b9e4f?w=500&h=500&fit=crop&auto=format'),
('Luvas Guarda-Redes Pro',         'Luvas de guarda-redes com latex de alta aderencia e protecao dos dedos',                  34.99,  25, TRUE, 8, 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=500&h=500&fit=crop&auto=format'),
('Bola de Treino Futebol',         'Bola de futebol para treino em piso rigido, tamanho 5, duravel',                          17.99,  60, TRUE, 8, 'https://images.unsplash.com/photo-1529516548873-9ce57c8f155e?w=500&h=500&fit=crop&auto=format'),
('Espinilleiras Carbon Pro',       'Espinilleiras em fibra de carbono com meias de compressao, tamanho M',                   22.99,  50, TRUE, 8, 'https://images.unsplash.com/photo-1589487391730-58f20eb2c308?w=500&h=500&fit=crop&auto=format'),
('Saco Mochila Futebol 40L',       'Mochila para equipamento de futebol com compartimento para bola e calcado', 39.99,  35, TRUE, 8, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500&h=500&fit=crop&auto=format&sat=20'),
('Baliza Portatil Mini Futebol',   'Baliza dobravel para treino individual ou em mini-campo, 1.2x0.8m',                      44.99,  15, TRUE, 8, 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=500&h=500&fit=crop&auto=format&sat=-10'),
('Balizas de Treino Set 6',        'Set de 6 balizas de treino dobravels em plastico resistente',                             19.99,  30, TRUE, 8, 'https://images.unsplash.com/photo-1552667466-07770ae110d0?w=500&h=500&fit=crop&auto=format');

-- ── Produtos — Running (id=9) ─────────────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Sapatilhas Running Carbono',     'Sapatilhas de competicao com placa de carbono e foam de reaccao',                        179.99, 12, TRUE, 9, 'https://images.unsplash.com/photo-1464207687429-7505649dae38?w=500&h=500&fit=crop&auto=format'),
('Sapatilhas Running Iniciante',   'Sapatilhas de corrida com amortecimento superior para principiantes',                     59.99,  40, TRUE, 9, 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=500&h=500&fit=crop&auto=format'),
('Camisola Running Refletora',     'Camisola tecnica com bandas refletoras 360 graus para corrida noturna',                   34.99,  45, TRUE, 9, 'https://images.unsplash.com/photo-1483721310020-03333e577078?w=500&h=500&fit=crop&auto=format'),
('Calcões Running 2-em-1',         'Calcoes com inner short de compressao integrado e bolso traseiro',                        28.99,  55, TRUE, 9, 'https://images.unsplash.com/photo-1562183241-b937e95585b6?w=500&h=500&fit=crop&auto=format&sat=20'),
('Meias Anti-Bolhas Running',      'Pack de 3 pares de meias tecnicas com amortecimento no calcanhar',                         9.99, 110, TRUE, 9, 'https://images.unsplash.com/photo-1582588678413-dbf45f4823e9?w=500&h=500&fit=crop&auto=format&sat=-10'),
('Smartwatch GPS Running',         'Relogio de desporto com GPS, cardiofrequencimetro optico e bateria 7 dias',              199.99,  8, TRUE, 9, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&h=500&fit=crop&auto=format'),
('Colete de Hidratação Trail 10L', 'Colete de hidratacao para trail running com reservatorio 2L e varios bolsos', 54.99,  20, TRUE, 9, 'https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=500&h=500&fit=crop&auto=format'),
('Cinto Running Porta-Telemóvel',  'Cinto elastico com bolso impermeavel para telemóvel e chaves durante corrida', 14.99,  65, TRUE, 9, 'https://images.unsplash.com/photo-1571731956672-f2b94d7dd0cb?w=500&h=500&fit=crop&auto=format&sat=-20'),
('Casaco Running Impermeável',     'Casaco de running impermeavel ultra-leve dobravel em bolso proprio',                      79.99,  25, TRUE, 9, 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=500&h=500&fit=crop&auto=format&sat=10');

-- ── Produtos — Montanhismo (id=10) ────────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Botas Trekking Gore-Tex',        'Botas de trekking impermeavel Gore-Tex com sola Vibram, ankle suporte',                 129.99,  18, TRUE, 10, 'https://images.unsplash.com/photo-1501555088652-021faa106b9b?w=500&h=500&fit=crop&auto=format'),
('Bastões de Trekking Carbono',    'Par de bastoes de trekking em carbono, dobravels em 3 secoes, 59-135cm',                 49.99,  25, TRUE, 10, 'https://images.unsplash.com/photo-1458442310124-dde6edb43d10?w=500&h=500&fit=crop&auto=format'),
('Mochila Trekking 40L',           'Mochila para montanha 40L com sistema de ventilacao e ajuste lombar',                    79.99,  14, TRUE, 10, 'https://images.unsplash.com/photo-1488085061387-422e29b40080?w=500&h=500&fit=crop&auto=format'),
('Impermeável Montanha 3 Camadas', 'Casaco de montanha em membrana de 3 camadas, costura soldada e capuz ajustavel',          89.99,  20, TRUE, 10, 'https://images.unsplash.com/photo-1579201838521-75ef11c9e4bc?w=500&h=500&fit=crop&auto=format'),
('Luvas de Trekking Touchscreen',  'Luvas de trekking com pontas dos dedos compativeis com ecras tacteis',                   19.99,  40, TRUE, 10, 'https://images.unsplash.com/photo-1576678927484-cc907957088c?w=500&h=500&fit=crop&auto=format&sat=-30'),
('Meias Trekking Lã Merino',       'Pack de 2 pares de meias de merino para trekking, antibacterianas',                      16.99,  55, TRUE, 10, 'https://images.unsplash.com/photo-1582588678413-dbf45f4823e9?w=500&h=500&fit=crop&auto=format&sat=30'),
('Lanterna Frontal 300 Lúmens',    'Lanterna frontal LED com 3 modos de luz, resistente a agua IPX4, pilhas incluidas',       29.99,  35, TRUE, 10, 'https://images.unsplash.com/photo-1518110925495-7c2f95e8dffc?w=500&h=500&fit=crop&auto=format'),
('Calças Trekking Convertíveis',   'Calcas de trekking convertidas em calcoes com cintura elastica e cinto',                  54.99,  28, TRUE, 10, 'https://images.unsplash.com/photo-1562183241-b937e95585b6?w=500&h=500&fit=crop&auto=format&sat=-10'),
('Cantil de Alumínio 750ml',       'Cantil de aluminio anodizado com tampa rosca hermética, BPA free',                        14.99,  60, TRUE, 10, 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&h=500&fit=crop&auto=format&sat=-20');

-- ── Produtos — Artes Marciais (id=11) ─────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Kimono Judo/Jiu-Jitsu Branco',   'Kimono branco de competicao em algodao 550g/m2, homologado IJF talla A3',               44.99,  20, TRUE, 11, 'https://images.unsplash.com/photo-1555597673-b21d5c935865?w=500&h=500&fit=crop&auto=format'),
('Luvas de Boxe 10oz Couro PU',    'Luvas de boxe em couro PU com fecho em velcro, espuma de alta densidade',                39.99,  35, TRUE, 11, 'https://images.unsplash.com/photo-1577998474517-7eeeed4e448a?w=500&h=500&fit=crop&auto=format'),
('Saco de Boxe 60kg com Suporte',  'Saco de boxe preenchido 60kg com suporte de chao em aco, cadeia incluida',               89.99,   8, TRUE, 11, 'https://images.unsplash.com/photo-1591548580938-6fe65e90f1c7?w=500&h=500&fit=crop&auto=format'),
('Capacete Full-Face MMA',         'Capacete de protecao completo para esparring, com grade de rosto e fecho',                49.99,  15, TRUE, 11, 'https://images.unsplash.com/photo-1516912481808-3406841bd33c?w=500&h=500&fit=crop&auto=format'),
('Espinilleiras MMA Profissional', 'Espinilleiras e protecao de pe MMA em neoprene com amortecimento de impacto',             24.99,  30, TRUE, 11, 'https://images.unsplash.com/photo-1555597673-b21d5c935865?w=500&h=500&fit=crop&auto=format&sat=20'),
('Protetor Bucal Termoformavel',   'Protetor bucal termoformavel com estojo e cinta de dentição, sabor neutro',               9.99,  80, TRUE, 11, 'https://images.unsplash.com/photo-1517963879433-6ad2b056d712?w=500&h=500&fit=crop&auto=format&sat=-40'),
('Bandagens de Boxe 4.5m',         'Pack de 2 bandagens de boxe em algodao elastico, 4.5m, protecao de punhos',               8.99, 100, TRUE, 11, 'https://images.unsplash.com/photo-1577998474517-7eeeed4e448a?w=500&h=500&fit=crop&auto=format&sat=-20'),
('Faixa de Karate Branca',         'Faixa de iniciante em algodão duplo, 4cm de largura, para karate e judo',                  6.99,  60, TRUE, 11, 'https://images.unsplash.com/photo-1591548580938-6fe65e90f1c7?w=500&h=500&fit=crop&auto=format&sat=-30');

-- ── Produtos — Golfe (id=12) ──────────────────────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Set Tacos Iniciante 9 Peças',    'Set completo de 9 tacos para iniciantes com saco e capas de protecao',                  199.99,   8, TRUE, 12, 'https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=500&h=500&fit=crop&auto=format'),
('Driver Titanio 460cc',           'Driver de golfe com cabeca em titanio 460cc e shaft de grafite, flex R',                149.99,   6, TRUE, 12, 'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=500&h=500&fit=crop&auto=format'),
('Saco de Golfe com Rodas',        'Saco de golfe trolley com 2 rodas e divisorias individuais para 14 tacos',               89.99,   8, TRUE, 12, 'https://images.unsplash.com/photo-1576839786769-02f31ab04703?w=500&h=500&fit=crop&auto=format'),
('Bolas de Golfe Pack 12',         'Pack de 12 bolas de golfe de 2 pecas para pratica e competicao',                         24.99,  40, TRUE, 12, 'https://images.unsplash.com/photo-1576839786769-02f31ab04703?w=500&h=500&fit=crop&auto=format&sat=20'),
('Luva de Golfe Cabretta',         'Luva de golfe em pele Cabretta para mao esquerda (right-handed), talla M',               14.99,  30, TRUE, 12, 'https://images.unsplash.com/photo-1516566438776-9a5a5e34aa4c?w=500&h=500&fit=crop&auto=format'),
('Polo de Golfe Anti-UV',          'Polo de golfe com protecao UPF50+ e tecnologia de secagem rapida',                        44.99,  25, TRUE, 12, 'https://images.unsplash.com/photo-1516566438776-9a5a5e34aa4c?w=500&h=500&fit=crop&auto=format&sat=10'),
('Tees de Golfe Pack 100',          'Pack de 100 tees de madeira em varios comprimentos (35mm, 54mm, 70mm)',                   5.99, 150, TRUE, 12, 'https://images.unsplash.com/photo-1576839786769-02f31ab04703?w=500&h=500&fit=crop&auto=format&sat=-20'),
('Putter de Golfe Mallet',         'Putter de golfe cabeca mallet com alinhamento duplo e shaft de aco',                     79.99,   5, TRUE, 12, 'https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=500&h=500&fit=crop&auto=format&sat=-10');

-- ── Produtos — Desportos de Inverno (id=13) ───────────────────────
INSERT INTO products (name, description, price, stock, active, category_id, image_url) VALUES
('Esquis Alpinos 160cm',           'Esquis alpinos all-mountain 160cm com fixacoes Marker incluidas',                       299.99,   6, TRUE, 13, 'https://images.unsplash.com/photo-1491555103944-7c647fd857e6?w=500&h=500&fit=crop&auto=format'),
('Botas de Ski 25.5 (Flex 80)',    'Botas de ski em plastico ABS com flexibilidade 80, fecho em rato',                      179.99,   8, TRUE, 13, 'https://images.unsplash.com/photo-1478358161113-b0e11994a36b?w=500&h=500&fit=crop&auto=format'),
('Capacete Ski/Snowboard',         'Capacete multidesporto neve com ventilacao ajustavel e audio integrado',                  89.99,  12, TRUE, 13, 'https://images.unsplash.com/photo-1547975515-9f14d1b8cd4c?w=500&h=500&fit=crop&auto=format'),
('Óculos de Ski Anti-Névoa',       'Oculos de ski dupla lente anti-niebla com tratamento UV400 e espuma tripla',              59.99,  18, TRUE, 13, 'https://images.unsplash.com/photo-1491555103944-7c647fd857e6?w=500&h=500&fit=crop&auto=format&sat=30'),
('Luvas Ski Impermeáveis Gore-Tex', 'Luvas de ski em Gore-Tex com isolamento Thinsulate e tecido de protecao',               39.99,  22, TRUE, 13, 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=500&h=500&fit=crop&auto=format&sat=-30'),
('Casaco Ski 20000mm Membrana',    'Casaco de ski com membrana 20000mm impermeavel e isolamento 200g',                       149.99,  10, TRUE, 13, 'https://images.unsplash.com/photo-1578933800959-a07f1a3e2e46?w=500&h=500&fit=crop&auto=format'),
('Calças Bib Ski com Alças',       'Calças bib de ski com alças ajustaveis, impermeavel 15000mm, joelhos reforcados',         99.99,   8, TRUE, 13, 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=500&h=500&fit=crop&auto=format'),
('Prancha Snowboard 155cm',        'Prancha de snowboard all-mountain 155cm, perfil camber, madeira de nucleo',              249.99,   5, TRUE, 13, 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=500&h=500&fit=crop&auto=format&sat=20'),
('Proteções de Pulso Snowboard',   'Proteções ergonomicas de pulso para snowboard, certificadas CE, talla M',                 22.99,  30, TRUE, 13, 'https://images.unsplash.com/photo-1547975515-9f14d1b8cd4c?w=500&h=500&fit=crop&auto=format&sat=-20');
