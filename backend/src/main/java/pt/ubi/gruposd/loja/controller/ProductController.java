package pt.ubi.gruposd.loja.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.PageResponse;
import pt.ubi.gruposd.loja.dto.ProductRequest;
import pt.ubi.gruposd.loja.dto.ProductResponse;
import pt.ubi.gruposd.loja.service.ProductService;

// Expõe os endpoints do catálogo de produtos com pesquisa, filtro por categoria e paginação para o frontend, e ainda os endpoints de criação, atualização e remoção restritos a administradores pela configuração de segurança.
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products — listagem paginada do catálogo; disponível sem autenticação (SecurityConfig).
    // activeOnly=false por defeito para que a área de admin veja também produtos inativos.
    // O parâmetro search ativa a pesquisa exata por LIKE com fallback fuzzy no ProductService.
    @GetMapping
    public PageResponse<ProductResponse> findAll(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(defaultValue = "false") Boolean activeOnly,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return productService.findAll(categoryId, activeOnly, search, page, size);
    }

    // GET /api/products/{id} — detalhe de um produto específico; disponível sem autenticação.
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    // POST /api/products — cria produto e devolve 201 Created com header Location apontando para o novo recurso.
    // Requer ROLE_ADMIN (SecurityConfig).
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.create(request);
        return ResponseEntity
            .created(URI.create("/api/products/" + response.id()))
            .body(response);
    }

    // PUT /api/products/{id} — substitui completamente os dados do produto. Requer ROLE_ADMIN.
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    // DELETE /api/products/{id} — remove o produto e devolve 204 No Content. Requer ROLE_ADMIN.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
