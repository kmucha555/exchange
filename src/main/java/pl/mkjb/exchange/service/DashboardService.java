package pl.mkjb.exchange.service;

import io.vavr.Tuple;
import io.vavr.collection.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.repository.CurrencyRateRepository;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final CurrencyRateRepository currencyRateRepository;

    public CurrencyRatesModel findNewestRates() {
        final Set<CurrencyRateEntity> newestRates = currencyRateRepository.findFirst6ByOrderByPublicationDateDesc();
        return newestRates
                .map(currencyRate -> Tuple.of(currencyRate.getPublicationDate(), newestRates))
                .map(tuple2 -> tuple2.map2(newestRate ->
                        newestRate.map(this::buildCurrencyModel)
                                .toJavaSet()))
                .map(tuple2 -> CurrencyRatesModel.of(tuple2._1(), tuple2._2()))
                .getOrElseThrow(() -> new IllegalArgumentException("No currency rates available."));
    }

    private CurrencyModel buildCurrencyModel(CurrencyRateEntity currencyRate) {
        return CurrencyModel.builder()
                .id(currencyRate.getId())
                .name(currencyRate.getCurrencyEntity().getName())
                .code(currencyRate.getCurrencyEntity().getCode())
                .unit(currencyRate.getCurrencyEntity().getUnit())
                .purchasePrice(currencyRate.getPurchasePrice())
                .sellPrice(currencyRate.getSellPrice())
                .averagePrice(currencyRate.getAveragePrice())
                .build();
    }
}
