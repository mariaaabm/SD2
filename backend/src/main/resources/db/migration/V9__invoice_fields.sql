-- Acrescenta os campos necessarios para emitir uma fatura completa (PT):
--  * Base tributavel, IVA e taxa aplicada por venda
--  * Serie do documento e data da operacao na fatura
-- Os precos do catalogo sao considerados com IVA incluido (default 23%).
-- Usamos ALTER TABLE separados (em vez de virgulas) para compatibilidade
-- com H2 MODE=MySQL nos testes de integracao.

ALTER TABLE sales ADD COLUMN subtotal_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE sales ADD COLUMN vat_amount      DECIMAL(10, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE sales ADD COLUMN vat_rate        DECIMAL(5, 2)  NOT NULL DEFAULT 23.00;

-- Para vendas existentes, calcula base e IVA a partir do total (IVA incluido a 23%)
UPDATE sales
   SET subtotal_amount = ROUND(total / 1.23, 2),
       vat_amount      = ROUND(total - (total / 1.23), 2),
       vat_rate        = 23.00
 WHERE subtotal_amount = 0 AND total > 0;

ALTER TABLE invoices ADD COLUMN series         VARCHAR(20) NOT NULL DEFAULT 'SP';
ALTER TABLE invoices ADD COLUMN operation_date TIMESTAMP   NULL;

UPDATE invoices SET operation_date = issued_at WHERE operation_date IS NULL;
