package pt.ubi.gruposd.loja.service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.InvoiceIssuer;
import pt.ubi.gruposd.loja.dto.InvoiceLine;
import pt.ubi.gruposd.loja.dto.InvoiceParty;
import pt.ubi.gruposd.loja.dto.InvoiceResponse;
import pt.ubi.gruposd.loja.dto.InvoiceVatSummary;
import pt.ubi.gruposd.loja.dto.SaleItemResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Invoice;
import pt.ubi.gruposd.loja.model.Sale;
import pt.ubi.gruposd.loja.repository.InvoiceRepository;
import pt.ubi.gruposd.loja.repository.SaleItemRepository;

// Cria, persiste e devolve as faturas associadas a uma venda confirmada, gera o número sequencial no formato SP{ano}/{id}, calcula a discriminação de IVA a partir do total da venda e monta o DTO completo que a página da fatura precisa de mostrar.
@Service
public class InvoiceService {
    private static final String DEFAULT_SERIES = "SP";
    private static final String DOCUMENT_TYPE = "Fatura";
    private static final String CURRENCY = "EUR";
    private static final String CERTIFICATION_TEXT =
        "Documento processado por programa informatico (simulado para fins academicos).";
    private static final String DEFAULT_NOTES =
        "Documento emitido eletronicamente. IVA incluido a taxa em vigor.";

