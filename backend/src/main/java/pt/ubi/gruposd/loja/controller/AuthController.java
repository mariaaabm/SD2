package pt.ubi.gruposd.loja.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ubi.gruposd.loja.dto.AuthResponse;
import pt.ubi.gruposd.loja.dto.CustomerResponse;
import pt.ubi.gruposd.loja.dto.LoginRequest;
import pt.ubi.gruposd.loja.dto.RegisterRequest;
import pt.ubi.gruposd.loja.dto.UpdateProfileRequest;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Value("${app.security.jwt-expiration-minutes:120}")
    private long jwtExpirationMinutes;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(
        @Valid @RequestBody RegisterRequest request,
        HttpServletResponse response
    ) {
        AuthResponse auth = authService.register(request);
        setJwtCookie(response, auth.token());
        return auth;
    }

    @PostMapping("/login")
    public AuthResponse login(
        @Valid @RequestBody LoginRequest request,
        HttpServletResponse response
    ) {
        AuthResponse auth = authService.login(request);
        setJwtCookie(response, auth.token());
        return auth;
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        clearJwtCookie(response);
    }

    @GetMapping("/me")
    public CustomerResponse me(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        return authService.me(userDetails);
    }

    @PatchMapping("/profile")
    public CustomerResponse updateProfile(
        @AuthenticationPrincipal CustomerUserDetails userDetails,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        return authService.updateProfile(userDetails.customer(), request);
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(jwtExpirationMinutes * 60)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
            .httpOnly(true)
            .sameSite("Strict")
            .path("/")
            .maxAge(0)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
