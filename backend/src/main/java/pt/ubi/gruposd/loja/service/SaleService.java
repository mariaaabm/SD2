package pt.ubi.gruposd.loja.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import pt.ubi.gruposd.loja.model.SaleStatus;
import pt.ubi.gruposd.loja.repository.InvoiceRepository;
import pt.ubi.gruposd.loja.repository.ProductRepository;
import pt.ubi.gruposd.loja.repository.SaleItemRepository;
import pt.ubi.gruposd.loja.repository.SaleRepository;

@Service
public class SaleService {
    static final BigDecimal DEFAULT_VAT_RATE = new BigDecimal("23.00");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final EmailService emailService;

    public SaleService(
        SaleRepository saleRepository,
        SaleItemRepository saleItemRepository,
        ProductRepository productRepository,
        InvoiceRepository invoiceRepository,
        InvoiceService invoiceService,
        EmailService emailService
    ) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
        this.emailService = emailService;
    }

    @Transactional
    public SaleResponse checkout(Customer customer, CheckoutRequest request) {
        if (request.items().isEmpty()) {
            throw new BadRequestException("O carrinho nao pode estar vazio.");
        }

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setTotal(BigDecimal.ZERO);
        sale.setSubtotal(BigDecimal.ZERO);
        sale.setVatAmount(BigDecimal.ZERO);
        sale.setVatRate(DEFAULT_VAT_RATE);
        sale.setShippingName(request.shippingName());
        sale.setShippingPhone(request.shippingPhone());
        sale.setShippingAddress(request.shippingAddress());
        sale.setShippingAddress2(request.shippingAddress2());
        sale.setShippingPostalCode(request.shippingPostalCode());
        sale.setShippingCity(request.shippingCity());
        sale.setShippingRegion(request.shippingRegion());
        sale.setShippingCountry(request.shippingCountry());
        sale.setPaymentMethod(request.paymentMethod());
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

        BigDecimal subtotal = netFromGross(total, DEFAULT_VAT_RATE);
        BigDecimal vatAmount = total.subtract(subtotal);

        savedSale.setTotal(total);
        savedSale.setSubtotal(subtotal);
        savedSale.setVatAmount(vatAmount);
        savedSale.setVatRate(DEFAULT_VAT_RATE);

        saleItemRepository.saveAll(saleItems);
        Invoice invoice = invoiceService.createForSale(savedSale);

        SaleResponse response = toResponse(savedSale, saleItems, invoice);
        emailService.sendOrderConfirmation(customer.getEmail(), customer.getName(), response);
        return response;
    }

    @Transactional
    public SaleResponse updateStatus(Long saleId, SaleStatus status) {
        Sale sale = saleRepository.findById(saleId)
            .orElseThrow(() -> new NotFoundException("Venda nao encontrada."));
        sale.setStatus(status);
        return toResponse(sale, saleItemRepository.findBySaleId(sale.getId()), findInvoice(sale));
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
        BigDecimal vatRate = sale.getVatRate() == null ? DEFAULT_VAT_RATE : sale.getVatRate();

        List<SaleItemResponse> itemResponses = saleItems.stream()
            .map(item -> toItemResponse(item, vatRate))
            .toList();

        InvoiceResponse invoiceResponse = invoice == null
            ? null
            : invoiceService.toResponse(invoice, sale, itemResponses);

        BigDecimal subtotal = sale.getSubtotal() != null ? sale.getSubtotal()
            : netFromGross(sale.getTotal(), vatRate);
        BigDecimal vatAmount = sale.getVatAmount() != null ? sale.getVatAmount()
            : sale.getTotal().subtract(subtotal);

        return new SaleResponse(
            sale.getId(),
            sale.getCustomer().getId(),
            sale.getCustomer().getName(),
            sale.getCreatedAt(),
            subtotal,
            vatAmount,
            vatRate,
            sale.getTotal(),
            sale.getStatus(),
            itemResponses,
            invoiceResponse,
            sale.getShippingName(),
            sale.getShippingPhone(),
            sale.getShippingAddress(),
            sale.getShippingAddress2(),
            sale.getShippingPostalCode(),
            sale.getShippingCity(),
            sale.getShippingRegion(),
            sale.getShippingCountry(),
            sale.getPaymentMethod()
        );
    }

    private Invoice findInvoice(Sale sale) {
        return invoiceRepository.findBySaleId(sale.getId()).orElse(null);
    }

    private SaleItemResponse toItemResponse(SaleItem item, BigDecimal vatRate) {
        BigDecimal unitGross = item.getUnitPrice();
        BigDecimal unitNet = netFromGross(unitGross, vatRate);
        BigDecimal subtotalGross = unitGross.multiply(BigDecimal.valueOf(item.getQuantity()));
        BigDecimal subtotalNet = unitNet.multiply(BigDecimal.valueOf(item.getQuantity()))
            .setScale(2, RoundingMode.HALF_UP);
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
    }

    static BigDecimal netFromGross(BigDecimal gross, BigDecimal vatRatePercent) {
        if (gross == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal divisor = BigDecimal.ONE.add(vatRatePercent.divide(HUNDRED, 6, RoundingMode.HALF_UP));
        return gross.divide(divisor, 2, RoundingMode.HALF_UP);
    }
}
