package pl.mkjb.exchange.restclient;

import io.vavr.control.Option;
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingBundle;

public interface RestClient {
    Option<CurrencyFutureProcessingBundle> getCurrenciesRates();

    boolean isConnectionAlive();
}
