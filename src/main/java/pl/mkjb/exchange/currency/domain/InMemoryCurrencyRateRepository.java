package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import lombok.val;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

class InMemoryCurrencyRateRepository implements CurrencyRateRepository {
    private Map<UUID, CurrencyRateEntity> currencyRates;

    {
        val publicationDate = LocalDateTime.of(2020, 4, 11, 14, 3, 1);
        val PLN_UUID = "f7086c16-87bc-4a02-9fe5-146a9f4b6bbe";
        val USD_UUID = "db64b6bc-f5e4-4eb8-9d53-5529f0691896";
        val CZK_UUID = "d1d1570a-f705-41ef-98d9-fb5c1a01c71d";

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

        val currencyRatePLN = CurrencyRateEntity.builder()
                .id(UUID.fromString(PLN_UUID))
                .currencyEntity(pln)
                .purchasePrice(BigDecimal.ONE)
                .sellPrice(BigDecimal.ONE)
                .active(true)
                .publicationDate(publicationDate)
                .build();

        val currencyRateUSD = CurrencyRateEntity.builder()
                .id(UUID.fromString(USD_UUID))
                .currencyEntity(usd)
                .purchasePrice(BigDecimal.valueOf(3.9598d))
                .sellPrice(BigDecimal.valueOf(3.9895d))
                .active(true)
                .publicationDate(publicationDate)
                .build();

        val currencyRateCZK = CurrencyRateEntity.builder()
                .id(UUID.fromString(CZK_UUID))
                .currencyEntity(czk)
                .purchasePrice(BigDecimal.valueOf(14.5678d))
                .sellPrice(BigDecimal.valueOf(14.7890d))
                .active(true)
                .publicationDate(publicationDate)
                .build();

        currencyRates = HashMap.of(
                UUID.fromString(PLN_UUID), currencyRatePLN,
                UUID.fromString(USD_UUID), currencyRateUSD,
                UUID.fromString(CZK_UUID), currencyRateCZK
        );
    }


    @Override
    public Optional<CurrencyRateEntity> findById(UUID id) {
        return Optional.ofNullable(currencyRates.getOrElse(id, null));
    }

    @Override
    public Set<CurrencyRateEntity> saveAll(Iterable<CurrencyRateEntity> currencyRateEntities) {
        currencyRateEntities.forEach(currencyRate -> currencyRates.put(currencyRate.getId(), currencyRate));
        return Stream.ofAll(currencyRateEntities).toJavaSet();
    }

    @Override
    public Option<Long> countByPublicationDate(LocalDateTime publicationDate) {
        return Option.of(currencyRates.values()
                .count(currencyRate -> currencyRate.getPublicationDate().isEqual(publicationDate)))
                .map(Long::valueOf);
    }

    @Override
    public io.vavr.collection.Set<CurrencyRateEntity> findByActiveTrue() {
        return currencyRates.values()
                .filter(currencyRate -> currencyRate.getActive().equals(Boolean.TRUE))
                .filter(currencyRate -> currencyRate.getCurrencyEntity().getBillingCurrency().equals(Boolean.FALSE))
                .toSet();
    }

    @Override
    public void archiveCurrencyRates() {
    }

    @Override
    public Option<CurrencyRateEntity> findByCurrencyEntityBillingCurrencyIsTrue() {
        return currencyRates.values()
                .find(currencyRate -> currencyRate.getCurrencyEntity().getBillingCurrency().equals(Boolean.TRUE));
    }
}
