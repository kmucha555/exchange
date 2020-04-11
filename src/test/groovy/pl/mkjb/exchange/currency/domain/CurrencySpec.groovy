package pl.mkjb.exchange.currency.domain

import io.vavr.collection.HashSet
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException
import spock.lang.Shared
import spock.lang.Specification

class CurrencySpec extends Specification implements SampleCurrencies {
    @Shared
    CurrencyFacade facade = new CurrencyConfiguration().currencyFacade()

    def "should get billing currency"() {
        expect: "facade returns billing currency"
        facade.findBillingCurrency().get() == billingCurrencyRate
    }

    def "should find currency rate by currency rate id"() {
        expect: "facade returns currency rate for given id"
        facade.findCurrencyRateByCurrencyRateId(USD_UUID).get() == usdCurrencyRate
    }

    def "should return empty Option when currency rate not found"() {
        given: "random currency rate id"
        UUID randomUUID = UUID.randomUUID()

        expect: "facade returns empty Option"
        facade.findCurrencyRateByCurrencyRateId(randomUUID).isEmpty()
    }

    def "should returns true if currency rate is active"() {
        expect: "facade returns false when currency currency rate is active"
        !facade.isArchivedCurrencyRate(USD_UUID)
    }

    def "should throw exception when asked for currency rate that's not in the system"() {
        given: "random currency rate id"
        UUID randomUUID = UUID.randomUUID()

        when: "facade is asked for unknown currency rate"
        facade.isArchivedCurrencyRate(randomUUID)

        then:
        thrown(CurrencyNotFoundException)
    }

    def "should get newest currency rates"() {
        expect: "facade returns newest currency rates"
        facade.getNewestCurrencyRates() == HashSet.of(usdCurrencyRate, czkCurrencyRate)
    }
}
