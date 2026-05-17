package pt.ubi.gruposd.loja.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.InvoiceResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Invoice;
import pt.ubi.gruposd.loja.model.Sale;
import pt.ubi.gruposd.loja.repository.InvoiceRepository;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Invoice createForSale(Sale sale) {
        Invoice invoice = new Invoice();
        invoice.setSale(sale);
        invoice.setInvoiceNumber(generateInvoiceNumber(sale));

        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findCustomerInvoiceById(Customer customer, Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new NotFoundException("Fatura nao encontrada."));

        ensureInvoiceBelongsToCustomer(invoice, customer);
        return toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findCustomerInvoiceBySaleId(Customer customer, Long saleId) {
        Invoice invoice = invoiceRepository.findBySaleId(saleId)
            .orElseThrow(() -> new NotFoundException("Fatura nao encontrada."));

        ensureInvoiceBelongsToCustomer(invoice, customer);
        return toResponse(invoice);
    }

    public InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getSale().getId(),
            invoice.getInvoiceNumber(),
            invoice.getIssuedAt()
        );
    }

    private void ensureInvoiceBelongsToCustomer(Invoice invoice, Customer customer) {
        if (!invoice.getSale().getCustomer().getId().equals(customer.getId())) {
            throw new NotFoundException("Fatura nao encontrada.");
        }
    }

    private String generateInvoiceNumber(Sale sale) {
        return "FT-" + sale.getCreatedAt().getYear() + "-" + String.format("%06d", sale.getId());
    }
}
