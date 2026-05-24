package pt.ubi.gruposd.loja.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ubi.gruposd.loja.model.SaleItem;

// Repositório Spring Data JPA para linhas de venda com query derivada por sale_id e queries agregadas que devolvem produtos ordenados por unidades vendidas, usadas pelo StatsService para o ranking de mais e menos vendidos no dashboard.
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySaleId(Long saleId);

    @Query("""
        select p.id, p.name, coalesce(sum(si.quantity), 0)
        from Product p
        left join SaleItem si on si.product = p
        group by p.id, p.name
        order by coalesce(sum(si.quantity), 0) desc
        """)
    List<Object[]> findTopSellingProducts();

    @Query("""
        select p.id, p.name, coalesce(sum(si.quantity), 0)
        from Product p
        left join SaleItem si on si.product = p
        group by p.id, p.name
        order by coalesce(sum(si.quantity), 0) asc
        """)
    List<Object[]> findLeastSellingProducts();
}
