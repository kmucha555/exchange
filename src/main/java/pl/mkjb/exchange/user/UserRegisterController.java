package pl.mkjb.exchange.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.mkjb.exchange.infrastructure.util.MessageConstant;
import pl.mkjb.exchange.user.domain.UserService;
import pl.mkjb.exchange.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class UserRegisterController {
    @Value("${pl.mkjb.exchange.controller.UserExists.message}")
    private String userExistsMessage;

    private static final String VIEW_NAME = "user-registration";
    private static final String REDIRECT_URL = "redirect:/";
    private static final String MODEL_NAME = "userModel";
    private static final String MESSAGE = MessageConstant.MESSAGE_SUCCESS.name();
    private final UserService userService;

    @GetMapping
    public ModelAndView show() {
        return new ModelAndView(VIEW_NAME,
                Map.of(MODEL_NAME, new UserDto()));
    }

    @PostMapping
    public String save(@Valid UserDto userDto,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if (userService.isGivenUserNameAlreadyUsed(userDto)) {
            bindingResult.rejectValue(
                    "userName", "error.user",
                    userExistsMessage
            );
        }
        if (bindingResult.hasErrors()) {
            return VIEW_NAME;
        }

        userService.save(userDto);
        redirectAttributes.addFlashAttribute(MESSAGE, "Successfully registered");
        return REDIRECT_URL;
    }
}
