package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;

import java.util.Set;

class InMemoryCurrencyRepository implements CurrencyRepository {
    private final Map<Integer, CurrencyEntity> currencyRates = HashMap.empty();

    @Override
    public Set<CurrencyEntity> findAll() {
        return null;
    }

    @Override
    public Option<CurrencyEntity> findByCode(String currencyCode) {
        return null;
    }
}
