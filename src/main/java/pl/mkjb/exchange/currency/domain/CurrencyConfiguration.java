package pl.mkjb.exchange.currency.domain;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CurrencyConfiguration {

    CurrencyFacade currencyFacade() {
        return currencyFacade(
                new InMemoryCurrencyRepository(),
                new InMemoryCurrencyRateRepository()
        );
    }

    @Bean
    CurrencyFacade currencyFacade(CurrencyRepository currencyRepository,
                                  CurrencyRateRepository currencyRateRepository) {
        val currencyRateCreator = new CurrencyRateCreator(currencyRepository);
        return new CurrencyFacade(currencyRateCreator, currencyRepository, currencyRateRepository);
    }
}
