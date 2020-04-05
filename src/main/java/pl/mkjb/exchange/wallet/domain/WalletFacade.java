package pl.mkjb.exchange.wallet.domain;

import io.vavr.collection.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.wallet.dto.UserWalletDto;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class WalletFacade {
    private final WalletService walletService;

    public Set<UserWalletDto> getUserWallet(UserDetails userDetails) {
        return walletService.getUserWallet(userDetails);
    }

    public BigDecimal getUserWalletAmountForGivenCurrency(UUID currencyId, UserDetails userDetails) {
        return walletService.getUserWalletAmountForGivenCurrency(currencyId, userDetails);
    }

    public BigDecimal getUserWalletAmountForBillingCurrency(UserDetails userDetails) {
        return walletService.getUserWalletAmountForBillingCurrency(userDetails);
    }

    public boolean hasInsufficientFundsToBuyCurrency(UUID currencyId, UserDetails userDetails) {
        return walletService.hasInsufficientFundsToBuyCurrency(currencyId, userDetails);
    }

    public boolean hasInsufficientFundsToSellCurrency(UUID currencyId, UserDetails userDetails) {
        return walletService.hasInsufficientFundsToSellCurrency(currencyId, userDetails);
    }
}
