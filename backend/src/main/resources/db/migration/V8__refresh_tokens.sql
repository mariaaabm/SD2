-- Cria a tabela refresh_tokens que armazena os tokens emitidos para prolongar a sessão sem voltar a pedir credenciais, com expiração e cascade delete para apagar os tokens automaticamente quando o cliente é removido.
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_refresh_token (token),
    CONSTRAINT fk_refresh_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);
