package pl.mkjb.exchange.restclient;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.mkjb.exchange.infrastructure.mvc.exception.BadResourceException;
import pl.mkjb.exchange.infrastructure.mvc.exception.RestTemplateResponseErrorHandler;
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingBundle;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
class FutureProcessingRestClient implements RestClient {
    private final AtomicBoolean activeConnection = new AtomicBoolean(false);
    private final RestTemplate restTemplate;

    @Value("${pl.mkjb.exchange.restclient.api.url}")
    private String currencyRatesUrl;

    @Override
    public Option<CurrencyFutureProcessingBundle> getCurrenciesRates() {
        try {

            this.restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
            final ResponseEntity<CurrencyFutureProcessingBundle> currencyRatesModelResponseEntity =
                    restTemplate.getForEntity(currencyRatesUrl, CurrencyFutureProcessingBundle.class);
            activeConnection.set(true);

            return Option.of(currencyRatesModelResponseEntity)
                    .map(HttpEntity::getBody)
                    .peek(fetchedCurrencyRates -> log.info("Fetched currency rates {}", fetchedCurrencyRates));

        } catch (RestClientException e) {
            activeConnection.set(false);
            log.error("Exception during currency rates fetch. Message: {}", e.getMessage());
            throw new BadResourceException("Error during currencies rates fetch");
        }
    }

    @Override
    public boolean isConnectionAlive() {
        return activeConnection.get();
    }
}
