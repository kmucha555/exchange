package pl.mkjb.exchange.currency.domain


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
        facade.findCurrencyRateByCurrencyRateId(usdUUID).get() == usdCurrencyRate
    }

    def "should return empty Option when currency rate not found"() {
        given: "random currency rate id"
        UUID randomUUID = UUID.randomUUID()

        expect: "facade returns empty Option"
        facade.findCurrencyRateByCurrencyRateId(randomUUID).isEmpty()
    }

    def "should returns true if currency rate is active"() {
        expect: "facade returns false when currency currency rate is active"
        !facade.isArchivedCurrencyRate(usdUUID)
    }

    def "should throw exception when asked for currency rate that's not in the system"() {
        given: "random currency rate id"
        UUID randomUUID = UUID.randomUUID()

        when: "facade is asked for unknown currency rate"
        facade.isArchivedCurrencyRate(randomUUID)

        then: "currency not found exception is thrown"
        thrown(CurrencyNotFoundException)
    }

    def "should get newest currency rates"() {
        when: "we ask for newest currency rates"
        def newestCurrencyRates = facade.getNewestCurrencyRates()

        then: "facade returns newest rates"
        newestCurrencyRates.contains(usdCurrencyRate)
        newestCurrencyRates.contains(czkCurrencyRate)
    }

    def "should not save new rates when publication date hasn't change"() {
        when: "we send rates bundle with the same publication date as exists in db"
        def rates = facade.processNewCurrencyRates(currentRates)

        then: "facade returns empty collection which means nothing has been saved to db"
        rates.isEmpty()
    }

    def "should add new rates when publication date has changed"() {
        when: "we send rates bundle with the new publication date"
        def rates = facade.processNewCurrencyRates(newRates)

        then: "facade returns empty collection contains added rates"
        rates.contains(usdCurrencyRateUpdated)
        rates.contains(czkCurrencyRateUpdated)
    }
}
