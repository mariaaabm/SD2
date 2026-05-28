package pt.ubi.gruposd.loja.service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.InvoiceResponse;
import pt.ubi.gruposd.loja.dto.SaleItemResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Invoice;
import pt.ubi.gruposd.loja.model.Sale;
import pt.ubi.gruposd.loja.repository.InvoiceRepository;
import pt.ubi.gruposd.loja.repository.SaleItemRepository;

// Cria e monta as faturas das vendas confirmadas.
// Gera o número sequencial (ex.: SP2025/000042), calcula o IVA e produz o DTO completo para a página de fatura.
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

    // Cria e grava uma fatura nova para a venda dada com série SP, número sequencial, data de emissão e data da operação.
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

    // Monta o DTO completo da fatura com emitente, cliente, linhas de produto, totais de IVA e metadados de certificação.
    public InvoiceResponse toResponse(Invoice invoice, Sale sale, List<SaleItemResponse> items) {
        InvoiceResponse.Issuer issuer = InvoiceResponse.Issuer.sportFlowDefault();
        InvoiceResponse.Party customer = buildCustomerParty(sale);
        InvoiceResponse.Party shipping = buildShippingParty(sale);

        List<InvoiceResponse.Line> lines = items.stream()
            .map(item -> new InvoiceResponse.Line(
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

        List<InvoiceResponse.VatSummary> vatSummary = List.of(
            new InvoiceResponse.VatSummary(vatRate, subtotal, vatAmount)
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

    private InvoiceResponse.Party buildCustomerParty(Sale sale) {
        Customer customer = sale.getCustomer();
        return new InvoiceResponse.Party(
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

    private InvoiceResponse.Party buildShippingParty(Sale sale) {
        return new InvoiceResponse.Party(
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

    // Lança NotFoundException (e não 401) para não revelar que a fatura existe mas pertence a outro cliente.
    private void ensureInvoiceBelongsToCustomer(Invoice invoice, Customer customer) {
        if (!invoice.getSale().getCustomer().getId().equals(customer.getId())) {
            throw new NotFoundException("Fatura nao encontrada.");
        }
    }

    // Formato: SP{ano}/{id com 6 dígitos} → ex.: SP2025/000042.
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

    // Simula o hash de controlo AT: SHA-256 do número+data+total, devolvendo 4 caracteres separados por reticências.
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
