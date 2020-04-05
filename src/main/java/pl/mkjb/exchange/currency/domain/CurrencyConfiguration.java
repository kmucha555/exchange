package pl.mkjb.exchange.currency.domain;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CurrencyConfiguration {

    @Bean
    CurrencyFacade currencyFacade(CurrencyRepository currencyRepository,
                                  CurrencyRateRepository currencyRateRepository) {
        val currencyRateCreator = new CurrencyRateCreator(currencyRepository);
        return new CurrencyFacade(currencyRateCreator, currencyRateRepository);
    }
}
