package pt.ubi.gruposd.loja.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.ubi.gruposd.loja.dto.RegisterRequest;
import pt.ubi.gruposd.loja.service.AuthService.LoginResult;
import pt.ubi.gruposd.loja.dto.UpdateProfileRequest;
import pt.ubi.gruposd.loja.exception.ConflictException;
import pt.ubi.gruposd.loja.exception.UnauthorizedException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.UserRole;
import pt.ubi.gruposd.loja.repository.CustomerRepository;
import pt.ubi.gruposd.loja.security.JwtService;

// Testa o AuthService isoladamente com Mockito a simular o repositório, o PasswordEncoder, o AuthenticationManager e o JwtService, cobrindo o registo de novos clientes, a deteção de emails duplicados, a normalização do email para minúsculas e os vários caminhos de updateProfile incluindo a verificação da password atual.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService authService;
    private Customer customer;

    // Cria antes de cada teste uma instância nova do AuthService com os mocks injetados e um cliente exemplo já preenchido para evitar repetição de boilerplate nos testes individuais.
    @BeforeEach
    void setUp() {
        authService = new AuthService(customerRepository, passwordEncoder, authenticationManager, jwtService);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("João Silva");
        customer.setEmail("joao@teste.pt");
        customer.setPasswordHash("$2a$hashed");
        customer.setRole(UserRole.CLIENT);
    }

    // Verifica que um registo válido encripta a password, grava o cliente novo e devolve já o JWT pronto a usar para o utilizador não ter de fazer login a seguir ao registo.
    @Test
    void register_savesNewCustomer_andReturnsToken() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@teste.pt", "password123");
        when(customerRepository.existsByEmailIgnoreCase("joao@teste.pt")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(jwtService.generateToken(customer)).thenReturn("jwt-token-abc");

        LoginResult result = authService.register(request);

        assertThat(result.auth().token()).isEqualTo("jwt-token-abc");
        assertThat(result.auth().customer().email()).isEqualTo("joao@teste.pt");
        assertThat(result.auth().customer().role()).isEqualTo(UserRole.CLIENT);
    }

    // Garante que tentar registar com um email já existente lança ConflictException em vez de criar uma conta duplicada na base de dados.
    @Test
    void register_throwsConflict_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("João Silva", "joao@teste.pt", "password123");
        when(customerRepository.existsByEmailIgnoreCase("joao@teste.pt")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(ConflictException.class);
    }

    // Confirma que o email é guardado sempre em minúsculas mesmo quando o utilizador escreve em maiúsculas, para garantir consistência nas pesquisas case-insensitive e evitar contas duplicadas com casing diferente.
    @Test
    void register_lowercasesEmail() {
        RegisterRequest request = new RegisterRequest("Maria", "MARIA@TESTE.PT", "pass");
        when(customerRepository.existsByEmailIgnoreCase("MARIA@TESTE.PT")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hash");
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });
        when(jwtService.generateToken(any(Customer.class))).thenReturn("token");

        authService.register(request);

        org.mockito.ArgumentCaptor<Customer> captor = org.mockito.ArgumentCaptor.forClass(Customer.class);
        org.mockito.Mockito.verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("maria@teste.pt");
    }

    // Verifica que enviar apenas o campo nome atualiza o nome do cliente sem mexer nos restantes campos.
    @Test
    void updateProfile_updatesName_whenProvided() {
        UpdateProfileRequest request = new UpdateProfileRequest("Novo Nome", null, null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        var result = authService.updateProfile(customer, request);

        assertThat(result.name()).isEqualTo("Novo Nome");
    }

    // Garante que a mudança de password é recusada com UnauthorizedException quando a password atual indicada não corresponde ao hash guardado, protegendo contra tokens roubados que tentem alterar credenciais.
    @Test
    void updateProfile_throwsUnauthorized_whenCurrentPasswordWrong() {
        UpdateProfileRequest request = new UpdateProfileRequest(null, "wrongpass", "newpass");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpass", "$2a$hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.updateProfile(customer, request))
            .isInstanceOf(UnauthorizedException.class);
    }

    // Confirma que se a password atual for válida a password nova é encriptada e gravada no cliente, substituindo o hash antigo.
    @Test
    void updateProfile_changesPassword_whenCurrentPasswordCorrect() {
        UpdateProfileRequest request = new UpdateProfileRequest(null, "oldpass", "newpass");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("oldpass", "$2a$hashed")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("$2a$newhash");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        authService.updateProfile(customer, request);

        assertThat(customer.getPasswordHash()).isEqualTo("$2a$newhash");
    }
}
