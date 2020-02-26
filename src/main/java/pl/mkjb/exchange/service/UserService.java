package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.RoleEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.UserModel;
import pl.mkjb.exchange.repository.RoleRepository;
import pl.mkjb.exchange.repository.UserRepository;
import pl.mkjb.exchange.util.Role;

import java.util.Set;

import static pl.mkjb.exchange.util.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isGivenUserNameAlreadyUsed(UserModel userModel) {
        return userRepository.findByUsername(userModel.getUserName())
                .map(userEntity -> userModel.getId() != userEntity.getId())
                .orElse(false);
    }

    @Transactional
    public void save(UserModel userModel) {
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        val roleEntity = findRoleByName(ROLE_USER);
        val userEntity = UserEntity.fromModel(userModel);
        userEntity.setRoles(Set.of(roleEntity));
        userRepository.save(userEntity);
    }

    public RoleEntity findRoleByName(Role role) {
        return roleRepository.findByRole(role.name())
                .getOrElseThrow(() -> new BadResourceException("Given role name doesn't exist" + role));
    }

    public Set<UserEntity> findUsersByRole(RoleEntity roleEntity) {
        return userRepository.findByRolesContaining(roleEntity);
    }
}
