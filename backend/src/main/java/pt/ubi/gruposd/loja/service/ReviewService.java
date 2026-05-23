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

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
    }

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

    @Transactional(readOnly = true)
    public boolean hasReviewed(Long customerId, Long productId) {
        return reviewRepository.existsByCustomerIdAndProductId(customerId, productId);
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
