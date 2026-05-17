package pt.ubi.gruposd.loja.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findBySaleId(Long saleId);
}
