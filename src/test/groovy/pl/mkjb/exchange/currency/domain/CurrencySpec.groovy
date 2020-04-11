package pl.mkjb.exchange.currency.domain


import spock.lang.Specification

class CurrencySpec extends Specification implements SampleCurrenciesDto {
    CurrencyFacade facade = new CurrencyConfiguration().currencyFacade()

    def "should get billing currency"() {
        expect: "facade return billing currency"
        facade.findBillingCurrency().get() == billingCurrencyRate
    }

    def "should find currency rate by currency rate id"() {
        given: "currency rate id"
        UUID USD_UUID = UUID.fromString("db64b6bc-f5e4-4eb8-9d53-5529f0691896")

        expect: "facade return currency rate for given id"
        facade.findCurrencyRateByCurrencyRateId(USD_UUID).get() == usdCurrencyRate
    }
}
