package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CurrencyService currencyService;

    public void sellCurrency(CurrencyRateEntity currencyRateEntity, BigDecimal amount, long userId) {
        val baseCurrencyEntity = currencyService.findBaseCurrencyRate().getCurrencyEntity();
        val currencySellPrice = currencyService.findCurrencyRateByCurrencyRateId(currencyRateEntity.getId()).getSellPrice();
        val currencyEntity = currencyService.findCurrencyById(currencyRateEntity.getCurrencyEntity().getId());
        val userEntity = userService.findById(userId);
        val ownerEntity = userService.findOwner();

        val currencyUnit = BigDecimal.valueOf(currencyEntity.getUnit());
        val transactionAmountIncreaseBalanceSoldCurrency = amount;
        val transactionAmountReduceBalanceSoldCurrency = amount.negate();
        val transactionAmountIncreaseBalanceBaseCurrency = amount.multiply(currencySellPrice).divide(currencyUnit, HALF_UP);
        val transactionAmountReduceBalanceBaseCurrency = transactionAmountIncreaseBalanceBaseCurrency.negate();

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
