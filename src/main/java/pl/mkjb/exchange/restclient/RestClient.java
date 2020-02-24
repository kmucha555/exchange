package pl.mkjb.exchange.restclient;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.mkjb.exchange.model.CurrencyRates;

@Log4j2
@Component
@RequiredArgsConstructor
public class RestClient {
    private final RestTemplate restTemplate;

    @Value("${currency.rates.url}")
    private String currencyRatesUrl;

    public CurrencyRates getCurrenciesRates() {
        final ResponseEntity<CurrencyRates> currencyBundleResponse = restTemplate.getForEntity(currencyRatesUrl, CurrencyRates.class);
        return Option.of(currencyBundleResponse)
                .filter(currencyBundleResponseEntity -> !currencyBundleResponse.getStatusCode().isError())
                .peek(log::info)
                .map(HttpEntity::getBody)
                .peek(log::info)
                .getOrElse(() -> {
                    log.error("Error {} during currencies rates fetch. Reason: {}",
                            currencyBundleResponse.getStatusCode(),
                            currencyBundleResponse.getStatusCode().getReasonPhrase());
                    throw new IllegalStateException("Error during currencies rates fetch.");
                });
    }
}
