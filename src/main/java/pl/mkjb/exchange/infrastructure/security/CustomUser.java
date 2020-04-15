package pl.mkjb.exchange.infrastructure.security;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class CustomUser extends User implements UserDetails {
    private final long id;
    private final String fullName;

    @Builder(builderMethodName = "buildCustomUser")
    public CustomUser(long id,
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
