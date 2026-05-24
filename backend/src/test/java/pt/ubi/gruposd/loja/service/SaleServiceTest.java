package pt.ubi.gruposd.loja.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ubi.gruposd.loja.dto.CartItemRequest;
import pt.ubi.gruposd.loja.dto.CheckoutRequest;
import pt.ubi.gruposd.loja.dto.SaleResponse;
import pt.ubi.gruposd.loja.exception.BadRequestException;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Category;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Invoice;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.model.Sale;
import pt.ubi.gruposd.loja.model.SaleStatus;
import pt.ubi.gruposd.loja.model.UserRole;
import pt.ubi.gruposd.loja.repository.InvoiceRepository;
import pt.ubi.gruposd.loja.repository.ProductRepository;
import pt.ubi.gruposd.loja.repository.SaleItemRepository;
import pt.ubi.gruposd.loja.repository.SaleRepository;

// Testa o SaleService de forma isolada com Mockito a simular os repositórios, o InvoiceService e o EmailService, verifica que o checkout reduz stock e cria a fatura, que valida stock insuficiente, produto inativo e produto inexistente, e que a mudança de estado só atua sobre vendas existentes.
@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private SaleItemRepository saleItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InvoiceRepository invoiceRepository;
    @Mock private InvoiceService invoiceService;
    @Mock private EmailService emailService;

    private SaleService saleService;
    private Customer customer;
    private Product product;
    private CheckoutRequest validRequest;

    // Prepara antes de cada teste uma instância nova do SaleService com todos os mocks injetados, e cria os objetos auxiliares cliente, produto e pedido de checkout válidos que a maior parte dos testes reutiliza.
    @BeforeEach
    void setUp() {
        saleService = new SaleService(
            saleRepository, saleItemRepository, productRepository, invoiceRepository, invoiceService, emailService
        );

        Category category = new Category();
        category.setId(1L);
        category.setName("Calçado");

        customer = new Customer();
        customer.setId(1L);
        customer.setName("João Silva");
        customer.setEmail("joao@teste.pt");
        customer.setRole(UserRole.CLIENT);

        product = new Product();
        product.setId(10L);
        product.setName("Sapatilha X");
        product.setPrice(new BigDecimal("49.99"));
        product.setStock(5);
        product.setActive(true);
        product.setCategory(category);

        validRequest = new CheckoutRequest(
            List.of(new CartItemRequest(10L, 2)),
            "João Silva", "910000000",
            "Rua Principal, 1", null,
            "1000-001", "Lisboa", "Lisboa",
            "Portugal", "CARD"
        );
    }

    // Garante que um checkout com stock suficiente cria a venda, abate as unidades vendidas ao stock do produto e dispara a criação da fatura associada.
    @Test
    void checkout_createsSaleAndReducesStock() {
        Sale savedSale = new Sale();
        savedSale.setId(100L);
        savedSale.setCustomer(customer);
        savedSale.setTotal(BigDecimal.ZERO);

        when(saleRepository.save(any(Sale.class))).thenReturn(savedSale);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(saleItemRepository.saveAll(any())).thenReturn(List.of());

        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("INV-001");
        when(invoiceService.createForSale(savedSale)).thenReturn(invoice);
        when(invoiceService.toResponse(any(Invoice.class), any(Sale.class), anyList())).thenReturn(
            new pt.ubi.gruposd.loja.dto.InvoiceResponse(
                1L, 100L, "Fatura", "SP", "INV-001", "Fatura INV-001",
                null, null, null, null, null,
                List.of(), List.of(),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                "EUR", "CARD", null, null, null, null, null
            )
        );

        SaleResponse result = saleService.checkout(customer, validRequest);

        assertThat(result.id()).isEqualTo(100L);
        assertThat(product.getStock()).isEqualTo(3);
        verify(saleItemRepository).saveAll(any());
        verify(invoiceService).createForSale(savedSale);
    }

    // Verifica que tentar comprar mais unidades do que existem em stock rejeita o pedido com BadRequestException e mensagem que identifica o problema.
    @Test
    void checkout_throwsBadRequest_whenInsufficientStock() {
        product.setStock(1);
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(99L);
            return s;
        });
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> saleService.checkout(customer, validRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Stock insuficiente");
    }

    // Verifica que comprar um produto marcado como inativo é bloqueado com BadRequestException, mesmo que ainda haja stock disponível na base de dados.
    @Test
    void checkout_throwsBadRequest_whenProductInactive() {
        product.setActive(false);
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(99L);
            return s;
        });
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> saleService.checkout(customer, validRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("indisponivel");
    }

    // Verifica que um pedido de checkout que referencia um produto inexistente é rejeitado com NotFoundException em vez de criar uma venda inválida.
    @Test
    void checkout_throwsBadRequest_whenProductNotFound() {
        when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> {
            Sale s = inv.getArgument(0);
            s.setId(99L);
            return s;
        });
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.checkout(customer, validRequest))
            .isInstanceOf(NotFoundException.class);
    }

    // Confirma que a mudança de estado de uma venda atualiza o campo na entidade gerida pela transação e devolve um SaleResponse já com o novo estado.
    @Test
    void updateStatus_changesStatusAndReturnsResponse() {
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setCustomer(customer);
        sale.setTotal(new BigDecimal("49.99"));
        sale.setStatus(SaleStatus.CONFIRMED);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleItemRepository.findBySaleId(1L)).thenReturn(List.of());
        when(invoiceRepository.findBySaleId(1L)).thenReturn(Optional.empty());

        SaleResponse result = saleService.updateStatus(1L, SaleStatus.PROCESSING);

        assertThat(sale.getStatus()).isEqualTo(SaleStatus.PROCESSING);
        assertThat(result.status()).isEqualTo(SaleStatus.PROCESSING);
    }

    // Verifica que tentar mudar o estado de uma venda que não existe lança NotFoundException em vez de criar uma venda nova ou falhar silenciosamente.
    @Test
    void updateStatus_throwsNotFoundException_whenSaleNotFound() {
        when(saleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.updateStatus(999L, SaleStatus.SHIPPED))
            .isInstanceOf(NotFoundException.class);
    }

    // Garante que a listagem das vendas do cliente usa o método filtrado por customerId em vez do método que devolveria todas as vendas, evitando expor vendas de outros clientes.
    @Test
    void findCustomerSales_returnsOnlyThatCustomersSales() {
        Sale sale = new Sale();
        sale.setId(1L);
        sale.setCustomer(customer);
        sale.setTotal(new BigDecimal("49.99"));

        when(saleRepository.findByCustomerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(sale));
        when(saleItemRepository.findBySaleId(1L)).thenReturn(List.of());
        when(invoiceRepository.findBySaleId(1L)).thenReturn(Optional.empty());

        List<SaleResponse> result = saleService.findCustomerSales(customer);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).customerId()).isEqualTo(1L);
        verify(saleRepository, never()).findAllByOrderByCreatedAtDesc();
    }
}
