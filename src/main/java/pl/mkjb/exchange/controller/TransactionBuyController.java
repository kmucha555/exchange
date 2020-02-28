package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.security.CustomAuthenticatedUser;
import pl.mkjb.exchange.service.CurrencyService;
import pl.mkjb.exchange.service.Transaction;
import pl.mkjb.exchange.service.WalletService;
import pl.mkjb.exchange.util.Message;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionBuyController {
    @Value("${pl.mkjb.exchange.controller.WrongAmount.message}")
    private String wrongAmount;
    private static final String MESSAGE_SUCCESS = Message.MESSAGE_SUCCESS.name();
    private static final String MESSAGE_FAILED = Message.MESSAGE_FAILED.name();
    private static final String VIEW_NAME = "buy";
    private static final String MODEL_NAME = "transactionModel";
    private static final String REDIRECT_URL = "redirect:/dashboard";
    private final Transaction transactionBuyService;
    private final WalletService walletService;
    private final CurrencyService currencyService;

    @GetMapping("/buy/{currencyRateId}")
    public String showBuyForm(@PathVariable UUID currencyRateId,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal CustomAuthenticatedUser authenticatedUser) {

        if (currencyService.isArchivedCurrencyRate(currencyRateId)) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Given currency rate has been archived");
            return REDIRECT_URL;
        }

        if (walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, authenticatedUser.getId())) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Insufficient funds");
            return REDIRECT_URL;
        }

        model.addAttribute(MODEL_NAME, transactionBuyService.getTransactionModel(currencyRateId, authenticatedUser.getId()));
        return VIEW_NAME;
    }

    @PostMapping("/buy")
    public String buyCurrency(@Valid TransactionModel transactionModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal CustomAuthenticatedUser authenticatedUser) {

        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }
        if (currencyService.isArchivedCurrencyRate(transactionModel.getCurrencyRateId())) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Transaction failed! Given currency rate has been archived. Try again.");
            return REDIRECT_URL;
        }

        if (transactionBuyService.hasErrors(transactionModel, authenticatedUser.getId())) {
            bindingResult.rejectValue("transactionAmount", "error.user", wrongAmount);
            return VIEW_NAME;
        }

        transactionBuyService.saveTransaction(transactionModel, authenticatedUser.getId());
        redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS,
                String.format("Success! You bought %s %s", transactionModel.getTransactionAmount(), transactionModel.getCurrencyCode()));

        return REDIRECT_URL;
    }
}
