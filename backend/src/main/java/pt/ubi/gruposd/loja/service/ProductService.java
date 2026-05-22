package pt.ubi.gruposd.loja.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.ProductRequest;
import pt.ubi.gruposd.loja.dto.ProductResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Category;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(Long categoryId, Boolean activeOnly, String search) {
        List<Product> products;
        boolean hasSearch = search != null && !search.isBlank();

        if (hasSearch && categoryId != null) {
            products = productRepository.searchByCategoryActive(categoryId, search.trim());
        } else if (hasSearch && Boolean.TRUE.equals(activeOnly)) {
            products = productRepository.searchActive(search.trim());
        } else if (hasSearch) {
            products = productRepository.search(search.trim());
        } else if (categoryId != null && Boolean.TRUE.equals(activeOnly)) {
            products = productRepository.findByCategoryIdAndActiveTrue(categoryId);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryId(categoryId);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            products = productRepository.findByActiveTrue();
        } else {
            products = productRepository.findAll();
        }

        return products.stream().map(this::toResponse).toList();
    }

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

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public Product findEntityById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Produto nao encontrado."));
    }

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
