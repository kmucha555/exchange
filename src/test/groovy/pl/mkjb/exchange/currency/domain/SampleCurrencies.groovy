package pl.mkjb.exchange.currency.domain

import groovy.transform.CompileStatic
import pl.mkjb.exchange.currency.dto.CurrencyDto
import pl.mkjb.exchange.currency.dto.CurrencyRateDto

@CompileStatic
trait SampleCurrencies {
    CurrencyDto billingCurrency = createCurrencyDto(1, "Polish Zloty", "PLN", BigDecimal.ONE, true)
    CurrencyDto usdCurrency = createCurrencyDto(2, "US Dollar", "USD", BigDecimal.ONE, false)
    CurrencyDto czkCurrency = createCurrencyDto(3, "Czech koruna", "CZK", BigDecimal.valueOf(100), false)

    CurrencyRateDto billingCurrencyRate =
            createCurrencyRateDto(
                    billingCurrency,
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    true)

    CurrencyRateDto UsdCurrencyRate =
            createCurrencyRateDto(
                    usdCurrency,
                    BigDecimal.valueOf(3.9598d),
                    BigDecimal.valueOf(3.9895d),
                    true)

    CurrencyRateDto CzkCurrencyRate =
            createCurrencyRateDto(
                    czkCurrency,
                    BigDecimal.valueOf(14.5678d),
                    BigDecimal.valueOf(14.7890d),
                    true)

    static private createCurrencyRateDto(CurrencyDto currencyDto,
                                         BigDecimal purchasePrice,
                                         BigDecimal sellPrice,
                                         boolean active) {

        return CurrencyRateDto.builder()
                .id(UUID.randomUUID())
                .currencyDto(currencyDto)
                .purchasePrice(purchasePrice)
                .sellPrice(sellPrice)
                .active(active)
                .build()
    }

    static private createCurrencyDto(int id,
                                     String name,
                                     String code,
                                     BigDecimal unit,
                                     boolean billingCurrency) {

        return CurrencyDto.builder()
                .id(id)
                .name(name)
                .code(code)
                .unit(unit)
                .billingCurrency(billingCurrency)
                .build()
    }
}