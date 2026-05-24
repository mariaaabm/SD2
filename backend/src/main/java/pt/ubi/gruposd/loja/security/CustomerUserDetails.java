package pt.ubi.gruposd.loja.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ubi.gruposd.loja.model.Customer;

// Adapta a entidade Customer ao contrato UserDetails que o Spring Security espera, mapeia o role do utilizador para uma autoridade com prefixo ROLE_ usada nas regras hasRole da configuração de segurança, e expõe o Customer original aos controllers via método customer().
public class CustomerUserDetails implements UserDetails {
    private final Customer customer;

    public CustomerUserDetails(Customer customer) {
        this.customer = customer;
    }

    public Customer customer() {
        return customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()));
    }

    @Override
    public String getPassword() {
        return customer.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }
}
