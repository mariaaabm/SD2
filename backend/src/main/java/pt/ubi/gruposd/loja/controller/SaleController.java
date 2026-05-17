package pt.ubi.gruposd.loja.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ubi.gruposd.loja.dto.CheckoutRequest;
import pt.ubi.gruposd.loja.dto.InvoiceResponse;
import pt.ubi.gruposd.loja.dto.SaleResponse;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.service.InvoiceService;
import pt.ubi.gruposd.loja.service.SaleService;

@RestController
@RequestMapping("/api/sales")
public class SaleController {
    private final SaleService saleService;
    private final InvoiceService invoiceService;

    public SaleController(SaleService saleService, InvoiceService invoiceService) {
        this.saleService = saleService;
        this.invoiceService = invoiceService;
    }

    @PostMapping("/checkout")
    public SaleResponse checkout(
        @AuthenticationPrincipal CustomerUserDetails userDetails,
        @Valid @RequestBody CheckoutRequest request
    ) {
        return saleService.checkout(userDetails.customer(), request);
    }

    @GetMapping
    public List<SaleResponse> findMySales(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        return saleService.findCustomerSales(userDetails.customer());
    }

    @GetMapping("/{id}")
    public SaleResponse findMySaleById(
        @AuthenticationPrincipal CustomerUserDetails userDetails,
        @PathVariable Long id
    ) {
        return saleService.findCustomerSaleById(userDetails.customer(), id);
    }

    @GetMapping("/{id}/invoice")
    public InvoiceResponse findMySaleInvoice(
        @AuthenticationPrincipal CustomerUserDetails userDetails,
        @PathVariable Long id
    ) {
        return invoiceService.findCustomerInvoiceBySaleId(userDetails.customer(), id);
    }
}
