package pt.ubi.gruposd.loja.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.Customer;

// Repositório Spring Data JPA para clientes com findByEmail usado pelo Spring Security ao autenticar, existsByEmailIgnoreCase para validar registos duplicados, e findAllByOrderByCreatedAtDesc para a listagem administrativa.
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Customer> findAllByOrderByCreatedAtDesc();
}
