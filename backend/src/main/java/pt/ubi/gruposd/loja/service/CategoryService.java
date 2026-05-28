package pt.ubi.gruposd.loja.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.CategoryRequest;
import pt.ubi.gruposd.loja.dto.CategoryResponse;
import pt.ubi.gruposd.loja.exception.ConflictException;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Category;
import pt.ubi.gruposd.loja.repository.CategoryRepository;
import pt.ubi.gruposd.loja.repository.ProductRepository;

// Trata do CRUD de categorias de produtos, valida que não existem nomes duplicados e bloqueia a remoção de categorias que ainda têm produtos associados para preservar a integridade referencial da base de dados.
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Ja existe uma categoria com esse nome.");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findEntityById(id);

        // Só verifica duplicados se o nome mudou (evitar conflito falso ao guardar sem alterar o nome).
        if (!category.getName().equalsIgnoreCase(request.name())
            && categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new ConflictException("Ja existe uma categoria com esse nome.");
        }

        category.setName(request.name());
        category.setDescription(request.description());

        // Não precisa de save() — o Hibernate deteta as alterações automaticamente (entidade gerida pelo JPA).
        return toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findEntityById(id);

        if (productRepository.existsByCategoryId(id)) {
            throw new ConflictException("Nao e possivel remover uma categoria com produtos associados.");
        }

        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public Category findEntityById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Categoria nao encontrada."));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription()
        );
    }
}
