package pl.mkjb.exchange.currency.domain;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

//@Repository
interface CurrencyRateRepository extends Repository<CurrencyRateEntity, UUID> {
    Optional<CurrencyRateEntity> findById(UUID id);

    Set<CurrencyRateEntity> saveAll(Iterable<CurrencyRateEntity> currencyRateEntities);

    Option<Long> countByPublicationDate(LocalDateTime publicationDate);

    @Query("select cr from CurrencyRateEntity cr where cr.active = true and cr.currencyEntity.billingCurrency = false")
    io.vavr.collection.Set<CurrencyRateEntity> findByActiveTrue();

    @Transactional
    @Modifying
    @Query(value = "UPDATE currency_rates cr JOIN currencies c ON cr.currency_id = c.id " +
            "SET cr.active = false WHERE cr.active = true and c.billing_currency = false",
            nativeQuery = true)
    void archiveCurrencyRates();

    Option<CurrencyRateEntity> findByCurrencyEntityBillingCurrencyIsTrue();
}
