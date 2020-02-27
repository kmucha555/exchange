package pl.mkjb.exchange.repository;

import io.vavr.collection.Set;
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

    Set<CurrencyRateEntity> findByActiveTrue();

    @Transactional
    @Modifying
    @Query("update CurrencyRateEntity cr set cr.active = false where cr.active = true")
    void archiveCurrencyRates();
}
