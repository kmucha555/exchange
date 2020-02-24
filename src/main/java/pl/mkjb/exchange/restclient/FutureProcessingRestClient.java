package pl.mkjb.exchange.restclient;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.mkjb.exchange.model.CurrencyRatesModel;

@Log4j2
@Component
@RequiredArgsConstructor
public class FutureProcessingRestClient implements RestClient {
    private final RestTemplate restTemplate;

    @Value("${currency.rates.url}")
    private String currencyRatesUrl;

    @Override
    public CurrencyRatesModel getCurrenciesRates() {
        final ResponseEntity<CurrencyRatesModel> currencyRatesResponseEntity = restTemplate.getForEntity(currencyRatesUrl, CurrencyRatesModel.class);
        return Option.of(currencyRatesResponseEntity)
                .filter(this::isValidResponse)
                .peek(log::info)
                .map(HttpEntity::getBody)
                .peek(log::info)
                .getOrElseThrow(() -> new IllegalStateException("Error during currencies rates fetch."));
    }

    private boolean isValidResponse(ResponseEntity<CurrencyRatesModel> currencyRatesResponseEntity) {
        return !currencyRatesResponseEntity.getStatusCode().isError();
    }
}
