package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
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
import pl.mkjb.exchange.security.CustomUser;
import pl.mkjb.exchange.service.CurrencyService;
import pl.mkjb.exchange.service.TransactionFacadeService;
import pl.mkjb.exchange.service.WalletService;
import pl.mkjb.exchange.util.MessageConstant;

import javax.validation.Valid;
import java.util.UUID;

import static pl.mkjb.exchange.util.TransactionTypeConstant.SELL;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionSellController {
    private static final String MESSAGE_SUCCESS = MessageConstant.MESSAGE_SUCCESS.name();
    private static final String MESSAGE_FAILED = MessageConstant.MESSAGE_FAILED.name();
    private static final String VIEW_NAME = "sell";
    private static final String MODEL_NAME = "transactionModel";
    private static final String REDIRECT_URL = "redirect:/dashboard";
    private final TransactionFacadeService transactionFacadeService;
    private final CurrencyService currencyService;
    private final WalletService walletService;

    @GetMapping("/sell/{currencyRateId}")
    public String showBuyForm(@PathVariable UUID currencyRateId,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal CustomUser customUser) {

        if (currencyService.isArchivedCurrencyRate(currencyRateId)) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Given currency rate has been archived");
            return REDIRECT_URL;
        }

        if (walletService.hasInsufficientFundsForSellCurrency(currencyRateId, customUser)) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Insufficient funds");
            return REDIRECT_URL;
        }

        model.addAttribute(MODEL_NAME,
                transactionFacadeService.getTransactionModel()
                        .apply(SELL)
                        .apply(currencyRateId, customUser));

        return VIEW_NAME;
    }

    @PostMapping("/sell")
    public String buyCurrency(@Valid TransactionModel transactionModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal CustomUser customUser) {

        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }

        if (currencyService.isArchivedCurrencyRate(transactionModel.getCurrencyRateId())) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Transaction failed! Given currency rate has been archived. Try again.");
            return REDIRECT_URL;
        }

        transactionFacadeService.saveTransaction()
                .apply(SELL)
                .accept(transactionModel, customUser);

        redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS,
                String.format("Success! You sold %s %s", transactionModel.getTransactionAmount(), transactionModel.getCurrencyCode()));
        return REDIRECT_URL;
    }
}