    private final InvoiceRepository invoiceRepository;
    private final SaleItemRepository saleItemRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, SaleItemRepository saleItemRepository) {
        this.invoiceRepository = invoiceRepository;
        this.saleItemRepository = saleItemRepository;
    }

    // Cria e persiste uma fatura nova associada à venda dada, atribui a série padrão SP, gera o número sequencial baseado no ano da venda e no id, e regista a data de emissão e a data da operação para constarem no documento.
    @Transactional
    public Invoice createForSale(Sale sale) {
        Invoice invoice = new Invoice();
        invoice.setSale(sale);
        invoice.setSeries(DEFAULT_SERIES);
        invoice.setInvoiceNumber(generateInvoiceNumber(sale));
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setOperationDate(sale.getCreatedAt());
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findCustomerInvoiceById(Customer customer, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new NotFoundException("Fatura nao encontrada."));

        ensureInvoiceBelongsToCustomer(invoice, customer);
        return buildResponse(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findCustomerInvoiceBySaleId(Customer customer, Long saleId) {
        Invoice invoice = invoiceRepository.findBySaleId(saleId)
            .orElseThrow(() -> new NotFoundException("Fatura nao encontrada."));

        ensureInvoiceBelongsToCustomer(invoice, customer);
        return buildResponse(invoice);
    }

    // Monta a resposta completa da fatura agrupando os dados do emitente, do adquirente, das linhas com IVA discriminado, do resumo de IVA por taxa, dos totais e dos metadados de certificação para a página de fatura conseguir renderizar tudo num único pedido.
    public InvoiceResponse toResponse(Invoice invoice, Sale sale, List<SaleItemResponse> items) {
        InvoiceIssuer issuer = InvoiceIssuer.sportFlowDefault();
        InvoiceParty customer = buildCustomerParty(sale);
        InvoiceParty shipping = buildShippingParty(sale);

        List<InvoiceLine> lines = items.stream()
            .map(item -> new InvoiceLine(
                item.productId(),
                item.productName(),
                item.quantity(),
                item.unitPriceNet(),
                item.unitPrice(),
                item.vatRate(),
                item.subtotalNet(),
                item.vatAmount(),
                item.subtotal()
            ))
            .toList();

        BigDecimal subtotal = sale.getSubtotal() == null ? BigDecimal.ZERO : sale.getSubtotal();
        BigDecimal vatAmount = sale.getVatAmount() == null ? BigDecimal.ZERO : sale.getVatAmount();
        BigDecimal vatRate = sale.getVatRate() == null ? SaleService.DEFAULT_VAT_RATE : sale.getVatRate();

        List<InvoiceVatSummary> vatSummary = List.of(
            new InvoiceVatSummary(vatRate, subtotal, vatAmount)
        );

        String formattedNumber = formatNumber(invoice);
        String atcud = buildAtcud(invoice);
        String hashControl = buildHash(invoice, sale.getTotal());

        return new InvoiceResponse(
            invoice.getId(),
            sale.getId(),
            DOCUMENT_TYPE,
            invoice.getSeries(),
            invoice.getInvoiceNumber(),
            formattedNumber,
            invoice.getIssuedAt(),
            invoice.getOperationDate() != null ? invoice.getOperationDate() : sale.getCreatedAt(),
            issuer,
            customer,
            shipping,
            lines,
            vatSummary,
            subtotal,
            vatAmount,
            sale.getTotal(),
            CURRENCY,
            sale.getPaymentMethod(),
            "Pronto pagamento",
            DEFAULT_NOTES,
            atcud,
            hashControl,
            CERTIFICATION_TEXT
        );
    }

    private InvoiceResponse buildResponse(Invoice invoice) {
        Sale sale = invoice.getSale();
        List<SaleItemResponse> items = saleItemRepository.findBySaleId(sale.getId())
            .stream()
            .map(item -> {
                BigDecimal vatRate = sale.getVatRate() == null
                    ? SaleService.DEFAULT_VAT_RATE
                    : sale.getVatRate();
                BigDecimal unitGross = item.getUnitPrice();
                BigDecimal unitNet = SaleService.netFromGross(unitGross, vatRate);
                BigDecimal subtotalGross = unitGross.multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal subtotalNet = unitNet.multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal vatAmount = subtotalGross.subtract(subtotalNet);
                return new SaleItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    unitGross,
                    unitNet,
                    vatRate,
                    vatAmount,
                    subtotalNet,
                    subtotalGross
                );
            })
            .toList();
        return toResponse(invoice, sale, items);
    }

    private InvoiceParty buildCustomerParty(Sale sale) {
        Customer customer = sale.getCustomer();
        return new InvoiceParty(
            sale.getShippingName() != null ? sale.getShippingName() : customer.getName(),
            "Consumidor Final",
            customer.getEmail(),
            sale.getShippingPhone(),
            sale.getShippingAddress(),
            sale.getShippingAddress2(),
            sale.getShippingPostalCode(),
            sale.getShippingCity(),
            sale.getShippingRegion(),
            sale.getShippingCountry()
        );
    }

    private InvoiceParty buildShippingParty(Sale sale) {
        return new InvoiceParty(
            sale.getShippingName(),
            null,
            null,
            sale.getShippingPhone(),
            sale.getShippingAddress(),
            sale.getShippingAddress2(),
            sale.getShippingPostalCode(),
            sale.getShippingCity(),
            sale.getShippingRegion(),
            sale.getShippingCountry()
        );
    }

    private void ensureInvoiceBelongsToCustomer(Invoice invoice, Customer customer) {
        if (!invoice.getSale().getCustomer().getId().equals(customer.getId())) {
            throw new NotFoundException("Fatura nao encontrada.");
        }
    }

    private String generateInvoiceNumber(Sale sale) {
        int year = sale.getCreatedAt() != null ? sale.getCreatedAt().getYear() : LocalDateTime.now().getYear();
        return String.format("%s%d/%06d", DEFAULT_SERIES, year, sale.getId());
    }

    private String formatNumber(Invoice invoice) {
        return DOCUMENT_TYPE + " " + invoice.getInvoiceNumber();
    }

    private String buildAtcud(Invoice invoice) {
        return "AAAAAAAA-" + invoice.getId();
    }

    // Calcula um hash SHA-256 a partir do número da fatura, data de emissão e total para simular o hash de controlo das faturas certificadas pela AT, e devolve apenas alguns caracteres separados por reticências como acontece em faturas reais.
    private String buildHash(Invoice invoice, BigDecimal total) {
        String payload = invoice.getInvoiceNumber() + "|"
            + (invoice.getIssuedAt() != null ? invoice.getIssuedAt().toString() : "")
            + "|" + (total != null ? total.toPlainString() : "0.00");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes());
            String hex = HexFormat.of().formatHex(hash).toUpperCase();
            return hex.charAt(0) + "..." + hex.charAt(10) + "..."
                + hex.charAt(20) + "..." + hex.charAt(30);
        } catch (NoSuchAlgorithmException ex) {
            return "----";
        }
    }
}
