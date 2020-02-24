package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.repository.UserRepository;
import pl.mkjb.exchange.security.LoggedUser;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> LoggedUser.buildLoggedUser()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .password(user.getPassword())
                        .enabled(user.getActive())
                        .accountNonExpired(true)
                        .credentialsNonExpired(true)
                        .accountNonLocked(true)
                        .authorities(Set.of())
                        .build())
                .orElseThrow(() -> {
                    log.error("Given username not found: {}", username);
                    throw new UsernameNotFoundException("Given username not found: " + username);
                });
    }
}
