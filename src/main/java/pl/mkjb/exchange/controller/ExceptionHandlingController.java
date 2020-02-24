package pl.mkjb.exchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.mkjb.exchange.exception.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {
    private static final String LOG_MESSAGE = "Request: {} raised {}";

    @ExceptionHandler({ResourceNotFoundException.class, IllegalArgumentException.class,
            UsernameNotFoundException.class})
    public String handleException(HttpServletRequest request, Exception exception) {
        log.error(LOG_MESSAGE, request.getRequestURL(), exception);
        return "errors/404";
    }
}