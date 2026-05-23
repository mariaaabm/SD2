package pt.ubi.gruposd.loja.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.ProductRatingResponse;
import pt.ubi.gruposd.loja.dto.ReviewRequest;
import pt.ubi.gruposd.loja.dto.ReviewResponse;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.service.ReviewService;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ProductRatingResponse getReviews(@PathVariable Long productId) {
        return reviewService.getProductRatings(productId);
    }

    @PutMapping
    public ReviewResponse upsertReview(
        @PathVariable Long productId,
        @AuthenticationPrincipal CustomerUserDetails user,
        @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.createOrUpdate(user.customer(), productId, request);
    }
}
