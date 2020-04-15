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

        val plnUUID = UUID.fromString("f7086c16-87bc-4a02-9fe5-146a9f4b6bbe");
        val usdUUID = UUID.fromString("db64b6bc-f5e4-4eb8-9d53-5529f0691896");
        val czkUUID = UUID.fromString("d1d1570a-f705-41ef-98d9-fb5c1a01c71d");

        val plnName = "Polish Zloty";
        val plnCode = "PLN";
        val plnUnit = BigDecimal.ONE;

        val usdName = "US Dollar";
        val usdCode = "USD";
        val usdUnit = BigDecimal.ONE;
        val usdPurchasePrice = BigDecimal.valueOf(4d);
        val usdSellPrice = BigDecimal.valueOf(4.5d);

        val czkName = "Czech koruna";
        val czkCode = "CZK";
        val czkUnit = BigDecimal.valueOf(100);
        val czkPurchasePrice = BigDecimal.valueOf(14d);
        val czkSellPrice = BigDecimal.valueOf(14.5d);

        val pln = CurrencyEntity.builder()
                .id(1)
                .name(plnName)
                .code(plnCode)
                .unit(plnUnit)
                .billingCurrency(true)
                .build();

        val currencyRatePLN = CurrencyRateEntity.builder()
                .id(plnUUID)
                .currencyEntity(pln)
                .purchasePrice(BigDecimal.ONE)
                .sellPrice(BigDecimal.ONE)
                .active(true)
                .publicationDate(publicationDate)
                .build();

        val usd = CurrencyEntity.builder()
                .id(2)
                .name(usdName)
                .code(usdCode)
                .unit(usdUnit)
                .billingCurrency(false)
                .build();

        val currencyRateUSD = CurrencyRateEntity.builder()
                .id(usdUUID)
                .currencyEntity(usd)
                .purchasePrice(usdPurchasePrice)
                .sellPrice(usdSellPrice)
                .active(true)
                .publicationDate(publicationDate)
                .build();

        val czk = CurrencyEntity.builder()
                .id(3)
                .name(czkName)
                .code(czkCode)
                .unit(czkUnit)
                .billingCurrency(false)
                .build();

        val currencyRateCZK = CurrencyRateEntity.builder()
                .id(czkUUID)
                .currencyEntity(czk)
                .purchasePrice(czkPurchasePrice)
                .sellPrice(czkSellPrice)
                .active(true)
                .publicationDate(publicationDate)
                .build();

        currencyRates = HashMap.of(
                plnUUID, currencyRatePLN,
                usdUUID, currencyRateUSD,
                czkUUID, currencyRateCZK
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
    public int countByPublicationDate(LocalDateTime publicationDate) {
        return currencyRates.values()
                .count(currencyRate -> currencyRate.getPublicationDate().isEqual(publicationDate));
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
