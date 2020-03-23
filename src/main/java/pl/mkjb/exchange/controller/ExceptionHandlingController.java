package pl.mkjb.exchange.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.mkjb.exchange.exception.BadResourceException;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler({BadResourceException.class, UsernameNotFoundException.class})
    public String handle4xxException(HttpServletRequest request, RuntimeException exception) {
        log.error("Request: {} raised exception. Message: {}", request.getRequestURL(), exception.getMessage());
        return "errors/404";
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public String handle5xxException(HttpServletRequest request, RuntimeException exception) {
        log.error("Request: {} raised exception. Message: {}", request.getRequestURL(), exception.getMessage());
        return "errors/500";
    }
}