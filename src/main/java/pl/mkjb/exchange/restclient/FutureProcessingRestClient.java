package pl.mkjb.exchange.restclient;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.exception.RestTemplateResponseErrorHandler;
import pl.mkjb.exchange.model.CurrencyRatesModel;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class FutureProcessingRestClient implements RestClient {
    private final AtomicBoolean activeConnection = new AtomicBoolean(false);
    private static final Duration TIMEOUT = Duration.ofSeconds(1L);
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${currency.rates.url}")
    private String currencyRatesUrl;

    @Override
    public CurrencyRatesModel getCurrenciesRates() {
        final RestTemplate restTemplate =
                restTemplateBuilder
                        .setConnectTimeout(TIMEOUT)
                        .setReadTimeout(TIMEOUT)
                        .errorHandler(new RestTemplateResponseErrorHandler())
                        .build();

        try {
            final ResponseEntity<CurrencyRatesModel> currencyRatesModelResponseEntity = restTemplate.getForEntity(currencyRatesUrl, CurrencyRatesModel.class);
            activeConnection.set(true);
            return Option.of(currencyRatesModelResponseEntity)
                    .map(HttpEntity::getBody)
                    .peek(currencyRatesModel -> log.info("Rest Client response body {}", currencyRatesModel))
                    .getOrElseThrow(() -> new BadResourceException("Invalid JSON response " + currencyRatesModelResponseEntity));
        } catch (RestClientException e) {
            activeConnection.set(false);
            log.error("Exception during currency rates fetch. Message: {}", e.getMessage());
            throw new BadResourceException("Error during currencies rates fetch");
        }
    }

    @Override
    public boolean isConnectionAlive() {
        log.info("Is Future Processing API alive: {}", activeConnection.get());
        return activeConnection.get();
    }
}
