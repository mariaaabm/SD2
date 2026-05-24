package pt.ubi.gruposd.loja.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.StatsCustomerResponse;
import pt.ubi.gruposd.loja.dto.StatsProductResponse;
import pt.ubi.gruposd.loja.dto.StatsRevenueResponse;
import pt.ubi.gruposd.loja.repository.SaleItemRepository;
import pt.ubi.gruposd.loja.repository.SaleRepository;

// Produz as estatísticas mostradas no dashboard de administração, incluindo os produtos mais e menos vendidos, os melhores clientes por volume de compras e a receita agregada por dia, semana ou mês usando consultas SQL agregadas no repositório.
@Service
public class StatsService {
    private final SaleItemRepository saleItemRepository;
    private final SaleRepository saleRepository;

    public StatsService(SaleItemRepository saleItemRepository, SaleRepository saleRepository) {
        this.saleItemRepository = saleItemRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional(readOnly = true)
    public List<StatsProductResponse> topSellingProducts() {
        return saleItemRepository.findTopSellingProducts()
            .stream()
            .map(this::toProductStats)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StatsProductResponse> leastSellingProducts() {
        return saleItemRepository.findLeastSellingProducts()
            .stream()
            .map(this::toProductStats)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StatsCustomerResponse> bestCustomers() {
        return saleRepository.findBestCustomers()
            .stream()
            .map(row -> new StatsCustomerResponse(
                (Long) row[0],
                (String) row[1],
                (Long) row[2],
                (BigDecimal) row[3]
            ))
            .toList();
    }

    // Calcula a receita total dentro do período indicado, traduz os parâmetros day, week ou month para intervalos concretos de datas a começar na segunda-feira ou no primeiro dia do mês, e devolve o intervalo usado para o frontend conseguir mostrá-lo ao utilizador.
    @Transactional(readOnly = true)
    public StatsRevenueResponse revenue(String period) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end;

        switch (period == null ? "day" : period) {
            case "week" -> {
                start = today.with(DayOfWeek.MONDAY);
                end = start.plusWeeks(1);
            }
            case "month" -> {
                start = today.withDayOfMonth(1);
                end = start.plusMonths(1);
            }
            case "day" -> {
                start = today;
                end = today.plusDays(1);
            }
            default -> throw new IllegalArgumentException("Periodo invalido. Usa day, week ou month.");
        }

        BigDecimal revenue = saleRepository.sumRevenueBetween(start.atStartOfDay(), end.atStartOfDay());
        return new StatsRevenueResponse(start, end.minusDays(1), revenue);
    }

    private StatsProductResponse toProductStats(Object[] row) {
        return new StatsProductResponse(
            (Long) row[0],
            (String) row[1],
            (Long) row[2]
        );
    }
}
