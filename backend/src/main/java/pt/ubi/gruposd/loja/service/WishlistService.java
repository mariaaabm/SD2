package pt.ubi.gruposd.loja.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.WishlistResponse;
import pt.ubi.gruposd.loja.exception.NotFoundException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.model.WishlistItem;
import pt.ubi.gruposd.loja.repository.WishlistRepository;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;

    public WishlistService(WishlistRepository wishlistRepository, ProductService productService) {
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<WishlistResponse> findByCustomer(Long customerId) {
        return wishlistRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> findProductIdsByCustomer(Long customerId) {
        return wishlistRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
            .stream()
            .map(w -> w.getProduct().getId())
            .toList();
    }

    @Transactional
    public WishlistResponse add(Customer customer, Long productId) {
        if (wishlistRepository.existsByCustomerIdAndProductId(customer.getId(), productId)) {
            return wishlistRepository
                .findByCustomerIdAndProductId(customer.getId(), productId)
                .map(this::toResponse)
                .orElseThrow();
        }
        Product product = productService.findEntityById(productId);
        WishlistItem item = new WishlistItem();
        item.setCustomer(customer);
        item.setProduct(product);
        return toResponse(wishlistRepository.save(item));
    }

    @Transactional
    public void remove(Customer customer, Long productId) {
        if (!wishlistRepository.existsByCustomerIdAndProductId(customer.getId(), productId)) {
            throw new NotFoundException("Produto não está na wishlist.");
        }
        wishlistRepository.deleteByCustomerIdAndProductId(customer.getId(), productId);
    }

    @Transactional(readOnly = true)
    public boolean isWishlisted(Long customerId, Long productId) {
        return wishlistRepository.existsByCustomerIdAndProductId(customerId, productId);
    }

    private WishlistResponse toResponse(WishlistItem item) {
        Product p = item.getProduct();
        return new WishlistResponse(
            item.getId(),
            p.getId(),
            p.getName(),
            p.getDescription(),
            p.getPrice().doubleValue(),
            p.getStock(),
            p.getActive(),
            p.getCategory().getName(),
            p.getImageUrl(),
            item.getCreatedAt()
        );
    }
}
