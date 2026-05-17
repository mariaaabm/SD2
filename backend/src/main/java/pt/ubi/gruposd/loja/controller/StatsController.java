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

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/products/top-selling")
    public List<StatsProductResponse> topSellingProducts() {
        return statsService.topSellingProducts();
    }

    @GetMapping("/products/least-selling")
    public List<StatsProductResponse> leastSellingProducts() {
        return statsService.leastSellingProducts();
    }

    @GetMapping("/customers/best")
    public List<StatsCustomerResponse> bestCustomers() {
        return statsService.bestCustomers();
    }

    @GetMapping("/revenue")
    public StatsRevenueResponse revenue(@RequestParam(defaultValue = "day") String period) {
        return statsService.revenue(period);
    }
}
