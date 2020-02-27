package pl.mkjb.exchange.service;

import io.vavr.Function4;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Set;

import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CurrencyService currencyService;

    public void saveTransaction(CurrencyRateEntity currencyRateEntity, BigDecimal transactionCurrencyAmount, long userId) {
        final CurrencyEntity baseCurrencyEntity = currencyService.findBaseCurrencyRate().getCurrencyEntity();
        final CurrencyEntity currencyEntity = currencyService.findCurrencyById(currencyRateEntity.getCurrencyEntity().getId());
        final UserEntity exchangeOwner = userService.findOwner();
        final UserEntity userEntity = userService.findById(userId);
        BigDecimal transactionBaseCurrencyAmount = transactionCurrencyAmount.multiply(currencyRateEntity.getSellPrice())
                .divide(BigDecimal.valueOf(currencyEntity.getUnit()), HALF_UP);

        final Set<TransactionEntity> transactionEntities = Set.of(
                prepareTransactionEntity().apply(
                        currencyEntity,
                        userEntity,
                        transactionCurrencyAmount,
                        currencyRateEntity.getSellPrice()),

                prepareTransactionEntity().apply(
                        currencyEntity,
                        exchangeOwner,
                        transactionCurrencyAmount.negate(),
                        currencyRateEntity.getSellPrice()),

                prepareTransactionEntity().apply(
                        baseCurrencyEntity,
                        userEntity,
                        transactionBaseCurrencyAmount.negate(),
                        currencyRateEntity.getSellPrice()),

                prepareTransactionEntity().apply(
                        baseCurrencyEntity,
                        exchangeOwner,
                        transactionBaseCurrencyAmount,
                        currencyRateEntity.getSellPrice()));

        save(transactionEntities);
    }

    private Function4<CurrencyEntity, UserEntity, BigDecimal, BigDecimal, TransactionEntity> prepareTransactionEntity() {
        return (currencyEntity, userEntity, amount, transactionPrice) ->
                TransactionEntity.builder()
                        .currencyEntity(currencyEntity)
                        .userEntity(userEntity)
                        .amount(amount)
                        .currencyRate(transactionPrice)
                        .build();
    }

    private void save(Set<TransactionEntity> transactionEntities) {
        transactionRepository.saveAll(transactionEntities);
    }
}
