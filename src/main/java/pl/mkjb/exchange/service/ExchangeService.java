package pl.mkjb.exchange.service;

import io.vavr.Function2;
import io.vavr.collection.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.repository.TransactionRepository;
import pl.mkjb.exchange.util.RoleConstant;

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
        final UserEntity exchangeOwnerEntity = userService.findOwner();
        final UserEntity userEntity = transactionBuilder.getUserEntity();

        return Set.of(
                prepareTransactionForTransactionCurrency().apply(userEntity, transactionBuilder),
                prepareTransactionForBillingCurrency().apply(userEntity, transactionBuilder),
                prepareTransactionForTransactionCurrency().apply(exchangeOwnerEntity, transactionBuilder),
                prepareTransactionForBillingCurrency().apply(exchangeOwnerEntity, transactionBuilder)
        );
    }

    private Function2<UserEntity, TransactionBuilder, TransactionEntity> prepareTransactionForTransactionCurrency() {
        return (user, transaction) ->
                TransactionEntity
                        .builder()
                        .currencyEntity(transaction.getCurrencyRateEntity().getCurrencyEntity())
                        .userEntity(user)
                        .currencyRate(transaction.getTransactionPrice())
                        .amount(calculateTransactionCurrencyAmount().apply(user, transaction))
                        .build();
    }

    private Function2<UserEntity, TransactionBuilder, TransactionEntity> prepareTransactionForBillingCurrency() {
        final CurrencyEntity billingCurrencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
        return (user, transaction) ->
                TransactionEntity
                        .builder()
                        .currencyEntity(billingCurrencyEntity)
                        .userEntity(user)
                        .currencyRate(transaction.getTransactionPrice())
                        .amount(calculateBillingCurrencyAmount().apply(user, transaction))
                        .build();
    }

    private Function2<UserEntity, TransactionBuilder, BigDecimal> calculateTransactionCurrencyAmount() {
        return (user, transaction) ->
                HashSet.ofAll(user.getRoles())
                        .filter(roleEntity -> roleEntity.getRole().equals(RoleConstant.ROLE_USER.name()))
                        .map(roleEntity -> transaction.getTransactionAmount().negate())
                        .getOrElse(transaction.getTransactionAmount());
    }

    private Function2<UserEntity, TransactionBuilder, BigDecimal> calculateBillingCurrencyAmount() {
        return (user, transaction) -> {
            val billingCurrencyTransactionAmount = transaction.getTransactionAmount()
                    .multiply(transaction.getTransactionPrice())
                    .divide(transaction.getCurrencyRateEntity().getCurrencyEntity().getUnit(), HALF_UP);

            return HashSet.ofAll(user.getRoles())
                    .filter(roleEntity -> roleEntity.getRole().equals(RoleConstant.ROLE_USER.name()))
                    .map(roleEntity -> billingCurrencyTransactionAmount)
                    .getOrElse(billingCurrencyTransactionAmount.negate());
        };
    }

    public void saveTransaction(Set<TransactionEntity> transactionEntities) {
        transactionRepository.saveAll(transactionEntities);
    }
}
