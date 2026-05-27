package pt.ubi.gruposd.loja.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.ProductRatingResponse;
import pt.ubi.gruposd.loja.dto.ReviewRequest;
import pt.ubi.gruposd.loja.dto.ReviewResponse;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.Product;
import pt.ubi.gruposd.loja.model.Review;
import pt.ubi.gruposd.loja.repository.ReviewRepository;

// Gere as avaliações e comentários dos clientes a produtos, calcula a média de estrelas e o número total de reviews por produto, e garante que cada cliente só tem uma review por produto fazendo update em vez de inserir uma segunda.
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
    }

    // Agrega média e contagem separadamente da lista de reviews completa.
    // Math.round(avg * 10.0) / 10.0 arredonda a 1 casa decimal para mostrar "4.3 estrelas" em vez de "4.33...".
    // Quando não há reviews, avg é null (SQL AVG de conjunto vazio) e é convertido para 0.0.
    @Transactional(readOnly = true)
    public ProductRatingResponse getProductRatings(Long productId) {
        List<ReviewResponse> reviews = reviewRepository
            .findByProductIdOrderByCreatedAtDesc(productId)
            .stream()
            .map(this::toResponse)
            .toList();

        Double avg = reviewRepository.avgRatingByProductId(productId);
        long count = reviewRepository.countByProductId(productId);

        return new ProductRatingResponse(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0, count, reviews);
    }

    // Cria uma review nova ou atualiza a existente do mesmo cliente para o mesmo produto, evitando duplicados e permitindo que o cliente reveja a sua opinião sem ter de apagar e reescrever.
    @Transactional
    public ReviewResponse createOrUpdate(Customer customer, Long productId, ReviewRequest request) {
        Product product = productService.findEntityById(productId);

        Review review = reviewRepository
            .findByCustomerIdAndProductId(customer.getId(), productId)
            .orElseGet(Review::new);

        review.setCustomer(customer);
        review.setProduct(product);
        review.setRating(request.rating());
        review.setComment(request.comment());

        return toResponse(reviewRepository.save(review));
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
            r.getId(),
            r.getCustomer().getId(),
            r.getCustomer().getName(),
            r.getProduct().getId(),
            r.getRating(),
            r.getComment(),
            r.getCreatedAt()
        );
    }
}
