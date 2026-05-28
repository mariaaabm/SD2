package pt.ubi.gruposd.loja.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pt.ubi.gruposd.loja.model.Customer;

// Adapta a entidade Customer ao formato que o Spring Security espera (UserDetails).
// Converte o role (ex.: ADMIN) para "ROLE_ADMIN" e expõe o Customer via customer().
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
