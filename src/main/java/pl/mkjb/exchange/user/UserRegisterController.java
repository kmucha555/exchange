package pl.mkjb.exchange.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.infrastructure.util.MessageConstant;
import pl.mkjb.exchange.transaction.domain.TransactionFacade;
import pl.mkjb.exchange.user.domain.UserEntity;
import pl.mkjb.exchange.user.domain.UserFacade;
import pl.mkjb.exchange.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
class UserRegisterController {
    @Value("${pl.mkjb.exchange.controller.UserExists.message}")
    private String userExistsMessage;

    private static final String VIEW_NAME = "user-registration";
    private static final String REDIRECT_URL = "redirect:/";
    private static final String MODEL_NAME = "userDto";
    private static final String MESSAGE = MessageConstant.MESSAGE_SUCCESS.name();
    private final UserFacade userFacade;
    private final TransactionFacade transactionFacade;
    private final CurrencyFacade currencyFacade;

    @GetMapping
    public ModelAndView show() {
        return new ModelAndView(VIEW_NAME,
                Map.of(MODEL_NAME, new UserDto()));
    }

    @PostMapping
    public String save(@Valid UserDto userDto,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (userFacade.isUserNameAlreadyUsed(userDto)) {
            bindingResult.rejectValue(
                    "userName", "error.user",
                    userExistsMessage
            );
        }
        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }

        final UserEntity savedUser = userFacade.save(userDto);

        //Only for demo purpose
        transactionFacade.saveInitialTransactions(currencyFacade.saveInitialTransactions(savedUser));
        currencyFacade.addFundsForUserForDemonstration(savedUser)
                .map(transactionFacade::saveInitialFunds)
                .onEmpty(() -> log.error("Error while adding initial funds"));

        redirectAttributes.addFlashAttribute(MESSAGE, "Successfully registered");
        return REDIRECT_URL;
    }
}

