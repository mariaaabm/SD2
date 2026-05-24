package pt.ubi.gruposd.loja.model;

// Enumera os estados possíveis de uma encomenda ao longo do ciclo de vida, desde CONFIRMED quando o cliente finaliza o checkout até DELIVERED quando chega ao destinatário, com CANCELLED como ramo paralelo possível em qualquer momento.
public enum SaleStatus {
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
