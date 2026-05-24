package pt.ubi.gruposd.loja.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.SaleResponse;
import pt.ubi.gruposd.loja.dto.UpdateSaleStatusRequest;
import pt.ubi.gruposd.loja.service.SaleService;

// Expõe os endpoints administrativos sobre vendas, permite listar todas as vendas existentes na loja e mudar o estado de uma encomenda específica para acompanhar o ciclo desde CONFIRMED até DELIVERED ou CANCELLED.
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

    @PatchMapping("/{id}/status")
    public SaleResponse updateStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateSaleStatusRequest request
    ) {
        return saleService.updateStatus(id, request.status());
    }
}
