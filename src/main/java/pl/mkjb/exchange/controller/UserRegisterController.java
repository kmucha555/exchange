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

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserRegisterController {
    private static final String VIEW_NAME = "user-registration";
    private static final String REDIRECT_URL = "redirect:/";
    private static final String MODEL_NAME = "userModel";

    @GetMapping
    public ModelAndView show() {
        return new ModelAndView(VIEW_NAME,
                Map.of(MODEL_NAME, UserModel.of()));
    }

    @PostMapping
    public String save(@Valid UserModel userModel,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
//        if (userService.isGivenUserNameAlreadyUsed(userModel)) {
//            bindingResult.rejectValue(
//                    "userName", "error.user",
//                    "Użytkownik o takim emailu jest już zarejestrowany"
//            );
//        }
        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }
//        userService.save(userModel, ROLE_USER);
//        redirectAttributes.addFlashAttribute(MESSAGE, "Użytkownik został zarejestrowany. Na adres e-mail wysłano link aktywacyjny");
        return REDIRECT_URL;
    }
}

