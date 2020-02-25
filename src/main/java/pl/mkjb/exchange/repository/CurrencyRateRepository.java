package pl.mkjb.exchange.repository;

import io.vavr.collection.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.CurrencyRateEntity;

import java.time.LocalDateTime;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {
    Long countByPublicationDate(LocalDateTime publicationDate);

    Set<CurrencyRateEntity> findFirst6ByOrderByPublicationDateDesc();
}
