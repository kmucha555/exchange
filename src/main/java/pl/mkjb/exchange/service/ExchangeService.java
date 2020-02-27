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
                        tModel.getTransactionAmount(),
                        tModel.getCurrencyRateEntity().getSellPrice()),

                prepareTransactionEntity().apply(
                        currencyEntity,
                        exchangeOwner,
                        tModel.getTransactionAmount().negate(),
                        tModel.getCurrencyRateEntity().getSellPrice()),

                prepareTransactionEntity().apply(
                        baseCurrencyEntity,
                        userEntity,
                        transactionBaseCurrencyAmount.negate(),
                        tModel.getCurrencyRateEntity().getSellPrice()),

                prepareTransactionEntity().apply(
                        baseCurrencyEntity,
                        exchangeOwner,
                        transactionBaseCurrencyAmount,
                        tModel.getCurrencyRateEntity().getSellPrice()));

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
