package pl.mkjb.exchange.service;

import io.vavr.Function4;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TModel;
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

    public void saveTransaction(TModel tModel) {
        final CurrencyEntity baseCurrencyEntity = currencyService.findBaseCurrencyRate().getCurrencyEntity();
        final CurrencyEntity currencyEntity = currencyService.findCurrencyById(tModel.getCurrencyRateEntity().getCurrencyEntity().getId());
        final UserEntity exchangeOwner = userService.findOwner();
        final UserEntity userEntity = userService.findById(tModel.getUserId());

        BigDecimal transactionBaseCurrencyAmount = tModel.getTransactionAmount().multiply(tModel.getTransactionPrice())
                .divide(BigDecimal.valueOf(currencyEntity.getUnit()), HALF_UP);

        final Set<TransactionEntity> transactionEntities = Set.of(
                prepareTransactionEntity().apply(
                        currencyEntity,
                        userEntity,
                        tModel.getTransactionPrice(),
                        tModel.getTransactionAmount()),

                prepareTransactionEntity().apply(
                        currencyEntity,
                        exchangeOwner,
                        tModel.getTransactionPrice(),
                        tModel.getTransactionAmount().negate()),

                prepareTransactionEntity().apply(
                        baseCurrencyEntity,
                        userEntity,
                        tModel.getTransactionPrice(),
                        transactionBaseCurrencyAmount.negate()),

                prepareTransactionEntity().apply(
                        baseCurrencyEntity,
                        exchangeOwner,
                        tModel.getTransactionPrice(),
                        transactionBaseCurrencyAmount));

        save(transactionEntities);
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

    private void save(Set<TransactionEntity> transactionEntities) {
        transactionRepository.saveAll(transactionEntities);
    }
}
