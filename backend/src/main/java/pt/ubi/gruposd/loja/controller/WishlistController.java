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

// Endpoints da lista de favoritos do cliente autenticado em /api/wishlist.
// Suporta listar, adicionar e remover produtos favoritos.
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // GET /api/wishlist — lista completa de produtos favoritos com todos os detalhes para a WishlistPage.
    @GetMapping
    public List<WishlistResponse> getWishlist(@AuthenticationPrincipal CustomerUserDetails user) {
        return wishlistService.findByCustomer(user.customer().getId());
    }

    // GET /api/wishlist/ids — endpoint leve que devolve apenas os IDs dos produtos favoritos.
    // Usado pelo WishlistContext ao arrancar para marcar os corações nas cards do catálogo
    // sem carregar os detalhes completos de cada produto favoritado.
    @GetMapping("/ids")
    public List<Long> getWishlistIds(@AuthenticationPrincipal CustomerUserDetails user) {
        return wishlistService.findProductIdsByCustomer(user.customer().getId());
    }

    // POST /api/wishlist/{productId} — adiciona o produto à wishlist; idempotente (não cria duplicados).
    @PostMapping("/{productId}")
    public WishlistResponse add(
        @AuthenticationPrincipal CustomerUserDetails user,
        @PathVariable Long productId
    ) {
        return wishlistService.add(user.customer(), productId);
    }

    // DELETE /api/wishlist/{productId} — remove o produto da wishlist e devolve 204 No Content.
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(
        @AuthenticationPrincipal CustomerUserDetails user,
        @PathVariable Long productId
    ) {
        wishlistService.remove(user.customer(), productId);
        return ResponseEntity.noContent().build();
    }
}
