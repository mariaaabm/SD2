package pt.ubi.gruposd.loja.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.CustomerResponse;
import pt.ubi.gruposd.loja.dto.SaleResponse;
import pt.ubi.gruposd.loja.dto.UpdateSaleStatusRequest;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.repository.CustomerRepository;
import pt.ubi.gruposd.loja.service.SaleService;

// Endpoints do backoffice em /api/admin — apenas acessíveis a utilizadores com role ADMIN.
// Permite listar todas as vendas, mudar o estado de uma encomenda e ver os clientes registados.
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final SaleService saleService;
    private final CustomerRepository customerRepository;

    public AdminController(SaleService saleService, CustomerRepository customerRepository) {
        this.saleService = saleService;
        this.customerRepository = customerRepository;
    }

    // GET /api/admin/sales — devolve todas as vendas ordenadas da mais recente para a mais antiga.
    @GetMapping("/sales")
    public List<SaleResponse> findAllSales() {
        return saleService.findAllSales();
    }

    // PATCH /api/admin/sales/{id}/status — atualiza o estado de uma venda específica (ex.: PROCESSING -> SHIPPED).
    // Usa PATCH em vez de PUT porque apenas um campo muda, o que é mais semântico e menos propenso a sobrescrever dados.
    @PatchMapping("/sales/{id}/status")
    public SaleResponse updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateSaleStatusRequest request
    ) {
        return saleService.updateStatus(id, request.status());
    }

    // GET /api/admin/customers — todos os clientes do mais recente para o mais antigo. O passwordHash nunca é incluído na resposta.
    @GetMapping("/customers")
    public List<CustomerResponse> findAllCustomers() {
        return customerRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(c -> new CustomerResponse(c.getId(), c.getName(), c.getEmail(), c.getRole()))
            .toList();
    }
}
