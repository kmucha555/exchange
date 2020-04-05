package pl.mkjb.exchange.transaction;

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
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.infrastructure.security.CustomUser;
import pl.mkjb.exchange.infrastructure.util.MessageConstant;
import pl.mkjb.exchange.transaction.domain.TransactionFacade;
import pl.mkjb.exchange.transaction.dto.TransactionDto;
import pl.mkjb.exchange.wallet.domain.WalletFacade;

import javax.validation.Valid;
import java.util.UUID;

import static pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant.SELL;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
class TransactionSellController {
    private static final String MESSAGE_SUCCESS = MessageConstant.MESSAGE_SUCCESS.name();
    private static final String MESSAGE_FAILED = MessageConstant.MESSAGE_FAILED.name();
    private static final String VIEW_NAME = "sell";
    private static final String MODEL_NAME = "transactionDto";
    private static final String REDIRECT_URL = "redirect:/dashboard";
    private final TransactionFacade transactionFacade;
    private final CurrencyFacade currencyFacade;
    private final WalletFacade walletFacade;

    @GetMapping("/sell/{currencyRateId}")
    public String showBuyForm(@PathVariable UUID currencyRateId,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal CustomUser customUser) {

        if (currencyFacade.isArchivedCurrencyRate(currencyRateId)) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Given currency rate has been archived");
            return REDIRECT_URL;
        }

        if (walletFacade.hasInsufficientFundsToSellCurrency(currencyRateId, customUser)) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Insufficient funds");
            return REDIRECT_URL;
        }

        model.addAttribute(MODEL_NAME,
                transactionFacade.getTransactionDto()
                        .apply(SELL)
                        .apply(currencyRateId, customUser));

        return VIEW_NAME;
    }

    @PostMapping("/sell")
    public String buyCurrency(@Valid TransactionDto transactionDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal CustomUser customUser) {

        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }

        if (currencyFacade.isArchivedCurrencyRate(transactionDto.getCurrencyRateId())) {
            redirectAttributes.addFlashAttribute(MESSAGE_FAILED, "Transaction failed! Given currency rate has been archived. Try again.");
            return REDIRECT_URL;
        }

        transactionFacade.saveTransaction()
                .apply(SELL)
                .accept(transactionDto, customUser);

        redirectAttributes.addFlashAttribute(MESSAGE_SUCCESS,
                String.format("Success! You sold %s %s", transactionDto.getTransactionAmount(), transactionDto.getCurrencyCode()));
        return REDIRECT_URL;
    }
}
