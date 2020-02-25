package pl.mkjb.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.mkjb.exchange.model.UserModel;
import pl.mkjb.exchange.service.UserService;
import pl.mkjb.exchange.util.Constant;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserRegisterController {
    private static final String VIEW_NAME = "user-registration";
    private static final String REDIRECT_URL = "redirect:/";
    private static final String MODEL_NAME = "userModel";
    private static final String MESSAGE = Constant.MESSAGE_SUCCESS.name();
    private final UserService userService;

    @GetMapping
    public ModelAndView show() {
        return new ModelAndView(VIEW_NAME,
                Map.of(MODEL_NAME, UserModel.of()));
    }

    @PostMapping
    public String save(@Valid UserModel userModel,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (userService.isGivenUserNameAlreadyUsed(userModel)) {
            bindingResult.rejectValue(
                    "userName", "error.user",
                    "Username already used"
            );
        }
        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }
        userService.save(userModel);
        redirectAttributes.addFlashAttribute(MESSAGE, "User has been registered");
        return REDIRECT_URL;
    }
}
