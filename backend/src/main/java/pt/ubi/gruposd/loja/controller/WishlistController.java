package pt.ubi.gruposd.loja.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.WishlistResponse;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.service.WishlistService;

// Expõe os endpoints da wishlist do cliente autenticado, devolve a lista completa ou apenas os ids dos produtos quando o frontend só precisa de marcar quais já estão favoritados nas cards, e suporta adicionar e remover por productId.
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public List<WishlistResponse> getWishlist(@AuthenticationPrincipal CustomerUserDetails user) {
        return wishlistService.findByCustomer(user.customer().getId());
    }

    @GetMapping("/ids")
    public List<Long> getWishlistIds(@AuthenticationPrincipal CustomerUserDetails user) {
        return wishlistService.findProductIdsByCustomer(user.customer().getId());
    }

    @PostMapping("/{productId}")
    public WishlistResponse add(
        @AuthenticationPrincipal CustomerUserDetails user,
        @PathVariable Long productId
    ) {
        return wishlistService.add(user.customer(), productId);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
        @AuthenticationPrincipal CustomerUserDetails user,
        @PathVariable Long productId
    ) {
        wishlistService.remove(user.customer(), productId);
        return ResponseEntity.noContent().build();
    }
}
