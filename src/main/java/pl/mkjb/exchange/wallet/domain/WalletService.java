package pl.mkjb.exchange.wallet.domain;

import io.vavr.collection.Set;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.wallet.dto.UserWalletDto;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
class WalletService {
    private final CurrencyFacade currencyFacade;
    private final WalletRepository walletRepository;

    public Set<UserWalletDto> getUserWallet(UserDetails userDetails) {
        final Set<UserWalletDto> userWallet = walletRepository.findUserWallet(userDetails.getUsername());
        return addNewestCurrencyRatesToUserWallet(userWallet);
    }

    private Set<UserWalletDto> addNewestCurrencyRatesToUserWallet(Set<UserWalletDto> userWallet) {
        final Set<CurrencyRateDto> newestCurrencyRates = currencyFacade.getNewestCurrencyRates();

        return userWallet.filter(this::isNotBillingCurrency)
                .map(wallet -> {
                    final CurrencyRateDto currentCurrencyRate = getCurrentWalletCurrency(newestCurrencyRates, wallet);

                    return UserWalletDto.builder()
                            .amount(wallet.getAmount())
                            .code(wallet.getCode())
                            .unit(currentCurrencyRate.getCurrencyDto().getUnit())
                            .currencyRateId(currentCurrencyRate.getId())
                            .purchasePrice(currentCurrencyRate.getPurchasePrice())
                            .build();
                })
                .toSet();
    }

    private boolean isNotBillingCurrency(UserWalletDto userWallet) {
        return currencyFacade.findBillingCurrency()
                .filter(currency -> !currency.getCurrencyDto().getCode().equals(userWallet.getCode()))
                .isDefined();
    }

    private CurrencyRateDto getCurrentWalletCurrency(Set<CurrencyRateDto> currencyRateDto, UserWalletDto userWalletDto) {
        return currencyRateDto.find(currencyRate -> {
            val code = currencyRate.getCurrencyDto().getCode();
            return code.equals(userWalletDto.getCode());
        })
                .getOrElseThrow(() -> new CurrencyNotFoundException("There's no currency with given code " + userWalletDto.getCode()));
    }

    public BigDecimal getUserWalletAmountForGivenCurrency(UUID currencyId, UserDetails userDetails) {
        return currencyFacade.findCurrencyRateByCurrencyRateId(currencyId)
                .map(currency -> getCurrencyAmount(userDetails, currency))
                .getOrElseThrow(() -> new CurrencyNotFoundException("No billing currency with id " + currencyId + " found"));
    }

    public BigDecimal getUserWalletAmountForBillingCurrency(UserDetails userDetails) {
        return currencyFacade.findBillingCurrency()
                .map(currency -> getCurrencyAmount(userDetails, currency))
                .getOrElseThrow(() -> new CurrencyNotFoundException("No billing currency found"));
    }

    private BigDecimal getCurrencyAmount(UserDetails userDetails, CurrencyRateDto currencyRateEntity) {
        return walletRepository.findUserWallet(userDetails.getUsername())
                .filter(userWalletDto -> userWalletDto.getCode().equals(currencyRateEntity.getCurrencyDto().getCode()))
                .map(UserWalletDto::getAmount)
                .getOrElse(BigDecimal.ZERO);
    }

    public boolean hasInsufficientFundsToBuyCurrency(UUID currencyId, UserDetails userDetails) {
        return currencyFacade.findCurrencyRateByCurrencyRateId(currencyId)
                .map(this::calculateMinimalTransactionAmount)
                .map(minimalTransactionAmount -> getUserWalletAmountForBillingCurrency(userDetails).compareTo(minimalTransactionAmount) < 1)
                .getOrElseThrow(() -> new CurrencyNotFoundException("No billing currency with id " + currencyId + " found"));
    }

    private BigDecimal calculateMinimalTransactionAmount(CurrencyRateDto currencyRateDto) {
        return currencyRateDto.getSellPrice().multiply(currencyRateDto.getCurrencyDto().getUnit());
    }

    public boolean hasInsufficientFundsToSellCurrency(UUID currencyId, UserDetails userDetails) {
        return getUserWalletAmountForGivenCurrency(currencyId, userDetails).compareTo(BigDecimal.ZERO) < 1;
    }
}
