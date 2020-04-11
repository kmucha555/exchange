package pl.mkjb.exchange.currency.domain

import io.vavr.collection.HashMap
import io.vavr.collection.Map

trait SampleCurrencies {
    Map<Integer, CurrencyEntity> currencies =
            HashMap.of(
                    1, pln,
                    2, usd,
                    3, czk
            )

    CurrencyEntity pln = CurrencyEntity.builder()
            .id(1)
            .name("Polish Zloty")
            .code("PLN")
            .unit(BigDecimal.ONE)
            .billingCurrency(true)
            .build()

    CurrencyEntity usd = CurrencyEntity.builder()
            .id(2)
            .name("US Dollar")
            .code("USD")
            .unit(BigDecimal.ONE)
            .billingCurrency(false)
            .build()

    CurrencyEntity czk = CurrencyEntity.builder()
            .id(3)
            .name("Czech koruna")
            .code("CZK")
            .unit(BigDecimal.valueOf(100))
            .billingCurrency(false)
            .build()
}