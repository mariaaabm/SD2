package pt.ubi.gruposd.loja.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.Invoice;

// Repositório Spring Data JPA para faturas com findBySaleId que tira partido da relação one-to-one entre venda e fatura para encontrar a fatura associada a partir do id da venda.
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findBySaleId(Long saleId);
}
