package pl.mkjb.exchange.currency.domain

import groovy.transform.CompileStatic
import pl.mkjb.exchange.currency.dto.CurrencyDto
import pl.mkjb.exchange.currency.dto.CurrencyRateDto

import java.time.LocalDateTime

@CompileStatic
trait SampleCurrencies {
    LocalDateTime publicationDate = LocalDateTime.of(2020, 4, 11, 14, 3, 1)
    UUID PLN_UUID = UUID.fromString("f7086c16-87bc-4a02-9fe5-146a9f4b6bbe")
    UUID USD_UUID = UUID.fromString("db64b6bc-f5e4-4eb8-9d53-5529f0691896")
    UUID CZK_UUID = UUID.fromString("d1d1570a-f705-41ef-98d9-fb5c1a01c71d")

    CurrencyDto billingCurrency = CurrencyDto.builder()
            .id(1)
            .name("Polish Zloty")
            .code("PLN")
            .unit(BigDecimal.ONE)
            .billingCurrency(true)
            .build()

    CurrencyDto usdCurrency = CurrencyDto.builder()
            .id(2)
            .name("US Dollar")
            .code("USD")
            .unit(BigDecimal.ONE)
            .billingCurrency(false)
            .build()

    CurrencyDto czkCurrency = CurrencyDto.builder()
            .id(3)
            .name("Czech koruna")
            .code("CZK")
            .unit(BigDecimal.valueOf(100))
            .billingCurrency(false)
            .build()

    CurrencyRateDto billingCurrencyRate =
            CurrencyRateDto.builder()
                    .id(PLN_UUID)
                    .currencyDto(billingCurrency)
                    .purchasePrice(BigDecimal.ONE)
                    .sellPrice(BigDecimal.ONE)
                    .active(true)
                    .publicationDate(publicationDate)
                    .build()

    CurrencyRateDto UsdCurrencyRate =
            CurrencyRateDto.builder()
                    .id(USD_UUID)
                    .currencyDto(usdCurrency)
                    .purchasePrice(BigDecimal.valueOf(3.9598d))
                    .sellPrice(BigDecimal.valueOf(3.9895d))
                    .active(true)
                    .publicationDate(publicationDate)
                    .build()

    CurrencyRateDto CzkCurrencyRate =
            CurrencyRateDto.builder()
                    .id(CZK_UUID)
                    .currencyDto(czkCurrency)
                    .purchasePrice(BigDecimal.valueOf(14.5678d))
                    .sellPrice(BigDecimal.valueOf(14.7890d))
                    .active(true)
                    .publicationDate(publicationDate)
                    .build()
}