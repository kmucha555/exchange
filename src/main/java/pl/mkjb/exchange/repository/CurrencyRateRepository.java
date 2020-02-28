package pl.mkjb.exchange.repository;

import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.CurrencyRateEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface CurrencyRateRepository extends CrudRepository<CurrencyRateEntity, UUID> {
    Long countByPublicationDate(LocalDateTime publicationDate);

    @Query("select cr from CurrencyRateEntity cr where cr.active = true and cr.currencyEntity.baseCurrency = false")
    Set<CurrencyRateEntity> findByActiveTrue();

    @Transactional
    @Modifying
    @Query(value = "UPDATE currency_rates cr JOIN currencies c ON cr.currency_id = c.id " +
            "SET cr.active = false WHERE cr.active = true and c.base_currency = false",
            nativeQuery = true)
    void archiveCurrencyRates();

    Option<CurrencyRateEntity> findByCurrencyEntityBaseCurrencyIsTrue();
}
