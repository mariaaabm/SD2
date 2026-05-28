package pt.ubi.gruposd.loja.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.ubi.gruposd.loja.repository.CustomerRepository;

// Carrega o utilizador da base de dados pelo email para o Spring Security poder autenticá-lo.
// Usado automaticamente pelo AuthenticationManager durante o login.
@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomerUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByEmail(username)
            .map(CustomerUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador nao encontrado."));
    }
}
