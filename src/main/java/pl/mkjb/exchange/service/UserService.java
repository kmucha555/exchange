package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import pl.mkjb.exchange.util.RoleConstant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.mkjb.exchange.util.RoleConstant.ROLE_OWNER;
import static pl.mkjb.exchange.util.RoleConstant.ROLE_USER;

@Slf4j
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
        val roleEntity = findRoleByName(ROLE_USER);
        val userEntity = UserEntity.fromModel(userModel);
        userEntity.setRoles(Set.of(roleEntity));
        userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));

        val savedUserEntity = userRepository.save(userEntity);

        saveInitialTransactions(savedUserEntity);
        addFundsForUserForDemonstration(savedUserEntity);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Given username not found: {}", username);
                    throw new UsernameNotFoundException("Given username not found: " + username);
                });
    }

    public UserEntity findOwner() {
        val roleEntity = findRoleByName(ROLE_OWNER);
        return userRepository.findByRolesContaining(roleEntity)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
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

    //Only for demo purpose
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

    //Only for demo purpose
    private void addFundsForUserForDemonstration(UserEntity userEntity) {
        val currencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
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
