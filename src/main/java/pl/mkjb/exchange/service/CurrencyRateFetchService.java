package pl.mkjb.exchange.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.Currency;
import pl.mkjb.exchange.model.CurrencyRates;
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

    @Scheduled(fixedRate = 5000)
    private void updateCurrenciesRates() {
        final CurrencyRates currenciesRates = futureProcessingRestClient.getCurrenciesRates();
        if (isNewCurrencyRateAvailable(currenciesRates)) {
            currencyRateRepository.saveAll(buildCurrencyRateEntity(currenciesRates));
            log.info("New exchange rates available. Saving to database.");
        }
    }

    private Set<CurrencyRateEntity> buildCurrencyRateEntity(CurrencyRates currenciesRates) {
        return currenciesRates.getItems()
                .stream()
                .map(currency -> CurrencyRateEntity.builder()
                        .currencyEntity(getCurrencyEntity(currency))
                        .averagePrice(currency.getAveragePrice())
                        .purchasePrice(currency.getPurchasePrice())
                        .sellPrice(currency.getSellPrice())
                        .publicationDate(currenciesRates.getPublicationDate())
                        .build())
                .collect(Collectors.toUnmodifiableSet());
    }

    private CurrencyEntity getCurrencyEntity(Currency currency) {
        return currencyRepository.findByCode(currency.getCode())
                .getOrElse(() -> {
                    log.error("Missing currency code {}", currency);
                    throw new IllegalArgumentException("Missing currency code");
                });
    }

    private boolean isNewCurrencyRateAvailable(CurrencyRates currencyRates) {
        final LocalDateTime publicationDate = currencyRates.getPublicationDate();
        return currencyRateRepository.countByPublicationDate(publicationDate) == 0;
    }
}