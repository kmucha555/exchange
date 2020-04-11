package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import lombok.val;

import java.math.BigDecimal;
import java.util.Set;

class InMemoryCurrencyRepository implements CurrencyRepository {
    private Map<Integer, CurrencyEntity> currencies;

    {
        val pln = CurrencyEntity.builder()
                .id(1)
                .name("Polish Zloty")
                .code("PLN")
                .unit(BigDecimal.ONE)
                .billingCurrency(true)
                .build();

        val usd = CurrencyEntity.builder()
                .id(2)
                .name("US Dollar")
                .code("USD")
                .unit(BigDecimal.ONE)
                .billingCurrency(false)
                .build();

        val czk = CurrencyEntity.builder()
                .id(3)
                .name("Czech koruna")
                .code("CZK")
                .unit(BigDecimal.valueOf(100))
                .billingCurrency(false)
                .build();

        currencies = HashMap.of(
                1, pln,
                2, usd,
                3, czk
        );
    }

    @Override
    public Set<CurrencyEntity> findAll() {
        return currencies.values().toJavaSet();
    }

    @Override
    public Option<CurrencyEntity> findByCode(String currencyCode) {
        return currencies.values().find(currency -> currency.getCode().equals(currencyCode));
    }
}
