-- Torna a pesquisa em produtos insensivel a acentos e maiusculas/minusculas.
-- A colacao utf8mb4_0900_ai_ci (Accent Insensitive, Case Insensitive) faz com que
-- LIKE '%competicao%' corresponda a 'Competição', 'futbol' a 'Futebol', etc.
-- Os indices e os dados existentes mantem-se; so o comportamento de comparacao muda.

ALTER TABLE products MODIFY name        VARCHAR(150) NOT NULL COLLATE utf8mb4_0900_ai_ci;
ALTER TABLE products MODIFY description TEXT                   COLLATE utf8mb4_0900_ai_ci;
