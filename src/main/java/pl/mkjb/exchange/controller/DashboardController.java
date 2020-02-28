package pl.mkjb.exchange.controller;

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
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.WalletModel;
import pl.mkjb.exchange.restclient.RestClient;
import pl.mkjb.exchange.security.CustomAuthenticatedUser;
import pl.mkjb.exchange.service.CurrencyService;
import pl.mkjb.exchange.service.WalletService;

import java.math.BigDecimal;
import java.util.Set;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private static final String VIEW_NAME = "dashboard";
    private final RestClient futureProcessingRestClient;
    private final CurrencyService currencyService;
    private final WalletService walletService;


    @GetMapping
    public String show() {
        return VIEW_NAME;
    }

    @ResponseBody
    @GetMapping("/wallet")
    public ResponseEntity<Set<WalletModel>> getUserWallet(@AuthenticationPrincipal CustomAuthenticatedUser customAuthenticatedUser) {
        if (futureProcessingRestClient.isConnectionAlive()) {
            val userWallet = walletService.getUserWallet(customAuthenticatedUser.getId());
            return ResponseEntity.status(HttpStatus.OK).body(userWallet);
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @GetMapping("/currencies")
    public ResponseEntity<CurrencyRatesModel> getAllCurrencies() {
        if (futureProcessingRestClient.isConnectionAlive()) {
            val newestRates = currencyService.getNewestRates();
            return ResponseEntity.status(HttpStatus.OK).body(newestRates);
        }
        return ResponseEntity.notFound().build();
    }

    @ModelAttribute("baseCurrencyAmount")
    public BigDecimal getUserWalletBaseCurrencyAmount(@AuthenticationPrincipal CustomAuthenticatedUser customAuthenticatedUser) {
        return walletService.getUserWalletAmountForBaseCurrency(customAuthenticatedUser.getId());
    }
}

