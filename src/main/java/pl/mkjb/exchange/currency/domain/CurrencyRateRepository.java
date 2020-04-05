package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
interface CurrencyRateRepository extends CrudRepository<CurrencyRateEntity, UUID> {
    Option<Long> countByPublicationDate(LocalDateTime publicationDate);

    @Query("select cr from CurrencyRateEntity cr where cr.active = true and cr.currencyEntity.billingCurrency = false")
    Set<CurrencyRateEntity> findByActiveTrue();

    @Transactional
    @Modifying
    @Query(value = "UPDATE currency_rates cr JOIN currencies c ON cr.currency_id = c.id " +
            "SET cr.active = false WHERE cr.active = true and c.billing_currency = false",
            nativeQuery = true)
    void archiveCurrencyRates();

    Option<CurrencyRateEntity> findByCurrencyEntityBillingCurrencyIsTrue();
}
