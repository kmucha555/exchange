package pl.mkjb.exchange.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Given resource not found")
public class BadResourceException extends RuntimeException {
    public <T, V> BadResourceException(Class<T> placeOfException, V givenValue) {
        super("Given value " + givenValue + " caused exception in " + placeOfException.getName());
    }

    public <T, V> BadResourceException(String message) {
        super("Message: " + message);
    }
}