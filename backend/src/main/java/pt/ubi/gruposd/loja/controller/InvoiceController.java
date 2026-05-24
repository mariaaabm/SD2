package pt.ubi.gruposd.loja.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.InvoiceResponse;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.service.InvoiceService;

// Expõe um endpoint para consultar uma fatura específica pelo id, e o serviço subjacente verifica que a fatura pertence ao cliente autenticado antes de a devolver para impedir que utilizadores vejam faturas de outros.
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public InvoiceResponse findById(
        @AuthenticationPrincipal CustomerUserDetails userDetails,
        @PathVariable Long id
    ) {
        return invoiceService.findCustomerInvoiceById(userDetails.customer(), id);
    }
}
