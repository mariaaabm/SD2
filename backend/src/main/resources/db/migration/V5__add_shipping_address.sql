-- Adiciona campos de morada de envio e método de pagamento às vendas
ALTER TABLE sales
    ADD COLUMN shipping_name        VARCHAR(150),
    ADD COLUMN shipping_phone       VARCHAR(30),
    ADD COLUMN shipping_address     VARCHAR(255),
    ADD COLUMN shipping_address2    VARCHAR(255),
    ADD COLUMN shipping_postal_code VARCHAR(20),
    ADD COLUMN shipping_city        VARCHAR(100),
    ADD COLUMN shipping_region      VARCHAR(100),
    ADD COLUMN shipping_country     VARCHAR(80),
    ADD COLUMN payment_method       VARCHAR(30);
