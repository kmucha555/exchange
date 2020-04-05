package pl.mkjb.exchange.currency.domain;

import io.vavr.control.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CurrencyRepository extends JpaRepository<CurrencyEntity, Integer> {
    Option<CurrencyEntity> findByCode(String currencyCode);
}
