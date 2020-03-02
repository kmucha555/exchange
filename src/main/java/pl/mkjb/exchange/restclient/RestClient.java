package pl.mkjb.exchange.restclient;

import io.vavr.control.Option;
import pl.mkjb.exchange.model.CurrencyRatesModel;

public interface RestClient {
    Option<CurrencyRatesModel> getCurrenciesRates();

    boolean isConnectionAlive();
}
