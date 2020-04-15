package pl.mkjb.exchange.user.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.infrastructure.security.CustomUser;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserFacade userFacade;

    @Override
    public CustomUser loadUserByUsername(String username) {
        val userEntity = userFacade.findByUsername(username);
        return CustomUser.buildCustomUser()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .fullName(userEntity.getFirstName() + " " + userEntity.getLastName())
                .password(userEntity.getPassword())
                .enabled(userEntity.getActive())
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .authorities(getUserAuthority(userEntity.getRoles()))
                .build();
    }

    private Set<GrantedAuthority> getUserAuthority(Set<RoleEntity> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toUnmodifiableSet());
    }
}