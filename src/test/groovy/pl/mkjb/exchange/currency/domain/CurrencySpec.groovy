package pl.mkjb.exchange.currency.domain

import spock.lang.Specification

class CurrencySpec extends Specification implements SampleCurrencies {
    CurrencyFacade facade = new CurrencyConfiguration().currencyFacade()

    def "FindBillingCurrency"() {
    }
}
