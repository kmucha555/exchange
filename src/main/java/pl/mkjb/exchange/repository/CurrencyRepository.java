package pl.mkjb.exchange.repository;

import io.vavr.control.Option;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.mkjb.exchange.entity.CurrencyEntity;

@Repository
public interface CurrencyRepository extends CrudRepository<CurrencyEntity, Integer> {
    Option<CurrencyEntity> findByCode(String currencyCode);
    Option<CurrencyEntity> findByBaseCurrencyIsTrue();
}
