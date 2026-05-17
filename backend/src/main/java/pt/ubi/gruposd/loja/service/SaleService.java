package pt.ubi.gruposd.loja.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.CartItemRequest;
import pt.ubi.gruposd.loja.dto.CheckoutRequest;
import pt.ubi.gruposd.loja.dto.InvoiceResponse;
import pt.ubi.gruposd.loja.dto.SaleItemResponse;
import pt.ubi.gruposd.loja.dto.SaleResponse;
import pt.ubi.gruposd.loja.exception.BadRequestException;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Invoice;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.model.Sale;
import pt.ubi.gruposd.loja.model.SaleItem;
import pt.ubi.gruposd.loja.repository.InvoiceRepository;
import pt.ubi.gruposd.loja.repository.ProductRepository;
import pt.ubi.gruposd.loja.repository.SaleItemRepository;
import pt.ubi.gruposd.loja.repository.SaleRepository;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    public SaleService(
        SaleRepository saleRepository,
        SaleItemRepository saleItemRepository,
        ProductRepository productRepository,
        InvoiceRepository invoiceRepository,
        InvoiceService invoiceService
    ) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }

    @Transactional
    public SaleResponse checkout(Customer customer, CheckoutRequest request) {
        if (request.items().isEmpty()) {
            throw new BadRequestException("O carrinho nao pode estar vazio.");
        }

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setTotal(BigDecimal.ZERO);
        Sale savedSale = saleRepository.save(sale);

        BigDecimal total = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();

        for (CartItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new NotFoundException("Produto nao encontrado."));

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new BadRequestException("Produto indisponivel: " + product.getName());
            }

            if (product.getStock() < itemRequest.quantity()) {
                throw new BadRequestException("Stock insuficiente para o produto: " + product.getName());
            }

            product.setStock(product.getStock() - itemRequest.quantity());

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(savedSale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setUnitPrice(product.getPrice());

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            saleItems.add(saleItem);
        }

        savedSale.setTotal(total);
        saleItemRepository.saveAll(saleItems);
        Invoice invoice = invoiceService.createForSale(savedSale);

        return toResponse(savedSale, saleItems, invoice);
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> findCustomerSales(Customer customer) {
        return saleRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
            .stream()
            .map(sale -> toResponse(sale, saleItemRepository.findBySaleId(sale.getId()), findInvoice(sale)))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> findAllSales() {
        return saleRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(sale -> toResponse(sale, saleItemRepository.findBySaleId(sale.getId()), findInvoice(sale)))
            .toList();
    }

    @Transactional(readOnly = true)
    public SaleResponse findCustomerSaleById(Customer customer, Long saleId) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new NotFoundException("Venda nao encontrada."));

        if (!sale.getCustomer().getId().equals(customer.getId())) {
            throw new NotFoundException("Venda nao encontrada.");
        }

        return toResponse(sale, saleItemRepository.findBySaleId(sale.getId()), findInvoice(sale));
    }

    private SaleResponse toResponse(Sale sale, List<SaleItem> saleItems, Invoice invoice) {
        List<SaleItemResponse> itemResponses = saleItems.stream()
            .map(this::toItemResponse)
            .toList();

        InvoiceResponse invoiceResponse = invoice == null ? null : invoiceService.toResponse(invoice);

        return new SaleResponse(
            sale.getId(),
            sale.getCustomer().getId(),
            sale.getCustomer().getName(),
            sale.getCreatedAt(),
            sale.getTotal(),
            sale.getStatus(),
            itemResponses,
            invoiceResponse
        );
    }

    private Invoice findInvoice(Sale sale) {
        return invoiceRepository.findBySaleId(sale.getId()).orElse(null);
    }

    private SaleItemResponse toItemResponse(SaleItem item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return new SaleItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getUnitPrice(),
            subtotal
        );
    }
}
