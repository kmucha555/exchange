package pl.mkjb.exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.CurrencyRateEntity;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {
}
