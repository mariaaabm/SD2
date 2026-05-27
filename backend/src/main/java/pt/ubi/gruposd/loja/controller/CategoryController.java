package pt.ubi.gruposd.loja.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import pt.ubi.gruposd.loja.dto.CategoryRequest;
import pt.ubi.gruposd.loja.dto.CategoryResponse;
import pt.ubi.gruposd.loja.service.CategoryService;

// Expõe o CRUD público de categorias de produtos para o frontend popular o menu de navegação e para a área de administração gerir o catálogo, e devolve 201 Created com Location no header quando uma categoria é adicionada com sucesso.
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories — lista todas as categorias; público para o menu e o catálogo.
    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll();
    }

    // GET /api/categories/{id} — detalhe de uma categoria específica; público.
    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    // POST /api/categories — cria uma categoria e devolve 201 Created. Requer ROLE_ADMIN.
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity
            .created(URI.create("/api/categories/" + response.id()))
            .body(response);
    }

    // PUT /api/categories/{id} — atualiza uma categoria. Requer ROLE_ADMIN.
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    // DELETE /api/categories/{id} — remove uma categoria e devolve 204. Falha com 409 se tiver produtos. Requer ROLE_ADMIN.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
