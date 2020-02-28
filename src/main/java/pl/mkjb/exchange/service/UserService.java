package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.RoleEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.UserModel;
import pl.mkjb.exchange.repository.RoleRepository;
import pl.mkjb.exchange.repository.TransactionRepository;
import pl.mkjb.exchange.repository.UserRepository;
import pl.mkjb.exchange.util.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.mkjb.exchange.util.Role.ROLE_OWNER;
import static pl.mkjb.exchange.util.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyService currencyService;
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
        val savedUserEntity = userRepository.save(userEntity);
        saveInitialTransactions(savedUserEntity);
        addFundsForUserForDemonstration(savedUserEntity);
    }

    public UserEntity findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadResourceException("Given user id doesn't exist" + id));
    }

    public UserEntity findOwner() {
        val roleEntity = findRoleByName(ROLE_OWNER);
        return findUsersByRole(roleEntity)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadResourceException("User with ROLE_OWNER doesn't exists"));
    }

    private RoleEntity findRoleByName(Role role) {
        return roleRepository.findByRole(role.name())
                .getOrElseThrow(() -> new BadResourceException("Given role name doesn't exist" + role));
    }

    private Set<UserEntity> findUsersByRole(RoleEntity roleEntity) {
        return userRepository.findByRolesContaining(roleEntity);
    }

    private void saveInitialTransactions(UserEntity userEntity) {
        val transactionEntities = currencyService.findAll()
                .stream()
                .map(currencyEntity ->
                        TransactionEntity.builder()
                                .userEntity(userEntity)
                                .currencyEntity(currencyEntity)
                                .amount(BigDecimal.ZERO)
                                .currencyRate(BigDecimal.ONE)
                                .createdAt(LocalDateTime.now())
                                .build())
                .collect(Collectors.toUnmodifiableSet());
        transactionRepository.saveAll(transactionEntities);
    }

    private void addFundsForUserForDemonstration(UserEntity userEntity) {
        val currencyEntity = currencyService.findBaseCurrencyRate().getCurrencyEntity();
        val transactionEntity = TransactionEntity.builder()
                .userEntity(userEntity)
                .currencyEntity(currencyEntity)
                .amount(BigDecimal.valueOf(10000))
                .currencyRate(BigDecimal.ONE)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transactionEntity);
    }
}
