package pl.mkjb.exchange.infrastructure;

import java.util.UUID;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(int currencyId) {
        super("No currency of id: " + currencyId + " found");
    }

    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(UUID currencyRateId) {
        super("No currency rate of uuid: " + currencyRateId + " found");
    }
}
