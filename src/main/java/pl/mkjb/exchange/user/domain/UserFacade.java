package pl.mkjb.exchange.user.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.infrastructure.mvc.exception.BadResourceException;
import pl.mkjb.exchange.infrastructure.util.RoleConstant;
import pl.mkjb.exchange.user.dto.UserDto;

import java.util.Set;

import static pl.mkjb.exchange.infrastructure.util.RoleConstant.ROLE_OWNER;
import static pl.mkjb.exchange.infrastructure.util.RoleConstant.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isUserNameAlreadyUsed(UserDto userDto) {
        return userRepository.findByUsername(userDto.getUserName())
                .map(userEntity -> userDto.getId() != userEntity.getId())
                .getOrElse(false);
    }

    @Transactional
    public UserEntity save(UserDto userDto) {
        val roleEntity = findRoleByName(ROLE_USER);
        val userEntity = UserEntity.fromModel(userDto);
        userEntity.setRoles(Set.of(roleEntity));
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(userEntity);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .getOrElseThrow(() -> {
                    log.error("Given username not found: {}", username);
                    throw new UsernameNotFoundException("Given username not found: " + username);
                });
    }

    public UserEntity findOwner() {
        val roleEntity = findRoleByName(ROLE_OWNER);
        return userRepository.findByRolesContaining(roleEntity)
                .getOrElseThrow(() -> {
                    log.error("User with ROLE_OWNER not found");
                    throw new BadResourceException("User with ROLE_OWNER not found");
                });
    }

    private RoleEntity findRoleByName(RoleConstant roleConstant) {
        return roleRepository.findByRole(roleConstant.name())
                .getOrElseThrow(() -> {
                    log.error("User with {} not found", roleConstant);
                    throw new BadResourceException("User with {} not found" + roleConstant);
                });
    }
}
