package pl.mkjb.exchange.service;

import io.vavr.Function4;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.repository.TransactionRepository;
import pl.mkjb.exchange.util.TransactionType;

import java.math.BigDecimal;
import java.util.Set;

import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CurrencyService currencyService;

    public Set<TransactionEntity> prepareTransactionToSave(TransactionBuilder transactionBuilder) {
        final CurrencyEntity billingCurrencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
        final CurrencyEntity currencyEntity = currencyService.findCurrencyById(transactionBuilder.getCurrencyRateEntity().getCurrencyEntity().getId());
        final UserEntity exchangeOwner = userService.findOwner();
        final UserEntity userEntity = userService.findById(transactionBuilder.getUserId());

        BigDecimal transactionBillingCurrencyAmount = calculateBillingCurrencyAmount(transactionBuilder, currencyEntity);

        return Set.of(
                prepareTransactionEntity().apply(
                        currencyEntity,
                        userEntity,
                        transactionBuilder.getTransactionPrice(),
                        transactionBuilder.getTransactionType().equals(TransactionType.BUY) ?
                                transactionBuilder.getTransactionAmount() : transactionBuilder.getTransactionAmount().negate()),

                prepareTransactionEntity().apply(
                        currencyEntity,
                        exchangeOwner,
                        transactionBuilder.getTransactionPrice(),
                        transactionBuilder.getTransactionType().equals(TransactionType.BUY) ?
                                transactionBuilder.getTransactionAmount().negate() : transactionBuilder.getTransactionAmount()),

                prepareTransactionEntity().apply(
                        billingCurrencyEntity,
                        userEntity,
                        transactionBuilder.getTransactionPrice(),
                        transactionBuilder.getTransactionType().equals(TransactionType.BUY) ?
                                transactionBillingCurrencyAmount.negate() : transactionBillingCurrencyAmount),

                prepareTransactionEntity().apply(
                        billingCurrencyEntity,
                        exchangeOwner,
                        transactionBuilder.getTransactionPrice(),
                        transactionBuilder.getTransactionType().equals(TransactionType.BUY) ?
                                transactionBillingCurrencyAmount : transactionBillingCurrencyAmount.negate()));


    }

    private BigDecimal calculateBillingCurrencyAmount(TransactionBuilder transactionBuilder, CurrencyEntity currencyEntity) {
        return transactionBuilder.getTransactionAmount().multiply(transactionBuilder.getTransactionPrice())
                .divide(currencyEntity.getUnit(), HALF_UP);
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
