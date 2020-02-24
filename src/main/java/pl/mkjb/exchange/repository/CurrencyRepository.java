package pl.mkjb.exchange.repository;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Integer> {
    Option<CurrencyEntity> findByCode(String currencyCode);
}
