package pt.ubi.gruposd.loja.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.SaleResponse;
import pt.ubi.gruposd.loja.service.SaleService;

@RestController
@RequestMapping("/api/admin/sales")
public class AdminSaleController {
    private final SaleService saleService;

    public AdminSaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<SaleResponse> findAll() {
        return saleService.findAllSales();
    }
}
