package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1);
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CurrencyService currencyService;

    public void sellCurrency(CurrencyModel currencyModel, BigDecimal amount, long userId) {
        val baseCurrencyEntity = currencyService.findBaseCurrency();
        val currencySellPrice = currencyService.findCurrencyByCurrencyRate(currencyModel.getCurrencyRateId()).getSellPrice();
        val currencyEntity = currencyService.findCurrencyById(currencyModel.getCurrencyId());
        val userEntity = userService.findById(userId);
        val ownerEntity = userService.findOwner();

        val currencyUnit = BigDecimal.valueOf(currencyEntity.getUnit());
        val transactionAmountIncreaseBalanceSoldCurrency = amount;
        val transactionAmountReduceBalanceSoldCurrency = amount.multiply(MINUS_ONE);
        val transactionAmountIncreaseBalanceBaseCurrency = amount.multiply(currencySellPrice).divide(currencyUnit, RoundingMode.HALF_UP);
        val transactionAmountReduceBalanceBaseCurrency = amount.multiply(currencySellPrice).multiply(MINUS_ONE).divide(currencyUnit, RoundingMode.HALF_UP);

        saveTransaction(currencyEntity, ownerEntity, currencySellPrice, transactionAmountReduceBalanceSoldCurrency);
        saveTransaction(baseCurrencyEntity, ownerEntity, currencySellPrice, transactionAmountIncreaseBalanceBaseCurrency);
        saveTransaction(currencyEntity, userEntity, currencySellPrice, transactionAmountIncreaseBalanceSoldCurrency);
        saveTransaction(baseCurrencyEntity, userEntity, currencySellPrice, transactionAmountReduceBalanceBaseCurrency);
    }

    private void saveTransaction(CurrencyEntity currencyEntity,
                                 UserEntity userEntity,
                                 BigDecimal currencyRate,
                                 BigDecimal transactionAmount) {

        val transactionEntity = TransactionEntity.builder()
                .currencyEntity(currencyEntity)
                .currencyRate(currencyRate)
                .userEntity(userEntity)
                .amount(transactionAmount)
                .build();
        transactionRepository.save(transactionEntity);
    }
}
