package pl.mkjb.exchange.restclient;

import pl.mkjb.exchange.model.CurrencyRates;

public interface RestClient {
    CurrencyRates getCurrenciesRates();
}
