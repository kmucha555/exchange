package pl.mkjb.exchange.currency.domain

import groovy.transform.CompileStatic
import pl.mkjb.exchange.currency.dto.CurrencyDto
import pl.mkjb.exchange.currency.dto.CurrencyRateDto
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingBundle
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingDto

import java.time.LocalDateTime

@CompileStatic
trait SampleCurrencies {
    LocalDateTime publicationDate = LocalDateTime.of(2020, 4, 11, 14, 3, 1)
    LocalDateTime publicationDateUpdated = publicationDate.plusSeconds(30L);

    UUID plnUUID = UUID.fromString("f7086c16-87bc-4a02-9fe5-146a9f4b6bbe")
    UUID usdUUID = UUID.fromString("db64b6bc-f5e4-4eb8-9d53-5529f0691896")
    UUID czkUUID = UUID.fromString("d1d1570a-f705-41ef-98d9-fb5c1a01c71d")

    String plnName = "Polish Zloty"
    String plnCode = "PLN"
    BigDecimal plnUnit = BigDecimal.ONE

    String usdName = "US Dollar"
    String usdCode = "USD"
    BigDecimal usdUnit = BigDecimal.ONE
    BigDecimal usdPurchasePrice = BigDecimal.valueOf(4d)
    BigDecimal usdSellPrice = BigDecimal.valueOf(4.5d)
    BigDecimal usdAveragePrice = BigDecimal.valueOf(4.25d)
    BigDecimal usdPurchasePriceUpdated = BigDecimal.valueOf(5d)
    BigDecimal usdSellPriceUpdated = BigDecimal.valueOf(5.5d)
    BigDecimal usdAveragePriceUpdated = BigDecimal.valueOf(5.25d)

    String czkName = "Czech koruna"
    String czkCode = "CZK"
    BigDecimal czkUnit = 100d
    BigDecimal czkPurchasePrice = BigDecimal.valueOf(14d)
    BigDecimal czkSellPrice = BigDecimal.valueOf(14.5d)
    BigDecimal czkAveragePrice = BigDecimal.valueOf(14.25d)
    BigDecimal czkPurchasePriceUpdated = BigDecimal.valueOf(15d)
    BigDecimal czkSellPriceUpdated = BigDecimal.valueOf(15.5d)
    BigDecimal czkAveragePriceUpdated = BigDecimal.valueOf(15.25d)

    CurrencyDto billingCurrency = CurrencyDto.builder()
            .id(1)
            .name(plnName)
            .code(plnCode)
            .unit(plnUnit)
            .billingCurrency(true)
            .build()

    CurrencyRateDto billingCurrencyRate =
            CurrencyRateDto.builder()
                    .id(plnUUID)
                    .currencyDto(billingCurrency)
                    .purchasePrice(BigDecimal.ONE)
                    .sellPrice(BigDecimal.ONE)
                    .active(true)
                    .publicationDate(publicationDate)
                    .build()

    CurrencyDto usdCurrency = CurrencyDto.builder()
            .id(2)
            .name(usdName)
            .code(usdCode)
            .unit(usdUnit)
            .billingCurrency(false)
            .build()

    CurrencyRateDto UsdCurrencyRate =
            CurrencyRateDto.builder()
                    .id(usdUUID)
                    .currencyDto(usdCurrency)
                    .purchasePrice(usdPurchasePrice)
                    .sellPrice(usdSellPrice)
                    .active(true)
                    .publicationDate(publicationDate)
                    .build()

    CurrencyDto czkCurrency = CurrencyDto.builder()
            .id(3)
            .name(czkName)
            .code(czkCode)
            .unit(czkUnit)
            .billingCurrency(false)
            .build()

    CurrencyRateDto CzkCurrencyRate =
            CurrencyRateDto.builder()
                    .id(czkUUID)
                    .currencyDto(czkCurrency)
                    .purchasePrice(czkPurchasePrice)
                    .sellPrice(czkSellPrice)
                    .active(true)
                    .publicationDate(publicationDate)
                    .build()


    CurrencyFutureProcessingDto usdNewRate =
            CurrencyFutureProcessingDto.builder()
                    .name(usdName)
                    .code(usdCode)
                    .unit(usdUnit)
                    .purchasePrice(usdPurchasePriceUpdated)
                    .sellPrice(usdSellPriceUpdated)
                    .averagePrice(usdAveragePriceUpdated)
                    .build()

    CurrencyFutureProcessingDto czkNewRate =
            CurrencyFutureProcessingDto.builder()
                    .name(czkName)
                    .code(czkCode)
                    .unit(czkUnit)
                    .purchasePrice(czkPurchasePriceUpdated)
                    .sellPrice(czkSellPriceUpdated)
                    .averagePrice(czkAveragePriceUpdated)
                    .build()

    CurrencyFutureProcessingBundle currentRates =
            CurrencyFutureProcessingBundle.builder()
                    .items(Set.of(usdNewRate, czkNewRate))
                    .publicationDate(publicationDate)
                    .build()

    CurrencyFutureProcessingBundle newRates =
            CurrencyFutureProcessingBundle.builder()
                    .items(Set.of(usdNewRate, czkNewRate))
                    .publicationDate(publicationDateUpdated)
                    .build()


    CurrencyRateDto UsdCurrencyRateUpdated =
            CurrencyRateDto.builder()
                    .id(null)
                    .currencyDto(usdCurrency)
                    .purchasePrice(usdPurchasePriceUpdated)
                    .sellPrice(usdSellPriceUpdated)
                    .averagePrice(usdAveragePriceUpdated)
                    .active(true)
                    .publicationDate(publicationDateUpdated)
                    .build()

    CurrencyRateDto CzkCurrencyRateUpdated =
            CurrencyRateDto.builder()
                    .id(null)
                    .currencyDto(czkCurrency)
                    .purchasePrice(czkPurchasePriceUpdated)
                    .sellPrice(czkSellPriceUpdated)
                    .averagePrice(czkAveragePriceUpdated)
                    .active(true)
                    .publicationDate(publicationDateUpdated)
                    .build()

}