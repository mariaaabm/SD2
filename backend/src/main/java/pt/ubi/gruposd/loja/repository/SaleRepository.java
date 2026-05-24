package pt.ubi.gruposd.loja.repository;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.ubi.gruposd.loja.model.Sale;

// Repositório Spring Data JPA para vendas com queries derivadas para listagens ordenadas por data, e queries JPQL agregadas para o ranking de melhores clientes e para a receita acumulada num intervalo de datas usadas pelo StatsService.
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findAllByOrderByCreatedAtDesc();

    List<Sale> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Query("""
        select c.id, c.name, count(s.id), coalesce(sum(s.total), 0)
        from Sale s
        join s.customer c
        group by c.id, c.name
        order by coalesce(sum(s.total), 0) desc
        """)
    List<Object[]> findBestCustomers();

    @Query("""
        select coalesce(sum(s.total), 0)
        from Sale s
        where s.createdAt >= :start and s.createdAt < :end
        """)
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
