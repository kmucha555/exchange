package pl.mkjb.exchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {
    @GetMapping
    public String handleError(HttpServletRequest request) {
        final int statusCode = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(HttpStatus.NOT_FOUND.value());
        log.error("Error {} in {}", statusCode, CustomErrorController.class);

        if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return "errors/500";
        }
        return "errors/404";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
