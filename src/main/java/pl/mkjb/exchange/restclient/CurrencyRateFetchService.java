package pl.mkjb.exchange.restclient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;

@Service
@Slf4j
@RequiredArgsConstructor
class CurrencyRateFetchService {
    private final RestClient futureProcessingRestClient;
    private final CurrencyFacade currencyFacade;

    @Scheduled(fixedRateString = "${pl.mkjb.exchange.restclient.dto.CurrencyRateFetchService.fixedDelay.in.milliseconds}")
    public void updateCurrenciesRates() {
        futureProcessingRestClient.getCurrenciesRates()
                .map(currencyFacade::processNewCurrencyRates)
                .peek(currencyRates -> log.info("New currency rates has been published."))
                .peek(currencyRates -> log.info("Following currency rates has been saved: {}", currencyRates))
                .onEmpty(() -> log.info("No new currency rates has been published since last fetch."));
    }
}
