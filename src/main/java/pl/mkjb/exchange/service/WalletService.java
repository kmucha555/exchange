package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.UserWalletModel;
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

    public Set<UserWalletModel> getUserWallet(UserDetails userDetails) {
        final Set<UserWalletModel> userWallet = transactionRepository.findUserWallet(userDetails.getUsername());
        return addNewestCurrencyRatesToUserWallet(userWallet);
    }

    private Set<UserWalletModel> addNewestCurrencyRatesToUserWallet(Set<UserWalletModel> userWallet) {
        val baseCurrencyRateEntity = currencyService.findBillingCurrencyRate();
        final CurrencyRatesModel currencyRatesModel = currencyService.getNewestRates();
        return userWallet.stream()
                .filter(userWalletModel -> !userWalletModel.getCode().equals(baseCurrencyRateEntity.getCurrencyEntity().getCode()))
                .map(userWalletModel -> {
                    val walletCurrency = getCurrencyModelForWalletCurrency(currencyRatesModel, userWalletModel);
                    return UserWalletModel.builder()
                            .amount(userWalletModel.getAmount())
                            .code(userWalletModel.getCode())
                            .unit(walletCurrency.getUnit())
                            .currencyRateId(walletCurrency.getCurrencyRateId())
                            .purchasePrice(walletCurrency.getPurchasePrice())
                            .build();
                })
                .collect(Collectors.toUnmodifiableSet());
    }

    private CurrencyModel getCurrencyModelForWalletCurrency(CurrencyRatesModel currencyRatesModel, UserWalletModel userWalletModel) {
        return currencyRatesModel.getItems()
                .stream()
                .filter(currencyModel -> currencyModel.getCode().equals(userWalletModel.getCode()))
                .findFirst()
                .orElseThrow(() -> new BadResourceException("There's no currency with given code " + userWalletModel.getCode()));
    }

    public BigDecimal getUserWalletAmountForGivenCurrency(UUID currencyId, UserDetails userDetails) {
        val currencyEntity =
                currencyService.findCurrencyRateByCurrencyRateId(currencyId)
                        .getCurrencyEntity();

        return getCurrencyAmount(userDetails, currencyEntity);
    }

    public BigDecimal getUserWalletAmountForBillingCurrency(UserDetails userDetails) {
        val billingCurrencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
        return getCurrencyAmount(userDetails, billingCurrencyEntity);
    }

    private BigDecimal getCurrencyAmount(UserDetails userDetails, CurrencyEntity currencyRateEntity) {
        return transactionRepository.findUserWallet(userDetails.getUsername())
                .stream()
                .filter(userWalletModel -> userWalletModel.getCode().equals(currencyRateEntity.getCode()))
                .map(UserWalletModel::getAmount)
                .findAny()
                .orElse(BigDecimal.ZERO);
    }

    public boolean hasInsufficientFundsForBuyCurrency(UUID currencyId, UserDetails userDetails) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyId);
        val minimalTransactionAmount = currencyRateEntity.getSellPrice().multiply(currencyRateEntity.getCurrencyEntity().getUnit());
        return getUserWalletAmountForBillingCurrency(userDetails).compareTo(minimalTransactionAmount) < 1;
    }

    public boolean hasInsufficientFundsForSellCurrency(UUID currencyId, UserDetails userDetails) {
        return getUserWalletAmountForGivenCurrency(currencyId, userDetails).compareTo(BigDecimal.ZERO) < 1;
    }
}
