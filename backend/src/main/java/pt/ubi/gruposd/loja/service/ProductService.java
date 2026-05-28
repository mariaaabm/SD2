package pt.ubi.gruposd.loja.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.PageResponse;
import pt.ubi.gruposd.loja.dto.ProductRequest;
import pt.ubi.gruposd.loja.dto.ProductResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Category;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.repository.ProductRepository;
import pt.ubi.gruposd.loja.repository.ProductSpecifications;

// Gere o CRUD de produtos e a pesquisa paginada com filtros por categoria e por termo livre, e quando a pesquisa exata por LIKE não devolve nada faz fallback para uma pesquisa fuzzy baseada em distância de Levenshtein para tolerar erros de digitação.
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    // Pesquisa paginada com filtros. Se o LIKE não devolver resultados, faz fallback para pesquisa fuzzy (Levenshtein).
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(Long categoryId, Boolean activeOnly, String search, int page, int size) {
        int safeSize = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(Sort.Direction.ASC, "name"));
        Specification<Product> spec = ProductSpecifications.withFilters(categoryId, activeOnly, search);
        Page<Product> exact = productRepository.findAll(spec, pageable);

        // Caminho rapido: ha resultados exatos (LIKE + colacao AI/CI) ou nao ha termo de pesquisa.
        if (!exact.isEmpty() || search == null || search.isBlank()) {
            return PageResponse.of(exact.map(this::toResponse));
        }

        // Fuzzy fallback: encontra produtos parecidos por distancia de Levenshtein.
        return fuzzyFallback(categoryId, activeOnly, search, page, safeSize);
    }

    // Compara o termo com nome e descrição de cada produto por distância de Levenshtein e ordena por relevância.
    // Atenção: carrega todos os produtos em memória — pode ser lento com catálogos muito grandes.
    private PageResponse<ProductResponse> fuzzyFallback(
        Long categoryId, Boolean activeOnly, String search, int page, int size
    ) {
        String query = FuzzyMatcher.normalize(search.trim());
        Specification<Product> baseSpec = ProductSpecifications.withFilters(categoryId, activeOnly, null);

        List<Product> candidates = productRepository.findAll(baseSpec);
        List<ScoredProduct> scored = candidates.stream()
            .map(p -> {
                double score = Math.max(
                    FuzzyMatcher.bestScore(p.getName(), query),
                    FuzzyMatcher.bestScore(p.getDescription(), query)
                );
                return new ScoredProduct(p, score);
            })
            .filter(sp -> sp.score >= FuzzyMatcher.MIN_RATIO)
            .sorted(Comparator.comparingDouble((ScoredProduct sp) -> sp.score).reversed()
                .thenComparing(sp -> sp.product.getName()))
            .toList();

        int total = scored.size();
        int totalPages = total == 0 ? 0 : (int) Math.ceil(total / (double) size);
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<ProductResponse> content = scored.subList(from, to).stream()
            .map(sp -> toResponse(sp.product))
            .toList();

        return new PageResponse<>(
            content, page, size, total, totalPages,
            page == 0, totalPages == 0 || page >= totalPages - 1
        );
    }

    private record ScoredProduct(Product product, double score) {}

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findEntityById(id);
        applyRequest(product, request);
        return toResponse(product);
    }

    // Remove o produto. Falha com erro de constraint se tiver itens de venda associados.
    @Transactional
    public void delete(Long id) {
        productRepository.delete(findEntityById(id));
    }

    // Devolve a entidade JPA diretamente (sem converter para DTO) para uso interno noutros serviços.
    @Transactional(readOnly = true)
    public Product findEntityById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Produto nao encontrado."));
    }

    // Preenche os campos do produto. Se active vier null, fica ativo por defeito.
    private void applyRequest(Product product, ProductRequest request) {
        Category category = categoryService.findEntityById(request.categoryId());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);
        product.setActive(request.active() == null || request.active());
        product.setImageUrl(request.imageUrl());
    }

    // Converte Product para DTO incluindo nome e id da categoria (evita pedido extra ao catálogo).
    private ProductResponse toResponse(Product product) {
        Category category = product.getCategory();
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getActive(),
            category.getId(),
            category.getName(),
            product.getImageUrl()
        );
    }
}
