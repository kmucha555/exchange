package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingBundle;

@Slf4j
@RequiredArgsConstructor
class CurrencyRateCreator {
    private final CurrencyRepository currencyRepository;

    public CurrencyRateEntity from(CurrencyRateDto currencyRateDto) {
        return CurrencyRateEntity.builder()
                .currencyEntity(findCurrencyByCode(currencyRateDto.getCurrencyDto().getCode()))
                .averagePrice(currencyRateDto.getAveragePrice())
                .purchasePrice(currencyRateDto.getPurchasePrice())
                .sellPrice(currencyRateDto.getSellPrice())
                .active(Boolean.TRUE)
                .publicationDate(currencyRateDto.getPublicationDate())
                .build();
    }

    public Set<CurrencyRateEntity> from(CurrencyFutureProcessingBundle currencyFutureProcessingBundle) {
        return HashSet.ofAll(currencyFutureProcessingBundle.getItems())
                .map(currency ->
                        CurrencyRateEntity.builder()
                                .currencyEntity(findCurrencyByCode(currency.getCode()))
                                .averagePrice(currency.getAveragePrice())
                                .purchasePrice(currency.getPurchasePrice())
                                .sellPrice(currency.getSellPrice())
                                .active(Boolean.TRUE)
                                .publicationDate(currencyFutureProcessingBundle.getPublicationDate())
                                .build())
                .toSet();
    }

    private CurrencyEntity findCurrencyByCode(String code) {
        return currencyRepository.findByCode(code)
                .getOrElse(() -> {
                    log.error("Missing currency with code {}", code);
                    throw new CurrencyNotFoundException("Missing currency with code " + code);
                });
    }
}
