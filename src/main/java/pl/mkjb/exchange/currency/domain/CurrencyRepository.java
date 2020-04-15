package pl.mkjb.exchange.currency.domain;

import io.vavr.control.Option;
import org.springframework.data.repository.Repository;

import java.util.Set;

interface CurrencyRepository extends Repository<CurrencyEntity, Integer> {

    Set<CurrencyEntity> findAll();

    Option<CurrencyEntity> findByCode(String currencyCode);
}
