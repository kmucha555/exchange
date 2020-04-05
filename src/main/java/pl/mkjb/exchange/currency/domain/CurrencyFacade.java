package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.mkjb.exchange.currency.dto.CurrencyDto;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingBundle;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CurrencyFacade {
    private final CurrencyRateCreator currencyRateCreator;
    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    public Set<CurrencyDto> findAll() {
        return HashSet.ofAll(currencyRepository.findAll())
                .map(CurrencyEntity::toDto)
                .toSet();
    }

    public Option<CurrencyRateDto> findBillingCurrency() {
        return currencyRateRepository.findByCurrencyEntityBillingCurrencyIsTrue()
                .map(CurrencyRateEntity::toDto);
    }

    public Option<CurrencyRateDto> findCurrencyRateByCurrencyRateId(UUID id) {
        return Option.ofOptional(currencyRateRepository.findById(id))
                .map(CurrencyRateEntity::toDto);
    }

    public Set<CurrencyRateDto> getNewestCurrencyRates() {
        return currencyRateRepository.findByActiveTrue()
                .map(CurrencyRateEntity::toDto)
                .toSet();
    }

    public boolean isArchivedCurrencyRate(UUID id) {
        return !currencyRateRepository.findById(id)
                .map(CurrencyRateEntity::getActive)
                .orElseThrow(() -> new CurrencyNotFoundException(id));
    }

    public Option<Iterable<CurrencyRateEntity>> processNewCurrencyRates(CurrencyFutureProcessingBundle currencyFutureProcessingBundle) {
        return currencyRateRepository.countByPublicationDate(currencyFutureProcessingBundle.getPublicationDate())
                .filter(count -> count == 0)
                .peek(count -> currencyRateRepository.archiveCurrencyRates())
                .map(count -> currencyRateCreator.from(currencyFutureProcessingBundle))
                .map(currencyRateRepository::saveAll);
    }

    public CurrencyRateEntity from(CurrencyRateDto currencyRateDto) {
        return currencyRateCreator.from(currencyRateDto);
    }
}
