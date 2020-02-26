package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import pl.mkjb.exchange.security.CustomAuthenticatedUser;
import pl.mkjb.exchange.service.TransactionService;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/buy/{currencyRateId}")
    public ModelAndView showBuyForm(@PathVariable UUID currencyRateId, @AuthenticationPrincipal CustomAuthenticatedUser authenticatedUser) {
        return new ModelAndView("buy", Map.of("transactionBuyModel", transactionService.getTransactionBuyModel(currencyRateId, authenticatedUser.getId())));
    }
}
