package pl.mkjb.exchange.repository;

import io.vavr.collection.Set;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.CurrencyRateEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface CurrencyRateRepository extends CrudRepository<CurrencyRateEntity, UUID> {
    Long countByPublicationDate(LocalDateTime publicationDate);

    Set<CurrencyRateEntity> findFirst6ByOrderByPublicationDateDesc();
}
