package pt.ubi.gruposd.loja.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.CustomerResponse;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.repository.CustomerRepository;

// Expõe os endpoints administrativos de gestão de clientes, restritos a utilizadores com role ADMIN pela configuração de segurança, e devolve a lista ordenada por data de criação descendente para mostrar primeiro os clientes mais recentes.
@RestController
@RequestMapping("/api/admin/customers")
public class AdminCustomerController {
    private final CustomerRepository customerRepository;

    public AdminCustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getName(), c.getEmail(), c.getRole());
    }
}
