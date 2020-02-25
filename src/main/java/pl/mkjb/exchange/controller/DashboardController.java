package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.WalletModel;
import pl.mkjb.exchange.security.CustomAuthenticatedUser;
import pl.mkjb.exchange.service.CurrencyService;
import pl.mkjb.exchange.service.WalletService;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private static final String VIEW_NAME = "dashboard";
    private final CurrencyService currencyService;
    private final WalletService walletService;


    @GetMapping
    public String show() {
        return VIEW_NAME;
    }

    @ResponseBody
    @GetMapping("/wallet")
    public Set<WalletModel> getUserWallet(@AuthenticationPrincipal CustomAuthenticatedUser customAuthenticatedUser) {
        return walletService.getUserWallet(customAuthenticatedUser.getId());
    }

    @ResponseBody
    @GetMapping("/currencies")
    public CurrencyRatesModel getAllCurrencies() {
        return currencyService.getNewestRates();
    }
}

