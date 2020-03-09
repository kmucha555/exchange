package pl.mkjb.exchange.service;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.UserWalletModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

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
        final CurrencyRateEntity billingCurrencyRateEntity = currencyService.findBillingCurrencyRate();
        final CurrencyRatesModel currencyRatesModel = currencyService.getNewestRates();

        return userWallet.filter(wallet -> !wallet.getCode().equals(billingCurrencyRateEntity.getCurrencyEntity().getCode()))
                .map(userWalletModel -> {
                    val currentWalletCurrency = getCurrencyModelForCurrentWalletCurrency(currencyRatesModel, userWalletModel);

                    return UserWalletModel.builder()
                            .amount(userWalletModel.getAmount())
                            .code(userWalletModel.getCode())
                            .unit(currentWalletCurrency.getUnit())
                            .currencyRateId(currentWalletCurrency.getCurrencyRateId())
                            .purchasePrice(currentWalletCurrency.getPurchasePrice())
                            .build();
                })
                .toSet();
    }

    private CurrencyModel getCurrencyModelForCurrentWalletCurrency(CurrencyRatesModel currencyRatesModel, UserWalletModel userWalletModel) {
        return HashSet.ofAll(currencyRatesModel.getItems())
                .filter(currencyModel -> currencyModel.getCode().equals(userWalletModel.getCode()))
                .getOrElseThrow(() -> new BadResourceException("There's no currency with given code " + userWalletModel.getCode()));
    }

    public BigDecimal getUserWalletAmountForGivenCurrency(UUID currencyId, UserDetails userDetails) {
        final CurrencyEntity currencyEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyId)
                .getCurrencyEntity();

        return getCurrencyAmount(userDetails, currencyEntity);
    }

    public BigDecimal getUserWalletAmountForBillingCurrency(UserDetails userDetails) {
        final CurrencyEntity billingCurrencyEntity = currencyService.findBillingCurrencyRate().getCurrencyEntity();
        return getCurrencyAmount(userDetails, billingCurrencyEntity);
    }

    private BigDecimal getCurrencyAmount(UserDetails userDetails, CurrencyEntity currencyRateEntity) {
        return transactionRepository.findUserWallet(userDetails.getUsername())
                .filter(userWalletModel -> userWalletModel.getCode().equals(currencyRateEntity.getCode()))
                .map(UserWalletModel::getAmount)
                .getOrElse(BigDecimal.ZERO);
    }

    public boolean hasInsufficientFundsForBuyCurrency(UUID currencyId, UserDetails userDetails) {
        final CurrencyRateEntity currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyId);
        final BigDecimal minimalTransactionAmount = currencyRateEntity.getSellPrice()
                .multiply(currencyRateEntity
                        .getCurrencyEntity()
                        .getUnit());

        return getUserWalletAmountForBillingCurrency(userDetails).compareTo(minimalTransactionAmount) < 1;
    }

    public boolean hasInsufficientFundsForSellCurrency(UUID currencyId, UserDetails userDetails) {
        return getUserWalletAmountForGivenCurrency(currencyId, userDetails).compareTo(BigDecimal.ZERO) < 1;
    }
}
