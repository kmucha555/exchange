package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class InMemoryCurrencyRateRepository implements CurrencyRateRepository {
    private final Map<UUID, CurrencyRateEntity> currencyRates = HashMap.empty();

    @Override
    public Optional<CurrencyRateEntity> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Set<CurrencyRateEntity> saveAll(Iterable<CurrencyRateEntity> currencyRateEntities) {
        return null;
    }

    @Override
    public Option<Long> countByPublicationDate(LocalDateTime publicationDate) {
        return null;
    }

    @Override
    public io.vavr.collection.Set<CurrencyRateEntity> findByActiveTrue() {
        return null;
    }

    @Override
    public void archiveCurrencyRates() {

    }

    @Override
    public Option<CurrencyRateEntity> findByCurrencyEntityBillingCurrencyIsTrue() {
        return null;
    }
}
