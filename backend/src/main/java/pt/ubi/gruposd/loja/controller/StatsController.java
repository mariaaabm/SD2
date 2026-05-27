package pt.ubi.gruposd.loja.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.StatsCustomerResponse;
import pt.ubi.gruposd.loja.dto.StatsProductResponse;
import pt.ubi.gruposd.loja.dto.StatsRevenueResponse;
import pt.ubi.gruposd.loja.service.StatsService;

// Expõe os endpoints de estatísticas usados pelo dashboard de administração, devolve os produtos mais e menos vendidos, os melhores clientes e a receita agregada por período, todos restritos a utilizadores com role ADMIN.
@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // GET /api/stats/products/top-selling — produtos ordenados por unidades vendidas (descendente). Requer ROLE_ADMIN.
    @GetMapping("/products/top-selling")
    public List<StatsProductResponse> topSellingProducts() {
        return statsService.topSellingProducts();
    }

    // GET /api/stats/products/least-selling — produtos ordenados por unidades vendidas (ascendente). Requer ROLE_ADMIN.
    @GetMapping("/products/least-selling")
    public List<StatsProductResponse> leastSellingProducts() {
        return statsService.leastSellingProducts();
    }

    // GET /api/stats/customers/best — clientes ordenados pelo valor total gasto. Requer ROLE_ADMIN.
    @GetMapping("/customers/best")
    public List<StatsCustomerResponse> bestCustomers() {
        return statsService.bestCustomers();
    }

    // GET /api/stats/revenue?period=day|week|month — receita total no período. Requer ROLE_ADMIN.
    @GetMapping("/revenue")
    public StatsRevenueResponse revenue(@RequestParam(defaultValue = "day") String period) {
        return statsService.revenue(period);
    }
}
