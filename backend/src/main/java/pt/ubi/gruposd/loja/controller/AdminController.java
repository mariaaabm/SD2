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

// Agrupa os endpoints de backoffice em /api/admin — todos protegidos a ROLE_ADMIN na SecurityConfig.
// Permite ao administrador ver todas as vendas, atualizar o estado de uma encomenda e listar clientes.
// Nota de design: a listagem de clientes acede diretamente ao repositório em vez de delegar a um
// serviço porque a lógica é trivial — apenas mapeamento para DTO sem regras de negócio adicionais.
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

    // GET /api/admin/customers — devolve todos os clientes registados ordenados por data de criação descendente.
    // O mapeamento para CustomerResponse aqui garante que o passwordHash nunca é exposto na API.
    @GetMapping("/customers")
    public List<CustomerResponse> findAllCustomers() {
        return customerRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(c -> new CustomerResponse(c.getId(), c.getName(), c.getEmail(), c.getRole()))
            .toList();
    }
}
