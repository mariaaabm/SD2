-- ============================================================
-- SportFlow — Configuração inicial da base de dados MySQL
-- Executar como root: mysql -u root -p < scripts/setup-mysql.sql
-- ============================================================

-- Criar base de dados
CREATE DATABASE IF NOT EXISTS sports_store
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Criar utilizador da aplicação
CREATE USER IF NOT EXISTS 'sportflow'@'localhost' IDENTIFIED BY 'sportflow123';
CREATE USER IF NOT EXISTS 'sportflow'@'%'         IDENTIFIED BY 'sportflow123';

-- Conceder permissões
GRANT ALL PRIVILEGES ON sports_store.* TO 'sportflow'@'localhost';
GRANT ALL PRIVILEGES ON sports_store.* TO 'sportflow'@'%';

FLUSH PRIVILEGES;

SELECT 'Base de dados sports_store criada com sucesso!' AS resultado;
