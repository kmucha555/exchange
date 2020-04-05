package pl.mkjb.exchange.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.security.CustomUser;
import pl.mkjb.exchange.restclient.RestClient;
import pl.mkjb.exchange.wallet.domain.WalletFacade;
import pl.mkjb.exchange.wallet.dto.UserWalletDto;

import java.math.BigDecimal;
import java.util.Set;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
class DashboardController {
    private static final String VIEW_NAME = "dashboard";
    private final RestClient futureProcessingRestClient;
    private final CurrencyFacade currencyFacade;
    private final WalletFacade walletFacade;


    @GetMapping
    public String show() {
        return VIEW_NAME;
    }

    @ResponseBody
    @GetMapping("/wallet")
    public ResponseEntity<Set<UserWalletDto>> getUserWallet(@AuthenticationPrincipal CustomUser customUser) {
        if (futureProcessingRestClient.isConnectionAlive()) {
            val userWallet = walletFacade.getUserWallet(customUser);
            return ResponseEntity.status(HttpStatus.OK).body(userWallet.toJavaSet());
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @GetMapping("/currencies")
    public ResponseEntity<Set<CurrencyRateDto>> getAllCurrencies() {
        if (futureProcessingRestClient.isConnectionAlive()) {
            val newestRates = currencyFacade.getNewestCurrencyRates().toJavaSet();
            return ResponseEntity.status(HttpStatus.OK).body(newestRates);
        }
        return ResponseEntity.notFound().build();
    }

    @ModelAttribute("baseCurrencyAmount")
    public BigDecimal getUserWalletBillingCurrencyAmount(@AuthenticationPrincipal CustomUser customUser) {
        return walletFacade.getUserWalletAmountForBillingCurrency(customUser);
    }
}

