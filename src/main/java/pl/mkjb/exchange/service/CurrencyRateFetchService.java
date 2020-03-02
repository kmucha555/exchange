package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.repository.CurrencyRateRepository;
import pl.mkjb.exchange.repository.CurrencyRepository;
import pl.mkjb.exchange.restclient.RestClient;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateFetchService {
    private final RestClient futureProcessingRestClient;
    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyRepository currencyRepository;

    @Scheduled(fixedRateString = "${pl.mkjb.exchange.service.CurrencyRateFetchService.fixedDelay.in.milliseconds}")
    public void updateCurrenciesRates() {
        futureProcessingRestClient.getCurrenciesRates()
                .filter(this::hasNewCurrencyRatesBundleBeenPublished)
                .peek(currenciesRates -> currencyRateRepository.archiveCurrencyRates())
                .map(this::buildCurrenciesRatesEntities)
                .map(currencyRateRepository::saveAll)
                .peek(currenciesRates -> log.info("New exchange rates available. Saving to database: {}", currenciesRates))
                .onEmpty(() -> log.info("No new exchange rates has been published"));
    }

    private boolean hasNewCurrencyRatesBundleBeenPublished(CurrencyRatesModel currencyRatesModel) {
        return currencyRateRepository.countByPublicationDate(currencyRatesModel.getPublicationDate()) == 0;
    }

    private Set<CurrencyRateEntity> buildCurrenciesRatesEntities(CurrencyRatesModel currenciesRates) {
        return currenciesRates.getItems()
                .stream()
                .map(currencyModel -> CurrencyRateEntity.builder()
                        .currencyEntity(getCurrencyEntity(currencyModel))
                        .averagePrice(currencyModel.getAveragePrice())
                        .purchasePrice(currencyModel.getPurchasePrice())
                        .sellPrice(currencyModel.getSellPrice())
                        .publicationDate(currenciesRates.getPublicationDate())
                        .build())
                .collect(Collectors.toUnmodifiableSet());
    }

    private CurrencyEntity getCurrencyEntity(CurrencyModel currencyModel) {
        return currencyRepository.findByCode(currencyModel.getCode())
                .getOrElse(() -> {
                    log.error("Missing currency code {}", currencyModel);
                    throw new IllegalArgumentException("Missing currency code");
                });
    }
}
