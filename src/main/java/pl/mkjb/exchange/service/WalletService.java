package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.WalletModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {
    private final CurrencyService currencyService;
    private final TransactionRepository transactionRepository;

    public Set<WalletModel> getUserWallet(long userId) {
        val userWallet = transactionRepository.findUserWallet(userId);
        return addNewestCurrencyRatesToUserWallet(userWallet);
    }

    private Set<WalletModel> addNewestCurrencyRatesToUserWallet(Set<WalletModel> userWallet) {
        val baseCurrencyRateEntity = currencyService.findBillingCurrencyRate();
        final CurrencyRatesModel currencyRatesModel = currencyService.getNewestRates();
        return userWallet.stream()
                .filter(walletModel -> !walletModel.getCode().equals(baseCurrencyRateEntity.getCurrencyEntity().getCode()))
                .map(walletModel -> {
                    val walletCurrency = getCurrencyModelForWalletCurrency(currencyRatesModel, walletModel);
                    return WalletModel.builder()
                            .amount(walletModel.getAmount())
                            .code(walletModel.getCode())
                            .unit(walletCurrency.getUnit())
                            .currencyRateId(walletCurrency.getCurrencyRateId())
                            .purchasePrice(walletCurrency.getPurchasePrice())
                            .build();
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    private CurrencyModel getCurrencyModelForWalletCurrency(CurrencyRatesModel currencyRatesModel, WalletModel walletModel) {
        return currencyRatesModel.getItems()
                .stream()
                .filter(currencyModel -> currencyModel.getCode().equals(walletModel.getCode()))
                .findFirst()
                .orElseThrow(() -> new BadResourceException("There's no currency with given code " + walletModel.getCode()));
    }

    public BigDecimal getUserWalletAmountForGivenCurrency(UUID currencyId, long userId) {
        val currencyEntity =
                currencyService.findCurrencyRateByCurrencyRateId(currencyId)
                        .getCurrencyEntity();

        return getCurrencyAmount(userId, currencyEntity);
    }

    public BigDecimal getUserWalletAmountForBillingCurrency(long userId) {
        val billingCurrencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
        return getCurrencyAmount(userId, billingCurrencyEntity);
    }

    private BigDecimal getCurrencyAmount(long userId, CurrencyEntity currencyRateEntity) {
        return transactionRepository.findUserWallet(userId)
                .stream()
                .filter(walletModel -> walletModel.getCode().equals(currencyRateEntity.getCode()))
                .map(WalletModel::getAmount)
                .findAny()
                .orElse(BigDecimal.ZERO);
    }

    public boolean hasInsufficientFundsForBuyCurrency(UUID currencyId, long userId) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyId);
        val minimalTransactionAmount = currencyRateEntity.getSellPrice().multiply(currencyRateEntity.getCurrencyEntity().getUnit());
        return getUserWalletAmountForBillingCurrency(userId).compareTo(minimalTransactionAmount) < 1;
    }

    public boolean hasInsufficientFundsForSellCurrency(UUID currencyId, long userId) {
        return getUserWalletAmountForGivenCurrency(currencyId, userId).compareTo(BigDecimal.ZERO) < 1;
    }
}
