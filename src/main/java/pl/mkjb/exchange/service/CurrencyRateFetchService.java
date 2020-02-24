package pl.mkjb.exchange.service;

import lombok.AccessLevel;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CurrencyRateFetchService {
    private final RestClient futureProcessingRestClient;
    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyRepository currencyRepository;

    @Scheduled(fixedRate = 50000)
    private void updateCurrenciesRates() {
        final CurrencyRatesModel currenciesRates = futureProcessingRestClient.getCurrenciesRates();
        if (isNewCurrencyRateAvailable(currenciesRates)) {
            currencyRateRepository.saveAll(buildCurrencyRateEntity(currenciesRates));
            log.info("New exchange rates available. Saving to database.");
        }
    }

    private Set<CurrencyRateEntity> buildCurrencyRateEntity(CurrencyRatesModel currenciesRates) {
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

    private boolean isNewCurrencyRateAvailable(CurrencyRatesModel currencyRatesModel) {
        final LocalDateTime publicationDate = currencyRatesModel.getPublicationDate();
        return currencyRateRepository.countByPublicationDate(publicationDate) == 0;
    }
}
