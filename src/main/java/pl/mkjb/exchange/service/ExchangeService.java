package pl.mkjb.exchange.service;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function4;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.repository.TransactionRepository;
import pl.mkjb.exchange.util.TransactionTypeConstant;

import java.math.BigDecimal;
import java.util.Set;

import static java.math.RoundingMode.HALF_UP;
import static pl.mkjb.exchange.util.TransactionTypeConstant.BUY;
import static pl.mkjb.exchange.util.TransactionTypeConstant.SELL;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CurrencyService currencyService;

    public Set<TransactionEntity> prepareTransactionToSave(TransactionBuilder transactionBuilder) {
        final CurrencyEntity billingCurrencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
        final CurrencyEntity currencyEntity = currencyService.findCurrencyById(transactionBuilder.getCurrencyRateEntity().getCurrencyEntity().getId());
        final UserEntity exchangeOwnerEntity = userService.findOwner();
        final UserEntity userEntity = userService.findByUsername(transactionBuilder.getUserDetails().getUsername());

        return Set.of(
                prepareTransactionEntity().apply(
                        currencyEntity,
                        userEntity,
                        transactionBuilder.getTransactionPrice(),
                        calculateCurrencyAmount().apply(SELL, transactionBuilder)),

                prepareTransactionEntity().apply(
                        currencyEntity,
                        exchangeOwnerEntity,
                        transactionBuilder.getTransactionPrice(),
                        calculateCurrencyAmount().apply(BUY, transactionBuilder)),

                prepareTransactionEntity().apply(
                        billingCurrencyEntity,
                        userEntity,
                        transactionBuilder.getTransactionPrice(),
                        calculateBillingCurrencyAmount().apply(SELL).apply(transactionBuilder, currencyEntity)),

                prepareTransactionEntity().apply(
                        billingCurrencyEntity,
                        exchangeOwnerEntity,
                        transactionBuilder.getTransactionPrice(),
                        calculateBillingCurrencyAmount().apply(BUY).apply(transactionBuilder, currencyEntity))
        );
    }

    private Function2<TransactionTypeConstant, TransactionBuilder, BigDecimal> calculateCurrencyAmount() {
        return (transactionType, transaction) -> Option.of(transactionType)
                .filter(type -> type.equals(BUY))
                .map(type -> transaction.getTransactionAmount())
                .getOrElse(() -> transaction.getTransactionAmount().negate());
    }

    private Function1<TransactionTypeConstant, Function2<TransactionBuilder, CurrencyEntity, BigDecimal>> calculateBillingCurrencyAmount() {
        return transactionType -> Option.of(transactionType)
                .filter(type -> type.equals(BUY))
                .map(type -> decreaseBalance())
                .getOrElse(this::increaseBalance);
    }

    private Function2<TransactionBuilder, CurrencyEntity, BigDecimal> increaseBalance() {
        return (transaction, currency) -> transaction.getTransactionAmount()
                .multiply(transaction.getTransactionPrice())
                .divide(currency.getUnit(), HALF_UP);
    }

    private Function2<TransactionBuilder, CurrencyEntity, BigDecimal> decreaseBalance() {
        return (transaction, currency) -> increaseBalance().apply(transaction, currency).negate();
    }

    private Function4<CurrencyEntity, UserEntity, BigDecimal, BigDecimal, TransactionEntity> prepareTransactionEntity() {
        return (currencyEntity, userEntity, transactionPrice, amount) ->
                TransactionEntity.builder()
                        .currencyEntity(currencyEntity)
                        .userEntity(userEntity)
                        .currencyRate(transactionPrice)
                        .amount(amount)
                        .build();
    }

    public void saveTransaction(Set<TransactionEntity> transactionEntities) {
        transactionRepository.saveAll(transactionEntities);
    }
}
