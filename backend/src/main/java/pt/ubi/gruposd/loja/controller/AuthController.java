package pt.ubi.gruposd.loja.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
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
import pt.ubi.gruposd.loja.exception.UnauthorizedException;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.security.JwtService;
import pt.ubi.gruposd.loja.service.AuthService;
import pt.ubi.gruposd.loja.service.AuthService.LoginResult;
import pt.ubi.gruposd.loja.service.RefreshTokenService;
import pt.ubi.gruposd.loja.service.RefreshTokenService.RefreshResult;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @Value("${app.security.jwt-expiration-minutes:15}")
    private long jwtExpirationMinutes;

    @Value("${app.security.refresh-expiration-days:7}")
    private long refreshExpirationDays;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(
        @Valid @RequestBody RegisterRequest request,
        HttpServletResponse response
    ) {
        LoginResult result = authService.register(request);
        String refreshToken = refreshTokenService.createForCustomer(result.customer());
        setJwtCookie(response, result.auth().token());
        setRefreshCookie(response, refreshToken);
        return result.auth();
    }

    @PostMapping("/login")
    public AuthResponse login(
        @Valid @RequestBody LoginRequest request,
        HttpServletResponse response
    ) {
        LoginResult result = authService.login(request);
        String refreshToken = refreshTokenService.createForCustomer(result.customer());
        setJwtCookie(response, result.auth().token());
        setRefreshCookie(response, refreshToken);
        return result.auth();
    }

    @PostMapping("/refresh")
    public CustomerResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String token = extractCookie(request, "refresh_token");
        if (token == null) throw new UnauthorizedException("Sem token de refresh.");

        RefreshResult result = refreshTokenService.validateAndRotate(token);
        String newJwt = jwtService.generateToken(result.customer());

        setJwtCookie(response, newJwt);
        setRefreshCookie(response, result.newRefreshToken());

        return new CustomerResponse(
            result.customer().getId(),
            result.customer().getName(),
            result.customer().getEmail(),
            result.customer().getRole()
        );
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractCookie(request, "refresh_token");
        if (token != null) {
            refreshTokenService.deleteByToken(token);
        }
        clearJwtCookie(response);
        clearRefreshCookie(response);
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

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
            .filter(c -> name.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
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

    private void setRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
            .httpOnly(true)
            .sameSite("Strict")
            .path("/api/auth")
            .maxAge(refreshExpirationDays * 24 * 60 * 60)
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

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .sameSite("Strict")
            .path("/api/auth")
            .maxAge(0)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
