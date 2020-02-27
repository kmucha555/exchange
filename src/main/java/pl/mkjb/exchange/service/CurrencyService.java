package pl.mkjb.exchange.service;

import io.vavr.Tuple;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.repository.CurrencyRateRepository;
import pl.mkjb.exchange.repository.CurrencyRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyRatesModel getNewestRates() {
        final Set<CurrencyRateEntity> newestRates = currencyRateRepository.findFirst6ByOrderByPublicationDateDesc();
        return newestRates
                .map(currencyRate -> Tuple.of(currencyRate.getPublicationDate(), newestRates))
                .map(tuple2 -> tuple2.map2(newestRate ->
                        newestRate.map(CurrencyModel::buildCurrencyModel)
                                .toJavaSet()))
                .map(tuple2 -> CurrencyRatesModel.of(tuple2._1(), tuple2._2()))
                .getOrElseThrow(() -> new BadResourceException(CurrencyService.class, "No currency rates available."));
    }

    public CurrencyModel findCurrency(UUID id) {
        return currencyRateRepository.findById(id)
                .map(CurrencyModel::buildCurrencyModel)
                .orElseThrow(() -> new BadResourceException("Given currency rate id is invalid: " + id));
    }

    public CurrencyEntity findBaseCurrency() {
        return currencyRepository.findByBaseCurrencyIsTrue()
                .getOrElseThrow(() -> new BadResourceException("No base currency found"));
    }

    public boolean isValidCurrencyRateId(UUID currencyId) {
        return Option.ofOptional(currencyRateRepository.findById(currencyId))
                .isDefined();
    }
}
