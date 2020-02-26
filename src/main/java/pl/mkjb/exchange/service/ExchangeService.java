package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;

import static pl.mkjb.exchange.util.Role.ROLE_OWNER;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public BigDecimal calculateAvailableCurrency(int currencyId) {
        val roleEntity = userService.findRoleByName(ROLE_OWNER);
        val userEntity =
                userService.findUsersByRole(roleEntity)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new BadResourceException("User with owner role doesn't exists"));

        return transactionRepository.sumCurrencyAmountForUser(userEntity.getId(), currencyId)
                .getOrElse(BigDecimal.ZERO);
    }
}
