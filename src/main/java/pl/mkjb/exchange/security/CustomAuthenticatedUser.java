package pl.mkjb.exchange.security;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@ToString
public class CustomAuthenticatedUser extends User {
    private final long id;
    private final String fullName;

    @Builder(builderMethodName = "buildLoggedUser")
    public CustomAuthenticatedUser(long id,
                                   String username,
                                   String fullName,
                                   String password,
                                   boolean enabled,
                                   boolean accountNonExpired,
                                   boolean credentialsNonExpired,
                                   boolean accountNonLocked,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.fullName = fullName;
    }
}
