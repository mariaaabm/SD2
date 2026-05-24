package pt.ubi.gruposd.loja.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.dto.AuthResponse;
import pt.ubi.gruposd.loja.dto.CustomerResponse;
import pt.ubi.gruposd.loja.dto.LoginRequest;
import pt.ubi.gruposd.loja.dto.RegisterRequest;
import pt.ubi.gruposd.loja.dto.UpdateProfileRequest;
import pt.ubi.gruposd.loja.exception.ConflictException;
import pt.ubi.gruposd.loja.exception.UnauthorizedException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.UserRole;
import pt.ubi.gruposd.loja.repository.CustomerRepository;
import pt.ubi.gruposd.loja.security.CustomerUserDetails;
import pt.ubi.gruposd.loja.security.JwtService;

// Gere o registo e autenticação dos clientes, encripta as passwords com BCrypt através do PasswordEncoder, valida credenciais via AuthenticationManager e devolve o JWT que o frontend usa para chamar a API protegida.
@Service
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Regista um novo cliente garantindo que o email ainda não existe, normaliza o email para minúsculas, encripta a password e devolve já um token JWT pronto para o frontend usar.
    @Transactional
    public LoginResult register(RegisterRequest request) {
        if (customerRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ja existe uma conta com esse email.");
        }

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email().toLowerCase());
        customer.setPasswordHash(passwordEncoder.encode(request.password()));
        customer.setRole(UserRole.CLIENT);

        Customer saved = customerRepository.save(customer);
        AuthResponse auth = new AuthResponse(jwtService.generateToken(saved), toResponse(saved));
        return new LoginResult(auth, saved);
    }

    // Autentica o cliente delegando ao AuthenticationManager do Spring Security, e quando as credenciais batem certo emite um JWT novo e devolve também o objeto Customer carregado da base de dados.
    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email().toLowerCase(), request.password())
        );

        Customer customer = ((CustomerUserDetails) authentication.getPrincipal()).customer();
        AuthResponse auth = new AuthResponse(jwtService.generateToken(customer), toResponse(customer));
        return new LoginResult(auth, customer);
    }

    public CustomerResponse me(CustomerUserDetails userDetails) {
        return toResponse(userDetails.customer());
    }

    // Atualiza o perfil do cliente autenticado, permite mudar o nome e opcionalmente a password, e exige a password atual correta antes de aceitar uma nova password para evitar que tokens roubados consigam alterar credenciais.
    @Transactional
    public CustomerResponse updateProfile(Customer customer, UpdateProfileRequest request) {
        Customer managed = customerRepository.findById(customer.getId())
            .orElseThrow(() -> new UnauthorizedException("Utilizador nao encontrado."));

        if (request.name() != null && !request.name().isBlank()) {
            managed.setName(request.name().trim());
        }

        if (request.newPassword() != null && !request.newPassword().isBlank()) {
            if (request.currentPassword() == null ||
                !passwordEncoder.matches(request.currentPassword(), managed.getPasswordHash())) {
                throw new UnauthorizedException("Password atual incorreta.");
            }
            managed.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }

        return toResponse(customerRepository.save(managed));
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getRole()
        );
    }

    public record LoginResult(AuthResponse auth, Customer customer) {}
}
