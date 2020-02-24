package pl.mkjb.exchange.restclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.mkjb.exchange.model.CurrencyBundle;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClient {
    private final RestTemplate restTemplate;

    @Value("${currency.rates.url}")
    private String currencyRatesUrl;

    public ResponseEntity<CurrencyBundle> getCurrenciesRates() {
        return restTemplate.getForEntity(currencyRatesUrl, CurrencyBundle.class);
    }
}
