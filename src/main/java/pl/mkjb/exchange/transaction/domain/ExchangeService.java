package pl.mkjb.exchange.transaction.domain;

import io.vavr.Function2;
import io.vavr.collection.HashSet;
import lombok.RequiredArgsConstructor;
import lombok.val;
import pl.mkjb.exchange.currency.domain.CurrencyEntity;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.currency.dto.CurrencyDto;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.infrastructure.util.RoleConstant;
import pl.mkjb.exchange.transaction.dto.TransactionBuilder;
import pl.mkjb.exchange.user.domain.UserEntity;
import pl.mkjb.exchange.user.domain.UserService;

import java.math.BigDecimal;
import java.util.Set;

import static java.math.RoundingMode.HALF_UP;

@RequiredArgsConstructor
class ExchangeService {
    private final CurrencyFacade currencyFacade;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public Set<TransactionEntity> prepareTransactionToSave(TransactionBuilder transactionBuilder) {
        final UserEntity exchangeOwnerEntity = userService.findOwner();
        final UserEntity userEntity = transactionBuilder.getUserEntity();

        return Set.of(
                prepareTransactionInTransactionCurrency().apply(userEntity, transactionBuilder),
                prepareTransactionInBillingCurrency().apply(userEntity, transactionBuilder),
                prepareTransactionInTransactionCurrency().apply(exchangeOwnerEntity, transactionBuilder),
                prepareTransactionInBillingCurrency().apply(exchangeOwnerEntity, transactionBuilder)
        );
    }

    private Function2<UserEntity, TransactionBuilder, TransactionEntity> prepareTransactionInTransactionCurrency() {
        return (user, transaction) ->
                TransactionEntity
                        .builder()
                        .currencyEntity(transaction.getCurrencyRateEntity().getCurrencyEntity())
                        .userEntity(user)
                        .currencyRate(transaction.getTransactionPrice())
                        .amount(calculateTransactionAmountInTransactionCurrency().apply(user, transaction))
                        .build();
    }

    private Function2<UserEntity, TransactionBuilder, TransactionEntity> prepareTransactionInBillingCurrency() {
        final CurrencyEntity billingCurrencyEntity = currencyFacade.findBillingCurrency()
                .map(CurrencyRateDto::getCurrencyDto)
                .map(CurrencyDto::toEntity)
                .getOrElseThrow(() -> new CurrencyNotFoundException(""));
        //TODO refactor

        return (user, transaction) ->
                TransactionEntity
                        .builder()
                        .currencyEntity(billingCurrencyEntity)
                        .userEntity(user)
                        .currencyRate(transaction.getTransactionPrice())
                        .amount(calculateTransactionAmountInBillingCurrency().apply(user, transaction))
                        .build();
    }

    private Function2<UserEntity, TransactionBuilder, BigDecimal> calculateTransactionAmountInTransactionCurrency() {
        return (user, transaction) ->
                HashSet.ofAll(user.getRoles())
                        .filter(roleEntity -> roleEntity.getRole().equals(RoleConstant.ROLE_USER.name()))
                        .map(roleEntity -> transaction.getTransactionAmount().negate())
                        .getOrElse(transaction.getTransactionAmount());
    }

    private Function2<UserEntity, TransactionBuilder, BigDecimal> calculateTransactionAmountInBillingCurrency() {
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

    public void saveCompleteTransaction(Set<TransactionEntity> transactionEntities) {
        transactionRepository.saveAll(transactionEntities);
    }
}
