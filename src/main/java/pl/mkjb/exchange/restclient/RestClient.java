package pl.mkjb.exchange.restclient;

import pl.mkjb.exchange.model.CurrencyRatesModel;

public interface RestClient {
    CurrencyRatesModel getCurrenciesRates();
    boolean isConnectionAlive();
}
