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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.mkjb.exchange.model.TransactionBuyModel;
import pl.mkjb.exchange.security.CustomAuthenticatedUser;
import pl.mkjb.exchange.service.TransactionService;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    @Value("${pl.mkjb.exchange.controller.WrongAmount.message}")
    private String wrongAmount;

    private static final String VIEW_NAME = "buy";
    private static final String MODEL_NAME = "transactionBuyModel";
    private final TransactionService transactionService;

    @GetMapping("/buy/{currencyRateId}")
    public ModelAndView showBuyForm(@PathVariable UUID currencyRateId, @AuthenticationPrincipal CustomAuthenticatedUser authenticatedUser) {
        return new ModelAndView(VIEW_NAME, Map.of(MODEL_NAME, transactionService.getTransactionBuyModel(currencyRateId, authenticatedUser.getId())));
    }

    @PostMapping("/buy")
    public String buyCurrency(@Valid TransactionBuyModel transactionBuyModel,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model,
                              @AuthenticationPrincipal CustomAuthenticatedUser authenticatedUser) {
        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }
        if (transactionService.hasErrors(transactionBuyModel, authenticatedUser.getId())) {
            bindingResult.rejectValue("buyAmount", "error.user", wrongAmount);
            return VIEW_NAME;
        }

        return "redirect:/dashboard";
    }
}
