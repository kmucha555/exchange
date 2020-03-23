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
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.exception.RestTemplateResponseErrorHandler;
import pl.mkjb.exchange.model.CurrencyRatesModel;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class FutureProcessingRestClient implements RestClient {
    private final AtomicBoolean activeConnection = new AtomicBoolean(false);
    private final RestTemplate restTemplate;

    @Value("${pl.mkjb.exchange.restclient.api.url}")
    private String currencyRatesUrl;

    @Override
    public Option<CurrencyRatesModel> getCurrenciesRates() {
        try {

            this.restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
            final ResponseEntity<CurrencyRatesModel> currencyRatesModelResponseEntity =
                    restTemplate.getForEntity(currencyRatesUrl, CurrencyRatesModel.class);
            activeConnection.set(true);

            return Option.of(currencyRatesModelResponseEntity)
                    .map(HttpEntity::getBody)
                    .peek(currencyRatesModel -> log.info("Rest Client response body {}", currencyRatesModel));

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
